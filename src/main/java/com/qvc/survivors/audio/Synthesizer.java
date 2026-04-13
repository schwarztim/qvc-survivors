package com.qvc.survivors.audio;

import javax.sound.sampled.AudioFormat;

public class Synthesizer {
    public static final int SAMPLE_RATE = 44100;
    public static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);

    public static double sine(double phase) { return Math.sin(phase * 2 * Math.PI); }
    public static double square(double phase) { return phase < 0.5 ? 1.0 : -1.0; }
    public static double triangle(double phase) { return 2.0 * Math.abs(2.0 * (phase % 1.0) - 1.0) - 1.0; }
    public static double sawtooth(double phase) { return 2.0 * (phase % 1.0) - 1.0; }
    public static double noise() { return Math.random() * 2.0 - 1.0; }

    public static double waveform(String type, double phase) {
        return switch (type) {
            case "square" -> square(phase);
            case "triangle" -> triangle(phase);
            case "sawtooth" -> sawtooth(phase);
            case "noise" -> noise();
            default -> sine(phase);
        };
    }

    public static byte[] generateTone(double frequency, double duration, double volume,
                                       String waveformType, double attack, double decay) {
        int numSamples = (int)(SAMPLE_RATE * duration);
        byte[] buffer = new byte[numSamples * 2]; // 16-bit
        for (int i = 0; i < numSamples; i++) {
            double time = (double) i / SAMPLE_RATE;
            double phase = (frequency * time) % 1.0;
            double sample = waveform(waveformType, phase);

            // Envelope
            double env = 1.0;
            if (time < attack && attack > 0) {
                env = time / attack;
            } else if (time > duration - decay && decay > 0) {
                env = (duration - time) / decay;
            }
            env = Math.max(0.0, Math.min(1.0, env));

            short val = (short)(sample * env * volume * 32767);
            buffer[i * 2] = (byte)(val >> 8);
            buffer[i * 2 + 1] = (byte)(val & 0xFF);
        }
        return buffer;
    }

    public static byte[] mix(byte[]... buffers) {
        int maxLen = 0;
        for (byte[] b : buffers) {
            if (b.length > maxLen) maxLen = b.length;
        }
        byte[] result = new byte[maxLen];
        for (int i = 0; i < maxLen; i += 2) {
            double sum = 0;
            int count = 0;
            for (byte[] b : buffers) {
                if (i + 1 < b.length) {
                    short val = (short)((b[i] << 8) | (b[i + 1] & 0xFF));
                    sum += val;
                    count++;
                }
            }
            if (count > 0) {
                short mixed = (short) Math.max(-32768, Math.min(32767, sum));
                result[i] = (byte)(mixed >> 8);
                if (i + 1 < maxLen) result[i + 1] = (byte)(mixed & 0xFF);
            }
        }
        return result;
    }

    public static byte[] generateKick(double volume) {
        double duration = 0.15;
        int numSamples = (int)(SAMPLE_RATE * duration);
        byte[] buffer = new byte[numSamples * 2];
        for (int i = 0; i < numSamples; i++) {
            double time = (double) i / SAMPLE_RATE;
            double freq = 150.0 * Math.exp(-15.0 * time);
            double phase = 0;
            // Accumulate phase for pitch-sweeping kick
            for (int j = 0; j <= i; j++) {
                phase += 150.0 * Math.exp(-15.0 * ((double) j / SAMPLE_RATE)) / SAMPLE_RATE;
            }
            double sample = Math.sin(phase * 2 * Math.PI) * Math.exp(-8.0 * time);
            short val = (short)(sample * volume * 32767);
            buffer[i * 2] = (byte)(val >> 8);
            buffer[i * 2 + 1] = (byte)(val & 0xFF);
        }
        return buffer;
    }

    public static byte[] generateHiHat(double volume) {
        double duration = 0.05;
        int numSamples = (int)(SAMPLE_RATE * duration);
        byte[] buffer = new byte[numSamples * 2];
        for (int i = 0; i < numSamples; i++) {
            double time = (double) i / SAMPLE_RATE;
            double sample = noise() * Math.exp(-30.0 * time);
            short val = (short)(sample * volume * 32767);
            buffer[i * 2] = (byte)(val >> 8);
            buffer[i * 2 + 1] = (byte)(val & 0xFF);
        }
        return buffer;
    }

    public static byte[] generateSnare(double volume) {
        double duration = 0.12;
        int numSamples = (int)(SAMPLE_RATE * duration);
        byte[] buffer = new byte[numSamples * 2];
        for (int i = 0; i < numSamples; i++) {
            double time = (double) i / SAMPLE_RATE;
            double tonePhase = (200.0 * time) % 1.0;
            double tone = sine(tonePhase) * 0.5;
            double noiseComp = noise() * 0.7;
            double sample = (tone + noiseComp) * Math.exp(-12.0 * time);
            short val = (short)(sample * volume * 32767);
            buffer[i * 2] = (byte)(val >> 8);
            buffer[i * 2 + 1] = (byte)(val & 0xFF);
        }
        return buffer;
    }

    public static byte[] applyVolume(byte[] buffer, double volume) {
        byte[] result = new byte[buffer.length];
        for (int i = 0; i < buffer.length - 1; i += 2) {
            short val = (short)((buffer[i] << 8) | (buffer[i + 1] & 0xFF));
            val = (short)(val * volume);
            result[i] = (byte)(val >> 8);
            result[i + 1] = (byte)(val & 0xFF);
        }
        return result;
    }

    public static byte[] silence(double duration) {
        return new byte[(int)(SAMPLE_RATE * duration) * 2];
    }
}
