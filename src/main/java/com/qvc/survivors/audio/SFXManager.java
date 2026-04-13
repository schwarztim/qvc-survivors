package com.qvc.survivors.audio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SFXManager {
    private static final Logger log = LoggerFactory.getLogger(SFXManager.class);

    private final ExecutorService sfxPool = Executors.newFixedThreadPool(8, r -> {
        Thread t = new Thread(r, "SFX-Playback");
        t.setDaemon(true);
        return t;
    });
    private volatile double volume = 0.5;
    private volatile boolean enabled = true;

    private final byte[] shootBuffer;
    private final byte[] hitBuffer;
    private final byte[] explosionBuffer;
    private final byte[] collectBuffer;
    private final byte[] levelUpBuffer;
    private final byte[] enemyHurtBuffer;
    private final byte[] playerHurtBuffer;
    private final byte[] evolveBuffer;
    private final byte[] bossAppearBuffer;
    private final byte[] chestOpenBuffer;
    private final byte[] gameOverBuffer;

    public SFXManager() {
        this.shootBuffer = generateShoot();
        this.hitBuffer = generateHit();
        this.explosionBuffer = generateExplosion();
        this.collectBuffer = generateCollect();
        this.levelUpBuffer = generateLevelUp();
        this.enemyHurtBuffer = generateEnemyHurt();
        this.playerHurtBuffer = generatePlayerHurt();
        this.evolveBuffer = generateEvolve();
        this.bossAppearBuffer = generateBossAppear();
        this.chestOpenBuffer = generateChestOpen();
        this.gameOverBuffer = generateGameOver();
    }

    private void playBuffer(byte[] buffer) {
        if (!enabled || buffer == null) return;
        double vol = this.volume;
        sfxPool.submit(() -> {
            try {
                SourceDataLine line = AudioSystem.getSourceDataLine(Synthesizer.FORMAT);
                line.open(Synthesizer.FORMAT, 4096);
                line.start();
                byte[] scaled = Synthesizer.applyVolume(buffer, vol);
                line.write(scaled, 0, scaled.length);
                line.drain();
                line.close();
            } catch (LineUnavailableException e) {
                log.error("Failed to play SFX", e);
            }
        });
    }

    // --- Public play methods ---

    public void playShoot() { playBuffer(shootBuffer); }
    public void playHit() { playBuffer(hitBuffer); }
    public void playExplosion() { playBuffer(explosionBuffer); }
    public void playCollect() { playBuffer(collectBuffer); }
    public void playLevelUp() { playBuffer(levelUpBuffer); }
    public void playEnemyHurt() { playBuffer(enemyHurtBuffer); }
    public void playPlayerHurt() { playBuffer(playerHurtBuffer); }
    public void playEvolve() { playBuffer(evolveBuffer); }
    public void playBossAppear() { playBuffer(bossAppearBuffer); }
    public void playChestOpen() { playBuffer(chestOpenBuffer); }
    public void playGameOver() { playBuffer(gameOverBuffer); }

    public void setVolume(double v) {
        this.volume = Math.max(0.0, Math.min(1.0, v));
    }

    public void setEnabled(boolean e) {
        this.enabled = e;
    }

    public boolean isEnabled() { return enabled; }
    public double getVolume() { return volume; }

    // --- Sound generation ---

    private byte[] generateShoot() {
        // Quick rising square wave chirp (150ms)
        int samples = (int)(Synthesizer.SAMPLE_RATE * 0.15);
        byte[] buf = new byte[samples * 2];
        for (int i = 0; i < samples; i++) {
            double time = (double) i / Synthesizer.SAMPLE_RATE;
            double freq = 400.0 + 800.0 * (time / 0.15); // 400->1200 Hz sweep
            double phase = (freq * time) % 1.0;
            double sample = Synthesizer.square(phase) * 0.3 * Math.exp(-6.0 * time);
            short val = (short)(sample * 32767);
            buf[i * 2] = (byte)(val >> 8);
            buf[i * 2 + 1] = (byte)(val & 0xFF);
        }
        return buf;
    }

    private byte[] generateHit() {
        // Medium impact: sine 400Hz + noise (80ms)
        byte[] tone = Synthesizer.generateTone(400, 0.08, 0.25, "sine", 0.001, 0.04);
        int noiseSamples = (int)(Synthesizer.SAMPLE_RATE * 0.08);
        byte[] noiseBuf = new byte[noiseSamples * 2];
        for (int i = 0; i < noiseSamples; i++) {
            double time = (double) i / Synthesizer.SAMPLE_RATE;
            double sample = Synthesizer.noise() * 0.15 * Math.exp(-20.0 * time);
            short val = (short)(sample * 32767);
            noiseBuf[i * 2] = (byte)(val >> 8);
            noiseBuf[i * 2 + 1] = (byte)(val & 0xFF);
        }
        return Synthesizer.mix(tone, noiseBuf);
    }

    private byte[] generateExplosion() {
        // Layered noise burst + low sine sweep (200ms)
        int samples = (int)(Synthesizer.SAMPLE_RATE * 0.2);
        byte[] noiseBuf = new byte[samples * 2];
        byte[] sineBuf = new byte[samples * 2];
        for (int i = 0; i < samples; i++) {
            double time = (double) i / Synthesizer.SAMPLE_RATE;
            double noiseSample = Synthesizer.noise() * 0.4 * Math.exp(-8.0 * time);
            short nv = (short)(noiseSample * 32767);
            noiseBuf[i * 2] = (byte)(nv >> 8);
            noiseBuf[i * 2 + 1] = (byte)(nv & 0xFF);

            double sineFreq = 200.0 - 150.0 * (time / 0.2);
            double sinePhase = (sineFreq * time) % 1.0;
            double sineSample = Synthesizer.sine(sinePhase) * 0.3 * Math.exp(-5.0 * time);
            short sv = (short)(sineSample * 32767);
            sineBuf[i * 2] = (byte)(sv >> 8);
            sineBuf[i * 2 + 1] = (byte)(sv & 0xFF);
        }
        return Synthesizer.mix(noiseBuf, sineBuf);
    }

    private byte[] generateCollect() {
        // Two-note rising chime (100ms)
        byte[] note1 = Synthesizer.generateTone(880, 0.05, 0.3, "sine", 0.002, 0.02);
        byte[] note2 = Synthesizer.generateTone(1174.66, 0.05, 0.3, "sine", 0.002, 0.02);
        byte[] result = new byte[note1.length + note2.length];
        System.arraycopy(note1, 0, result, 0, note1.length);
        System.arraycopy(note2, 0, result, note1.length, note2.length);
        return result;
    }

    private byte[] generateLevelUp() {
        // Ascending 4-note arpeggio (triangle wave, 300ms)
        double[] freqs = {523.25, 659.25, 783.99, 1046.50};
        byte[][] notes = new byte[4][];
        int totalLen = 0;
        for (int i = 0; i < 4; i++) {
            notes[i] = Synthesizer.generateTone(freqs[i], 0.075, 0.3, "triangle", 0.005, 0.03);
            totalLen += notes[i].length;
        }
        byte[] result = new byte[totalLen];
        int offset = 0;
        for (byte[] note : notes) {
            System.arraycopy(note, 0, result, offset, note.length);
            offset += note.length;
        }
        return result;
    }

    private byte[] generateEnemyHurt() {
        // Quick downward sweep (60ms)
        int samples = (int)(Synthesizer.SAMPLE_RATE * 0.06);
        byte[] buf = new byte[samples * 2];
        for (int i = 0; i < samples; i++) {
            double time = (double) i / Synthesizer.SAMPLE_RATE;
            double freq = 500.0 - 300.0 * (time / 0.06);
            double phase = (freq * time) % 1.0;
            double sample = Synthesizer.sine(phase) * 0.2 * Math.exp(-10.0 * time);
            short val = (short)(sample * 32767);
            buf[i * 2] = (byte)(val >> 8);
            buf[i * 2 + 1] = (byte)(val & 0xFF);
        }
        return buf;
    }

    private byte[] generatePlayerHurt() {
        // Low impact buzz (100ms)
        byte[] tone = Synthesizer.generateTone(250, 0.1, 0.25, "sawtooth", 0.001, 0.05);
        int noiseSamples = (int)(Synthesizer.SAMPLE_RATE * 0.1);
        byte[] noiseBuf = new byte[noiseSamples * 2];
        for (int i = 0; i < noiseSamples; i++) {
            double time = (double) i / Synthesizer.SAMPLE_RATE;
            double sample = Synthesizer.noise() * 0.1 * Math.exp(-12.0 * time);
            short val = (short)(sample * 32767);
            noiseBuf[i * 2] = (byte)(val >> 8);
            noiseBuf[i * 2 + 1] = (byte)(val & 0xFF);
        }
        return Synthesizer.mix(tone, noiseBuf);
    }

    private byte[] generateEvolve() {
        // Dramatic rising sweep with harmonics (500ms)
        int samples = (int)(Synthesizer.SAMPLE_RATE * 0.5);
        byte[] buf = new byte[samples * 2];
        for (int i = 0; i < samples; i++) {
            double time = (double) i / Synthesizer.SAMPLE_RATE;
            double progress = time / 0.5;
            double freq = 200.0 + 800.0 * progress;
            double phase = (freq * time) % 1.0;
            double fundamental = Synthesizer.square(phase) * 0.2;
            double harmonic2 = Synthesizer.sine((freq * 2.0 * time) % 1.0) * 0.1;
            double harmonic3 = Synthesizer.triangle((freq * 3.0 * time) % 1.0) * 0.05;
            double env = Math.sin(progress * Math.PI); // bell curve envelope
            double sample = (fundamental + harmonic2 + harmonic3) * env;
            short val = (short)(sample * 32767);
            buf[i * 2] = (byte)(val >> 8);
            buf[i * 2 + 1] = (byte)(val & 0xFF);
        }
        return buf;
    }

    private byte[] generateBossAppear() {
        // Deep rumble with rising tone (400ms)
        int samples = (int)(Synthesizer.SAMPLE_RATE * 0.4);
        byte[] rumble = new byte[samples * 2];
        byte[] tone = new byte[samples * 2];
        for (int i = 0; i < samples; i++) {
            double time = (double) i / Synthesizer.SAMPLE_RATE;
            double r = Synthesizer.noise() * 0.2 * (1.0 - time / 0.4);
            short rv = (short)(r * 32767);
            rumble[i * 2] = (byte)(rv >> 8);
            rumble[i * 2 + 1] = (byte)(rv & 0xFF);

            double freq = 80.0 + 200.0 * (time / 0.4);
            double phase = (freq * time) % 1.0;
            double t = Synthesizer.sawtooth(phase) * 0.25 * Math.min(1.0, time / 0.1);
            short tv = (short)(t * 32767);
            tone[i * 2] = (byte)(tv >> 8);
            tone[i * 2 + 1] = (byte)(tv & 0xFF);
        }
        return Synthesizer.mix(rumble, tone);
    }

    private byte[] generateChestOpen() {
        // Sparkle: 3-note ascending chime (150ms)
        double[] freqs = {659.25, 880.00, 1318.51};
        byte[][] notes = new byte[3][];
        int totalLen = 0;
        for (int i = 0; i < 3; i++) {
            notes[i] = Synthesizer.generateTone(freqs[i], 0.05, 0.3, "sine", 0.002, 0.02);
            totalLen += notes[i].length;
        }
        byte[] result = new byte[totalLen];
        int offset = 0;
        for (byte[] note : notes) {
            System.arraycopy(note, 0, result, offset, note.length);
            offset += note.length;
        }
        return result;
    }

    private byte[] generateGameOver() {
        // Descending 5-note sequence (750ms)
        double[] freqs = {392.00, 349.23, 329.63, 293.66, 261.63};
        byte[][] notes = new byte[5][];
        int totalLen = 0;
        for (int i = 0; i < 5; i++) {
            notes[i] = Synthesizer.generateTone(freqs[i], 0.15, 0.3, "sine", 0.005, 0.08);
            totalLen += notes[i].length;
        }
        byte[] result = new byte[totalLen];
        int offset = 0;
        for (byte[] note : notes) {
            System.arraycopy(note, 0, result, offset, note.length);
            offset += note.length;
        }
        return result;
    }
}
