package com.qvc.survivors.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.qvc.survivors.engine.Camera;

public class DamageNumberPool {
    private static final int MAX_NUMBERS = 50;
    private final DamageNumber[] pool;

    public DamageNumberPool() {
        pool = new DamageNumber[MAX_NUMBERS];
        for (int i = 0; i < MAX_NUMBERS; i++) {
            pool[i] = new DamageNumber();
        }
    }

    public void spawn(double worldX, double worldY, double damage, Color color) {
        for (DamageNumber dn : pool) {
            if (!dn.isActive()) {
                dn.init(worldX, worldY, damage, color);
                return;
            }
        }
        // Pool full -- recycle oldest (first in array)
        pool[0].init(worldX, worldY, damage, color);
    }

    public void update(double deltaTime) {
        for (DamageNumber dn : pool) {
            if (dn.isActive()) {
                dn.update(deltaTime);
            }
        }
    }

    public void render(GraphicsContext gc, Camera camera) {
        for (DamageNumber dn : pool) {
            if (!dn.isActive()) continue;
            double screenX = camera.worldToScreenX(dn.getX());
            double screenY = camera.worldToScreenY(dn.getY());
            double alpha = Math.max(0.0, Math.min(1.0, dn.getLifetime()));
            String text;
            if (dn.getDamage() >= 100) {
                text = String.valueOf((int) dn.getDamage());
            } else {
                text = String.format("%.0f", dn.getDamage());
            }

            Color baseColor = dn.getColor();
            Color fadedColor = Color.rgb(
                (int)(baseColor.getRed() * 255),
                (int)(baseColor.getGreen() * 255),
                (int)(baseColor.getBlue() * 255),
                alpha
            );

            double fontSize = dn.getDamage() >= 50 ? 16 : 12;
            gc.setFont(Font.font("Courier New", FontWeight.BOLD, fontSize));
            // Shadow
            gc.setFill(Color.rgb(0, 0, 0, alpha * 0.5));
            gc.fillText(text, screenX + 1, screenY + 1);
            // Main text
            gc.setFill(fadedColor);
            gc.fillText(text, screenX, screenY);
        }
    }
}
