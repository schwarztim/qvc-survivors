package com.qvc.survivors.audio;

import com.qvc.survivors.world.ZoneType;

public class ProceduralTrack {
    private final String name;
    private final int bpm;
    private final String key;
    private final double[] melody;
    private final double[] bassline;
    private final String melodyWaveform;
    private final String bassWaveform;
    private final boolean hasDrums;
    private final double melodyVolume;
    private final double bassVolume;
    private final double drumVolume;

    public ProceduralTrack(String name, int bpm, String key,
                            double[] melody, double[] bassline,
                            String melodyWaveform, String bassWaveform,
                            boolean hasDrums,
                            double melodyVolume, double bassVolume, double drumVolume) {
        this.name = name;
        this.bpm = bpm;
        this.key = key;
        this.melody = melody;
        this.bassline = bassline;
        this.melodyWaveform = melodyWaveform;
        this.bassWaveform = bassWaveform;
        this.hasDrums = hasDrums;
        this.melodyVolume = melodyVolume;
        this.bassVolume = bassVolume;
        this.drumVolume = drumVolume;
    }

    public String getName() { return name; }
    public int getBpm() { return bpm; }
    public String getKey() { return key; }
    public double[] getMelody() { return melody; }
    public double[] getBassline() { return bassline; }
    public String getMelodyWaveform() { return melodyWaveform; }
    public String getBassWaveform() { return bassWaveform; }
    public boolean hasDrums() { return hasDrums; }
    public double getMelodyVolume() { return melodyVolume; }
    public double getBassVolume() { return bassVolume; }
    public double getDrumVolume() { return drumVolume; }

    // Note frequencies for reference:
    // C4=261.63 D4=293.66 E4=329.63 F4=349.23 G4=392.00 A4=440.00 B4=493.88
    // C5=523.25 D5=587.33 E5=659.25 F5=698.46 G5=783.99 A5=880.00 B5=987.77
    // C3=130.81 D3=146.83 E3=164.81 F3=174.61 G3=196.00 A3=220.00 B3=246.94

    public static ProceduralTrack forZone(ZoneType zone) {
        return switch (zone) {
            case SOUNDSTAGE -> soundstageTrack();
            case WAREHOUSE -> warehouseTrack();
            case MALL -> mallTrack();
            case RETURNS -> returnsTrack();
            case CORPORATE -> corporateTrack();
        };
    }

    public static ProceduralTrack soundstageTrack() {
        // Upbeat chiptune, C major, BPM 120
        return new ProceduralTrack("QVC Soundstage", 120, "C",
            new double[]{
                523.25, 587.33, 659.25, 783.99, 659.25, 587.33, 523.25, 392.00,
                440.00, 523.25, 587.33, 659.25, 783.99, 880.00, 783.99, 659.25
            },
            new double[]{
                130.81, 130.81, 196.00, 196.00, 174.61, 174.61, 130.81, 130.81
            },
            "square", "triangle", true, 0.25, 0.3, 0.2);
    }

    public static ProceduralTrack warehouseTrack() {
        // Industrial, Dm, BPM 100
        return new ProceduralTrack("Warehouse", 100, "Dm",
            new double[]{
                293.66, 349.23, 293.66, 261.63, 349.23, 392.00, 349.23, 293.66,
                261.63, 293.66, 349.23, 261.63, 233.08, 261.63, 293.66, 261.63
            },
            new double[]{
                73.42, 73.42, 87.31, 73.42, 65.41, 65.41, 73.42, 73.42
            },
            "sawtooth", "square", true, 0.2, 0.35, 0.3);
    }

    public static ProceduralTrack mallTrack() {
        // Synth-pop chaos, F major, BPM 140
        return new ProceduralTrack("Mega Mall", 140, "F",
            new double[]{
                698.46, 783.99, 880.00, 783.99, 698.46, 659.25, 587.33, 659.25,
                698.46, 880.00, 1046.50, 880.00, 783.99, 698.46, 659.25, 698.46
            },
            new double[]{
                87.31, 87.31, 110.00, 110.00, 98.00, 98.00, 87.31, 87.31
            },
            "triangle", "sine", true, 0.25, 0.25, 0.2);
    }

    public static ProceduralTrack returnsTrack() {
        // Dark/tense, Am, BPM 80
        return new ProceduralTrack("Returns Dept", 80, "Am",
            new double[]{
                440.00, 415.30, 392.00, 329.63, 349.23, 329.63, 293.66, 329.63,
                392.00, 415.30, 440.00, 493.88, 440.00, 392.00, 349.23, 329.63
            },
            new double[]{
                110.00, 110.00, 98.00, 110.00, 87.31, 87.31, 98.00, 110.00
            },
            "sine", "sawtooth", true, 0.2, 0.3, 0.15);
    }

    public static ProceduralTrack corporateTrack() {
        // Epic/dramatic, Cm, BPM 110
        return new ProceduralTrack("Corporate HQ", 110, "Cm",
            new double[]{
                523.25, 622.25, 698.46, 783.99, 622.25, 523.25, 466.16, 523.25,
                587.33, 698.46, 783.99, 932.33, 783.99, 698.46, 622.25, 523.25
            },
            new double[]{
                130.81, 130.81, 155.56, 155.56, 174.61, 174.61, 130.81, 130.81
            },
            "square", "sawtooth", true, 0.25, 0.35, 0.25);
    }

    public static ProceduralTrack bossTrack() {
        // Intense, Em, BPM 160
        return new ProceduralTrack("Boss Battle", 160, "Em",
            new double[]{
                659.25, 783.99, 659.25, 587.33, 659.25, 783.99, 880.00, 783.99,
                659.25, 587.33, 523.25, 587.33, 659.25, 783.99, 987.77, 880.00
            },
            new double[]{
                82.41, 82.41, 98.00, 82.41, 73.42, 73.42, 82.41, 82.41
            },
            "square", "sawtooth", true, 0.3, 0.35, 0.35);
    }

    public static ProceduralTrack menuTrack() {
        // Chill, C major, BPM 90
        return new ProceduralTrack("Menu", 90, "C",
            new double[]{
                523.25, 493.88, 440.00, 392.00, 440.00, 493.88, 523.25, 587.33,
                659.25, 587.33, 523.25, 493.88, 440.00, 523.25, 587.33, 523.25
            },
            new double[]{
                130.81, 130.81, 164.81, 164.81, 174.61, 174.61, 130.81, 130.81
            },
            "sine", "sine", false, 0.2, 0.2, 0.0);
    }

    public static ProceduralTrack metaShopTrack() {
        // Lofi shopping, G major, BPM 85
        return new ProceduralTrack("Meta Shop", 85, "G",
            new double[]{
                392.00, 440.00, 493.88, 523.25, 493.88, 440.00, 392.00, 349.23,
                392.00, 493.88, 587.33, 523.25, 493.88, 440.00, 392.00, 440.00
            },
            new double[]{
                98.00, 98.00, 123.47, 123.47, 110.00, 110.00, 98.00, 98.00
            },
            "triangle", "sine", false, 0.2, 0.2, 0.0);
    }
}
