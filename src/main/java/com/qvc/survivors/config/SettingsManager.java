package com.qvc.survivors.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsManager {
    private static final Logger log = LoggerFactory.getLogger(SettingsManager.class);
    private static final String SAVE_DIR = System.getProperty("user.home") + "/.qvc-survivors/";
    private static final String SETTINGS_FILE = "settings.json";

    public GameSettings load() {
        try {
            Path path = Path.of(SAVE_DIR, SETTINGS_FILE);
            if (Files.exists(path)) {
                String json = Files.readString(path, StandardCharsets.UTF_8);
                log.info("Settings loaded from {}", path);
                return GameSettings.fromJson(json);
            }
        } catch (Exception e) {
            log.error("Failed to load settings", e);
        }
        log.info("Using default settings");
        return new GameSettings();
    }

    public void save(GameSettings settings) {
        try {
            Path dir = Path.of(SAVE_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path path = dir.resolve(SETTINGS_FILE);
            Files.writeString(path, settings.toJson(), StandardCharsets.UTF_8);
            log.info("Settings saved to {}", path);
        } catch (IOException e) {
            log.error("Failed to save settings", e);
        }
    }
}
