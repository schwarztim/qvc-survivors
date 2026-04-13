package com.qvc.survivors.view;

import com.qvc.survivors.engine.Camera;
import com.qvc.survivors.model.entity.BossEnemy;
import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Player;
import com.qvc.survivors.model.entity.TreasureChest;
import com.qvc.survivors.world.MapCollectible;
import com.qvc.survivors.world.TileMap;
import com.qvc.survivors.world.Zone;
import com.qvc.survivors.world.ZoneType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class MinimapView {
    private static final int MAP_W = 150;
    private static final int MAP_H = 112;
    private static final int WORLD_W = 400;
    private static final int WORLD_H = 300;
    private static final double SCALE_X = (double) MAP_W / WORLD_W;
    private static final double SCALE_Y = (double) MAP_H / WORLD_H;
    private static final int FOG_COLS = 40;
    private static final int FOG_ROWS = 30;
    private static final double FOG_CELL_W = (double) WORLD_W / FOG_COLS;
    private static final double FOG_CELL_H = (double) WORLD_H / FOG_ROWS;

    private final boolean[][] explored = new boolean[FOG_COLS][FOG_ROWS];

    public void updateExplored(double playerX, double playerY) {
        int cellX = (int)(playerX / FOG_CELL_W);
        int cellY = (int)(playerY / FOG_CELL_H);
        int radius = 1;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int cx = cellX + dx;
                int cy = cellY + dy;
                if (cx >= 0 && cx < FOG_COLS && cy >= 0 && cy < FOG_ROWS) {
                    explored[cx][cy] = true;
                }
            }
        }
    }

    public void render(GraphicsContext gc, double screenWidth, double screenHeight,
                       Camera camera, Player player, List<Enemy> enemies,
                       TileMap tileMap, List<MapCollectible> collectibles,
                       List<TreasureChest> treasureChests) {
        double mapX = screenWidth - MAP_W - 10;
        double mapY = screenHeight - MAP_H - 10;

        // Background
        gc.setFill(Color.rgb(10, 10, 15, 0.75));
        gc.fillRect(mapX, mapY, MAP_W, MAP_H);

        // Zone boundaries
        if (tileMap != null) {
            for (Zone zone : tileMap.getZones()) {
                ZoneType type = zone.getType();
                Color accent = type.getAccentColor();
                gc.setFill(Color.rgb(
                    (int)(accent.getRed() * 255),
                    (int)(accent.getGreen() * 255),
                    (int)(accent.getBlue() * 255), 0.15));

                double zx = mapX + zone.getStartX() * SCALE_X;
                double zy = mapY + zone.getStartY() * SCALE_Y;
                double zw = zone.getWidth() * SCALE_X;
                double zh = zone.getHeight() * SCALE_Y;
                gc.fillRect(zx, zy, zw, zh);
            }
        }

        // Fog of war overlay -- draw dark rects over unexplored cells
        for (int fx = 0; fx < FOG_COLS; fx++) {
            for (int fy = 0; fy < FOG_ROWS; fy++) {
                if (!explored[fx][fy]) {
                    double rx = mapX + fx * ((double) MAP_W / FOG_COLS);
                    double ry = mapY + fy * ((double) MAP_H / FOG_ROWS);
                    double rw = (double) MAP_W / FOG_COLS + 0.5;
                    double rh = (double) MAP_H / FOG_ROWS + 0.5;
                    gc.setFill(Color.rgb(5, 5, 10, 0.9));
                    gc.fillRect(rx, ry, rw, rh);
                }
            }
        }

        // Map collectibles (green dots)
        gc.setFill(Color.LIME);
        for (MapCollectible mc : collectibles) {
            if (mc.isAvailable() && isExplored(mc.getX(), mc.getY())) {
                double dx = mapX + mc.getX() * SCALE_X;
                double dy = mapY + mc.getY() * SCALE_Y;
                gc.fillRect(dx, dy, 2, 2);
            }
        }

        // Treasure chests (yellow dots)
        gc.setFill(Color.YELLOW);
        for (TreasureChest chest : treasureChests) {
            if (chest.isActive() && !chest.isCollected() && isExplored(chest.getX(), chest.getY())) {
                double dx = mapX + chest.getX() * SCALE_X;
                double dy = mapY + chest.getY() * SCALE_Y;
                gc.fillRect(dx, dy, 2, 2);
            }
        }

        // Enemies (red dots) -- only within 50 tiles of player
        double px = player.getX();
        double py = player.getY();
        for (Enemy enemy : enemies) {
            if (!enemy.isActive()) continue;
            double ex = enemy.getX();
            double ey = enemy.getY();
            double dist = Math.abs(ex - px) + Math.abs(ey - py);
            if (dist > 50) continue;
            if (!isExplored(ex, ey)) continue;

            if (enemy instanceof BossEnemy) {
                gc.setFill(Color.ORANGE);
                double dx = mapX + ex * SCALE_X - 1;
                double dy = mapY + ey * SCALE_Y - 1;
                gc.fillRect(dx, dy, 3, 3);
            } else {
                gc.setFill(Color.RED);
                double dx = mapX + ex * SCALE_X;
                double dy = mapY + ey * SCALE_Y;
                gc.fillRect(dx, dy, 1, 1);
            }
        }

        // Player (white dot)
        gc.setFill(Color.WHITE);
        double pdx = mapX + px * SCALE_X - 1;
        double pdy = mapY + py * SCALE_Y - 1;
        gc.fillRect(pdx, pdy, 2, 2);

        // Border
        gc.setStroke(Color.rgb(100, 200, 255, 0.5));
        gc.setLineWidth(1.0);
        gc.strokeRect(mapX, mapY, MAP_W, MAP_H);
    }

    private boolean isExplored(double worldX, double worldY) {
        int cellX = (int)(worldX / FOG_CELL_W);
        int cellY = (int)(worldY / FOG_CELL_H);
        if (cellX < 0 || cellX >= FOG_COLS || cellY < 0 || cellY >= FOG_ROWS) return false;
        return explored[cellX][cellY];
    }
}
