package com.qvc.survivors.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class GameSettings {
    private int windowWidth = 1200;
    private int windowHeight = 750;
    private boolean fullscreen = false;
    private double musicVolume = 0.5;
    private double sfxVolume = 0.5;
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;
    private boolean showFPS = false;
    private boolean damageNumbers = true;
    private boolean screenShake = true;
    private String controlScheme = "WASD";

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"windowWidth\": ").append(windowWidth).append(",\n");
        sb.append("  \"windowHeight\": ").append(windowHeight).append(",\n");
        sb.append("  \"fullscreen\": ").append(fullscreen).append(",\n");
        sb.append("  \"musicVolume\": ").append(musicVolume).append(",\n");
        sb.append("  \"sfxVolume\": ").append(sfxVolume).append(",\n");
        sb.append("  \"musicEnabled\": ").append(musicEnabled).append(",\n");
        sb.append("  \"sfxEnabled\": ").append(sfxEnabled).append(",\n");
        sb.append("  \"showFPS\": ").append(showFPS).append(",\n");
        sb.append("  \"damageNumbers\": ").append(damageNumbers).append(",\n");
        sb.append("  \"screenShake\": ").append(screenShake).append(",\n");
        sb.append("  \"controlScheme\": \"").append(controlScheme).append("\"\n");
        sb.append("}");
        return sb.toString();
    }

    public static GameSettings fromJson(String json) {
        GameSettings s = new GameSettings();
        Map<String, String> pairs = parseJsonPairs(json);
        if (pairs.containsKey("windowWidth")) s.windowWidth = Integer.parseInt(pairs.get("windowWidth"));
        if (pairs.containsKey("windowHeight")) s.windowHeight = Integer.parseInt(pairs.get("windowHeight"));
        if (pairs.containsKey("fullscreen")) s.fullscreen = Boolean.parseBoolean(pairs.get("fullscreen"));
        if (pairs.containsKey("musicVolume")) s.musicVolume = Double.parseDouble(pairs.get("musicVolume"));
        if (pairs.containsKey("sfxVolume")) s.sfxVolume = Double.parseDouble(pairs.get("sfxVolume"));
        if (pairs.containsKey("musicEnabled")) s.musicEnabled = Boolean.parseBoolean(pairs.get("musicEnabled"));
        if (pairs.containsKey("sfxEnabled")) s.sfxEnabled = Boolean.parseBoolean(pairs.get("sfxEnabled"));
        if (pairs.containsKey("showFPS")) s.showFPS = Boolean.parseBoolean(pairs.get("showFPS"));
        if (pairs.containsKey("damageNumbers")) s.damageNumbers = Boolean.parseBoolean(pairs.get("damageNumbers"));
        if (pairs.containsKey("screenShake")) s.screenShake = Boolean.parseBoolean(pairs.get("screenShake"));
        if (pairs.containsKey("controlScheme")) s.controlScheme = pairs.get("controlScheme");
        return s;
    }

    private static Map<String, String> parseJsonPairs(String json) {
        Map<String, String> pairs = new LinkedHashMap<>();
        String stripped = json.replaceAll("[{}]", "").trim();
        String[] lines = stripped.split(",");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            int colonIdx = line.indexOf(':');
            if (colonIdx < 0) continue;
            String key = line.substring(0, colonIdx).trim().replace("\"", "");
            String value = line.substring(colonIdx + 1).trim().replace("\"", "");
            pairs.put(key, value);
        }
        return pairs;
    }

    public int getWindowWidth() { return windowWidth; }
    public void setWindowWidth(int windowWidth) { this.windowWidth = windowWidth; }
    public int getWindowHeight() { return windowHeight; }
    public void setWindowHeight(int windowHeight) { this.windowHeight = windowHeight; }
    public boolean isFullscreen() { return fullscreen; }
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }
    public double getMusicVolume() { return musicVolume; }
    public void setMusicVolume(double musicVolume) { this.musicVolume = Math.max(0.0, Math.min(1.0, musicVolume)); }
    public double getSfxVolume() { return sfxVolume; }
    public void setSfxVolume(double sfxVolume) { this.sfxVolume = Math.max(0.0, Math.min(1.0, sfxVolume)); }
    public boolean isMusicEnabled() { return musicEnabled; }
    public void setMusicEnabled(boolean musicEnabled) { this.musicEnabled = musicEnabled; }
    public boolean isSfxEnabled() { return sfxEnabled; }
    public void setSfxEnabled(boolean sfxEnabled) { this.sfxEnabled = sfxEnabled; }
    public boolean isShowFPS() { return showFPS; }
    public void setShowFPS(boolean showFPS) { this.showFPS = showFPS; }
    public boolean isDamageNumbers() { return damageNumbers; }
    public void setDamageNumbers(boolean damageNumbers) { this.damageNumbers = damageNumbers; }
    public boolean isScreenShake() { return screenShake; }
    public void setScreenShake(boolean screenShake) { this.screenShake = screenShake; }
    public String getControlScheme() { return controlScheme; }
    public void setControlScheme(String controlScheme) { this.controlScheme = controlScheme; }
}
