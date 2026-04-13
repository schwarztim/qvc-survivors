package com.qvc.survivors.audio;

import com.qvc.survivors.world.ZoneType;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicManager {
    private static final Logger log = LoggerFactory.getLogger(MusicManager.class);

    private volatile ProceduralTrack currentTrack;
    private Thread musicThread;
    private volatile boolean playing;
    private volatile boolean stopping;
    private volatile double volume = 0.5;
    private volatile boolean enabled = true;

    public void playTrack(ProceduralTrack track) {
        if (track == null) return;
        if (!enabled) return;
        if (currentTrack != null && currentTrack.getName().equals(track.getName()) && playing) return;

        stop();
        currentTrack = track;
        playing = true;
        stopping = false;

        musicThread = new Thread(() -> {
            try {
                SourceDataLine line = AudioSystem.getSourceDataLine(Synthesizer.FORMAT);
                line.open(Synthesizer.FORMAT, 8192);
                line.start();

                double fadeVolume = 0.0;
                double fadeInDuration = 1.0; // seconds to fade in
                double fadeInElapsed = 0.0;

                while (playing && !stopping) {
                    byte[] buffer = generateBars(currentTrack);
                    // Apply volume and fade-in in chunks
                    int chunkSize = 4410; // ~50ms at 44100Hz, 16-bit mono = 2 bytes per sample
                    for (int offset = 0; offset < buffer.length && playing && !stopping; offset += chunkSize) {
                        int len = Math.min(chunkSize, buffer.length - offset);

                        fadeInElapsed += (double) len / (Synthesizer.SAMPLE_RATE * 2);
                        fadeVolume = Math.min(1.0, fadeInElapsed / fadeInDuration);

                        byte[] chunk = new byte[len];
                        for (int i = 0; i < len - 1; i += 2) {
                            short val = (short)((buffer[offset + i] << 8) | (buffer[offset + i + 1] & 0xFF));
                            val = (short)(val * volume * fadeVolume);
                            chunk[i] = (byte)(val >> 8);
                            chunk[i + 1] = (byte)(val & 0xFF);
                        }
                        line.write(chunk, 0, len);
                    }
                }

                // Fade out if stopping
                if (stopping) {
                    // Quick fade out on remaining buffer in line
                    line.flush();
                }

                line.drain();
                line.close();
            } catch (LineUnavailableException e) {
                log.error("Failed to open audio line for music", e);
            } catch (Exception e) {
                if (playing) log.error("Music playback error", e);
            }
        }, "MusicManager-Playback");
        musicThread.setDaemon(true);
        musicThread.start();
    }

    private byte[] generateBars(ProceduralTrack track) {
        double beatDuration = 60.0 / track.getBpm();
        // Generate 2 bars (8 beats typically)
        int beatsPerBar = 4;
        int bars = 2;
        int totalBeats = beatsPerBar * bars;

        double totalDuration = totalBeats * beatDuration;
        int totalSamples = (int)(Synthesizer.SAMPLE_RATE * totalDuration);
        byte[] melodyBuf = new byte[totalSamples * 2];
        byte[] bassBuf = new byte[totalSamples * 2];
        byte[] drumBuf = new byte[totalSamples * 2];

        double[] melody = track.getMelody();
        double[] bassline = track.getBassline();

        // Generate melody
        for (int beat = 0; beat < totalBeats; beat++) {
            int melodyIdx = beat % melody.length;
            double freq = melody[melodyIdx];
            byte[] note = Synthesizer.generateTone(freq, beatDuration * 0.85, track.getMelodyVolume(),
                    track.getMelodyWaveform(), 0.01, beatDuration * 0.2);
            int offset = (int)(beat * beatDuration * Synthesizer.SAMPLE_RATE) * 2;
            System.arraycopy(note, 0, melodyBuf, offset, Math.min(note.length, melodyBuf.length - offset));
        }

        // Generate bassline
        for (int beat = 0; beat < totalBeats; beat++) {
            int bassIdx = beat % bassline.length;
            double freq = bassline[bassIdx];
            byte[] note = Synthesizer.generateTone(freq, beatDuration * 0.9, track.getBassVolume(),
                    track.getBassWaveform(), 0.005, beatDuration * 0.3);
            int offset = (int)(beat * beatDuration * Synthesizer.SAMPLE_RATE) * 2;
            System.arraycopy(note, 0, bassBuf, offset, Math.min(note.length, bassBuf.length - offset));
        }

        // Generate drums
        if (track.hasDrums()) {
            for (int beat = 0; beat < totalBeats; beat++) {
                int offset = (int)(beat * beatDuration * Synthesizer.SAMPLE_RATE) * 2;
                byte[] drum;
                if (beat % 4 == 0) {
                    drum = Synthesizer.generateKick(track.getDrumVolume());
                } else if (beat % 4 == 2) {
                    drum = Synthesizer.generateSnare(track.getDrumVolume());
                } else {
                    drum = Synthesizer.generateHiHat(track.getDrumVolume() * 0.6);
                }
                if (offset + drum.length <= drumBuf.length) {
                    System.arraycopy(drum, 0, drumBuf, offset, drum.length);
                }
            }
        }

        return Synthesizer.mix(melodyBuf, bassBuf, drumBuf);
    }

    public void stop() {
        stopping = true;
        playing = false;
        if (musicThread != null) {
            musicThread.interrupt();
            try {
                musicThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            musicThread = null;
        }
        currentTrack = null;
    }

    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            stop();
        }
    }

    public boolean isEnabled() { return enabled; }
    public double getVolume() { return volume; }

    public void playZoneMusic(ZoneType zone) {
        if (zone == null) return;
        playTrack(ProceduralTrack.forZone(zone));
    }

    public void playBossMusic() {
        playTrack(ProceduralTrack.bossTrack());
    }

    public void playMenuMusic() {
        playTrack(ProceduralTrack.menuTrack());
    }

    public void playMetaShopMusic() {
        playTrack(ProceduralTrack.metaShopTrack());
    }
}
