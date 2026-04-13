package com.qvc.survivors.view;

import com.qvc.survivors.config.GameSettings;
import javafx.scene.paint.Color;

public class SettingsView {
    private static final Color OVERLAY_COLOR = Color.rgb(0, 0, 0, 0.85);
    private static final Color BOX_COLOR = Color.rgb(25, 25, 35);
    private static final Color BORDER_COLOR = Color.rgb(100, 200, 255);
    private static final Color TEXT_COLOR = Color.rgb(220, 230, 255);
    private static final Color ACCENT_COLOR = Color.rgb(100, 255, 200);
    private static final Color DIM_COLOR = Color.rgb(120, 120, 140);

    private static final int[][] RESOLUTIONS = {
        {1200, 750}, {1280, 720}, {1600, 900}, {1920, 1080}
    };

    public static final int ROW_COUNT = 9;

    private final GameView gameView;

    public SettingsView(GameView gameView) {
        this.gameView = gameView;
    }

    public void render(GameSettings settings, int selectedRow) {
        double canvasWidth = this.gameView.getWidth();
        double canvasHeight = this.gameView.getHeight();

        this.gameView.drawBox(0.0, 0.0, canvasWidth, canvasHeight, Color.TRANSPARENT, OVERLAY_COLOR);

        double boxWidth = 550.0;
        double boxHeight = 420.0;
        double boxX = (canvasWidth - boxWidth) / 2.0;
        double boxY = (canvasHeight - boxHeight) / 2.0;

        this.gameView.drawBox(boxX + 2.0, boxY + 2.0, boxWidth, boxHeight, Color.TRANSPARENT, Color.rgb(100, 200, 255, 0.2));
        this.gameView.drawBox(boxX, boxY, boxWidth, boxHeight, BORDER_COLOR, BOX_COLOR);

        double centerX = canvasWidth / 2.0;
        double y = boxY + 40.0;

        this.gameView.drawText("=== SETTINGS ===", centerX - 120.0, y, ACCENT_COLOR, 24);
        y += 40.0;

        String[] labels = new String[ROW_COUNT];
        labels[0] = "Resolution: " + settings.getWindowWidth() + "x" + settings.getWindowHeight() + " < >";
        labels[1] = "Fullscreen: " + (settings.isFullscreen() ? "ON" : "OFF") + " < >";
        labels[2] = "Music: " + volumeBar(settings.getMusicVolume()) + " " + pct(settings.getMusicVolume()) + " < >";
        labels[3] = "SFX: " + volumeBar(settings.getSfxVolume()) + " " + pct(settings.getSfxVolume()) + " < >";
        labels[4] = "Music Enabled: " + (settings.isMusicEnabled() ? "ON" : "OFF") + " < >";
        labels[5] = "SFX Enabled: " + (settings.isSfxEnabled() ? "ON" : "OFF") + " < >";
        labels[6] = "Show FPS: " + (settings.isShowFPS() ? "ON" : "OFF") + " < >";
        labels[7] = "Damage Numbers: " + (settings.isDamageNumbers() ? "ON" : "OFF") + " < >";
        labels[8] = "Screen Shake: " + (settings.isScreenShake() ? "ON" : "OFF") + " < >";

        for (int i = 0; i < ROW_COUNT; i++) {
            Color rowColor = (i == selectedRow) ? ACCENT_COLOR : TEXT_COLOR;
            String prefix = (i == selectedRow) ? "> " : "  ";
            this.gameView.drawText(prefix + labels[i], boxX + 30.0, y, rowColor, 16);
            y += 34.0;
        }

        y += 10.0;
        this.gameView.drawText("ESC/SPACE to close", centerX - 90.0, y, DIM_COLOR, 14);
    }

    private static String volumeBar(double v) {
        int filled = (int) Math.round(v * 10);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(i < filled ? '\u2588' : '\u2591');
        }
        return sb.toString();
    }

    private static String pct(double v) {
        return ((int) Math.round(v * 100)) + "%";
    }

    public static int findResolutionIndex(int w, int h) {
        for (int i = 0; i < RESOLUTIONS.length; i++) {
            if (RESOLUTIONS[i][0] == w && RESOLUTIONS[i][1] == h) return i;
        }
        return 0;
    }

    public static int[] getResolution(int index) {
        int clamped = Math.max(0, Math.min(RESOLUTIONS.length - 1, index));
        return RESOLUTIONS[clamped];
    }

    public static int getResolutionCount() {
        return RESOLUTIONS.length;
    }
}
