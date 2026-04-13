package com.qvc.survivors.service;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoundEffectGenerator {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(SoundEffectGenerator.class);
   private static final int SAMPLE_RATE = 22050;
   private static final AudioFormat AUDIO_FORMAT = new AudioFormat(22050.0F, 8, 1, true, true);
   private static final String ERROR_AUDIO_LINE = "Failed to initialize audio line";
   private static final double TWO_PI = Math.PI * 2;
   private double masterVolume = 0.1;
   private double musicVolume = 0.1;
   private boolean sfxEnabled = true;
   private boolean musicEnabled = true;
   private Thread introMusicThread;
   private volatile boolean introMusicPlaying = false;
   private Thread metaShopMusicThread;
   private volatile boolean metaShopMusicPlaying = false;

   public void setMasterVolume(double v) {
      this.masterVolume = Math.max(0.0, Math.min(1.0, v)) * 0.2;
   }

   public void setMusicVolumeLevel(double v) {
      this.musicVolume = Math.max(0.0, Math.min(1.0, v)) * 0.2;
   }

   public void setSfxEnabled(boolean enabled) {
      this.sfxEnabled = enabled;
   }

   public void setMusicEnabled(boolean enabled) {
      this.musicEnabled = enabled;
      if (!enabled) {
         this.stopIntroMusic();
         this.stopMetaShopMusic();
      }
   }

   public void playShootSound() {
      if (!this.sfxEnabled) return;
      this.playSound(800.0, 50);
   }

   public void playHitSound() {
      if (!this.sfxEnabled) return;
      this.playSound(400.0, 80);
   }

   public void playExplosionSound() {
      if (!this.sfxEnabled) return;
      double vol = this.masterVolume;
      Thread soundThread = new Thread(() -> {
         try {
            SourceDataLine line = AudioSystem.getSourceDataLine(AUDIO_FORMAT);
            line.open(AUDIO_FORMAT);
            line.start();
            int duration = 200;
            byte[] buffer = new byte[22050 * duration / 1000];

            for (int i = 0; i < buffer.length; i++) {
               double time = i / 22050.0;
               double frequency = 200.0 - 150.0 * time;
               double angle = (Math.PI * 2) * frequency * time;
               double decay = Math.exp(-5.0 * time);
               double sample = Math.sin(angle) + 0.5 * Math.sin(2.0 * angle) + 0.25 * Math.sin(3.0 * angle);
               buffer[i] = (byte)(sample * 127.0 * decay * vol);
            }

            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
         } catch (LineUnavailableException var14) {
            log.error("Failed to initialize audio line", var14);
         }
      });
      soundThread.setDaemon(true);
      soundThread.start();
   }

   public void playCollectSound() {
      if (!this.sfxEnabled) return;
      double vol = this.masterVolume;
      Thread soundThread = new Thread(() -> {
         try {
            SourceDataLine line = AudioSystem.getSourceDataLine(AUDIO_FORMAT);
            line.open(AUDIO_FORMAT);
            line.start();
            double[] frequencies = new double[]{523.25, 659.25, 783.99};
            int noteDuration = 50;

            for (double frequency : frequencies) {
               byte[] buffer = new byte[22050 * noteDuration / 1000];

               for (int i = 0; i < buffer.length; i++) {
                  double time = i / 22050.0;
                  double angle = (Math.PI * 2) * frequency * time;
                  buffer[i] = (byte)(Math.sin(angle) * 127.0 * vol);
               }

               line.write(buffer, 0, buffer.length);
            }

            line.drain();
            line.close();
         } catch (LineUnavailableException var14) {
            log.error("Failed to initialize audio line", var14);
         }
      });
      soundThread.setDaemon(true);
      soundThread.start();
   }

   public void playLevelUpSound() {
      if (!this.sfxEnabled) return;
      double vol = this.masterVolume;
      Thread soundThread = new Thread(() -> {
         try {
            SourceDataLine line = AudioSystem.getSourceDataLine(AUDIO_FORMAT);
            line.open(AUDIO_FORMAT);
            line.start();
            double[] frequencies = new double[]{261.63, 329.63, 392.0, 523.25};
            int noteDuration = 100;

            for (double frequency : frequencies) {
               byte[] buffer = new byte[22050 * noteDuration / 1000];

               for (int i = 0; i < buffer.length; i++) {
                  double time = i / 22050.0;
                  double angle = (Math.PI * 2) * frequency * time;
                  double envelope = 1.0 - time * 1000.0 / noteDuration * 0.5;
                  buffer[i] = (byte)(Math.sin(angle) * 127.0 * envelope * vol);
               }

               line.write(buffer, 0, buffer.length);
            }

            line.drain();
            line.close();
         } catch (LineUnavailableException var16) {
            log.error("Failed to initialize audio line", var16);
         }
      });
      soundThread.setDaemon(true);
      soundThread.start();
   }

   public void playGameOverSound() {
      if (!this.sfxEnabled) return;
      double vol = this.masterVolume;
      Thread soundThread = new Thread(() -> {
         try {
            SourceDataLine line = AudioSystem.getSourceDataLine(AUDIO_FORMAT);
            line.open(AUDIO_FORMAT);
            line.start();
            double[] frequencies = new double[]{392.0, 349.23, 329.63, 293.66, 261.63};
            int noteDuration = 150;

            for (double frequency : frequencies) {
               byte[] buffer = new byte[22050 * noteDuration / 1000];

               for (int i = 0; i < buffer.length; i++) {
                  double time = i / 22050.0;
                  double angle = (Math.PI * 2) * frequency * time;
                  buffer[i] = (byte)(Math.sin(angle) * 127.0 * vol);
               }

               line.write(buffer, 0, buffer.length);
            }

            line.drain();
            line.close();
         } catch (LineUnavailableException var14) {
            log.error("Failed to initialize audio line", var14);
         }
      });
      soundThread.setDaemon(true);
      soundThread.start();
   }

   public void playEnemyHurtSound() {
      if (!this.sfxEnabled) return;
      this.playSound(300.0, 60);
   }

   public void playPlayerHurtSound() {
      if (!this.sfxEnabled) return;
      this.playSound(250.0, 100);
   }

   private void playSound(double frequency, int durationMs) {
      double vol = this.masterVolume;
      Thread soundThread = new Thread(() -> {
         try {
            SourceDataLine line = AudioSystem.getSourceDataLine(AUDIO_FORMAT);
            line.open(AUDIO_FORMAT);
            line.start();
            byte[] buffer = new byte[22050 * durationMs / 1000];

            for (int i = 0; i < buffer.length; i++) {
               double time = i / 22050.0;
               double angle = (Math.PI * 2) * frequency * time;
               buffer[i] = (byte)(Math.sin(angle) * 127.0 * vol);
            }

            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
         } catch (LineUnavailableException var10) {
            log.error("Failed to initialize audio line", var10);
         }
      });
      soundThread.setDaemon(true);
      soundThread.start();
   }

   public void startIntroMusic() {
      if (!this.musicEnabled) return;
      if (!this.introMusicPlaying) {
         this.introMusicPlaying = true;
         double mVol = this.musicVolume;
         this.introMusicThread = new Thread(
            () -> {
               try {
                  SourceDataLine line = AudioSystem.getSourceDataLine(AUDIO_FORMAT);
                  line.open(AUDIO_FORMAT);
                  line.start();
                  double[] chordProgression = new double[]{220.0, 261.63, 329.63, 246.94, 293.66, 369.99, 196.0, 246.94, 293.66, 220.0, 277.18, 329.63};
                  double[] melody = new double[]{
                     440.0, 493.88, 523.25, 587.33, 659.25, 587.33, 523.25, 493.88, 523.25, 587.33, 659.25, 698.46, 659.25, 587.33, 523.25, 440.0
                  };
                  int beatDuration = 400;

                  while (this.introMusicPlaying) {
                     for (int measure = 0; measure < 4 && this.introMusicPlaying; measure++) {
                        int chordIndex = measure * 3;
                        double bass = chordProgression[chordIndex];
                        double mid = chordProgression[chordIndex + 1];
                        double high = chordProgression[chordIndex + 2];

                        for (int beat = 0; beat < 4 && this.introMusicPlaying; beat++) {
                           int melodyIndex = (measure * 4 + beat) % melody.length;
                           double melodyNote = melody[melodyIndex];
                           byte[] buffer = new byte[22050 * beatDuration / 1000];

                           for (int i = 0; i < buffer.length; i++) {
                              double time = i / 22050.0;
                              double envelope = 1.0 - time / (beatDuration / 1000.0) * 0.3;
                              double bassWave = Math.sin((Math.PI * 2) * bass * time) * 0.3;
                              double midWave = Math.sin((Math.PI * 2) * mid * time) * 0.25;
                              double highWave = Math.sin((Math.PI * 2) * high * time) * 0.2;
                              double melodyWave = Math.sin((Math.PI * 2) * melodyNote * time) * 0.35;
                              double mixedSample = (bassWave + midWave + highWave + melodyWave) * envelope;
                              buffer[i] = (byte)(mixedSample * 127.0 * mVol);
                           }

                           line.write(buffer, 0, buffer.length);
                        }
                     }
                  }

                  line.drain();
                  line.close();
               } catch (LineUnavailableException var33) {
                  log.error("Failed to initialize audio line", var33);
               }
            }
         );
         this.introMusicThread.setDaemon(true);
         this.introMusicThread.start();
      }
   }

   public void stopIntroMusic() {
      this.introMusicPlaying = false;
      if (this.introMusicThread != null) {
         this.introMusicThread.interrupt();
         this.introMusicThread = null;
      }
   }

   public void startMetaShopMusic() {
      this.stopIntroMusic();
      if (!this.musicEnabled) return;
      if (!this.metaShopMusicPlaying) {
         this.metaShopMusicPlaying = true;
         double mVol = this.musicVolume;
         this.metaShopMusicThread = new Thread(
            () -> {
               try {
                  SourceDataLine line = AudioSystem.getSourceDataLine(AUDIO_FORMAT);
                  line.open(AUDIO_FORMAT);
                  line.start();
                  double[] chordProgression = new double[]{
                     329.63,
                     392.0,
                     493.88,
                     392.0,
                     466.16,
                     587.33,
                     349.23,
                     440.0,
                     523.25,
                     293.66,
                     369.99,
                     440.0,
                     329.63,
                     415.3,
                     493.88,
                     349.23,
                     440.0,
                     554.37,
                     392.0,
                     493.88,
                     587.33,
                     329.63,
                     415.3,
                     493.88
                  };
                  double[] melody = new double[]{
                     659.25,
                     783.99,
                     880.0,
                     987.77,
                     880.0,
                     783.99,
                     659.25,
                     783.99,
                     698.46,
                     830.61,
                     932.33,
                     1046.5,
                     932.33,
                     830.61,
                     698.46,
                     587.33,
                     659.25,
                     783.99,
                     880.0,
                     1046.5,
                     880.0,
                     783.99,
                     698.46,
                     659.25,
                     783.99,
                     932.33,
                     1046.5,
                     1174.66,
                     1046.5,
                     932.33,
                     783.99,
                     659.25
                  };
                  int beatDuration = 300;

                  while (this.metaShopMusicPlaying) {
                     for (int measure = 0; measure < 8 && this.metaShopMusicPlaying; measure++) {
                        int chordIndex = measure * 3;
                        double bass = chordProgression[chordIndex];
                        double mid = chordProgression[chordIndex + 1];
                        double high = chordProgression[chordIndex + 2];

                        for (int beat = 0; beat < 4 && this.metaShopMusicPlaying; beat++) {
                           int melodyIndex = (measure * 4 + beat) % melody.length;
                           double melodyNote = melody[melodyIndex];
                           byte[] buffer = new byte[22050 * beatDuration / 1000];

                           for (int i = 0; i < buffer.length; i++) {
                              double time = i / 22050.0;
                              double envelope = 1.0 - time / (beatDuration / 1000.0) * 0.2;
                              double bassWave = Math.sin((Math.PI * 2) * bass * time) * 0.25;
                              double midWave = Math.sin((Math.PI * 2) * mid * time) * 0.2;
                              double highWave = Math.sin((Math.PI * 2) * high * time) * 0.15;
                              double melodyWave = Math.sin((Math.PI * 2) * melodyNote * time) * 0.4;
                              double arpeggio = Math.sin((Math.PI * 2) * melodyNote * 2.0 * time) * 0.1 * Math.sin(time * 10.0);
                              double mixedSample = (bassWave + midWave + highWave + melodyWave + arpeggio) * envelope;
                              buffer[i] = (byte)(mixedSample * 127.0 * mVol);
                           }

                           line.write(buffer, 0, buffer.length);
                        }
                     }
                  }

                  line.drain();
                  line.close();
               } catch (LineUnavailableException var35) {
                  log.error("Failed to initialize audio line", var35);
               }
            }
         );
         this.metaShopMusicThread.setDaemon(true);
         this.metaShopMusicThread.start();
      }
   }

   public void stopMetaShopMusic() {
      this.metaShopMusicPlaying = false;
      if (this.metaShopMusicThread != null) {
         this.metaShopMusicThread.interrupt();
         this.metaShopMusicThread = null;
      }
   }
}
