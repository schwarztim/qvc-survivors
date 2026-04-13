package com.qvc.survivors.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenerator {
    private static final Random RANDOM = new Random(42);

    public TileMap generate(int width, int height) {
        TileMap map = new TileMap(width, height);

        // Create zones
        Zone soundstage = new Zone(ZoneType.SOUNDSTAGE, 140, 100, 120, 100);
        Zone warehouse = new Zone(ZoneType.WAREHOUSE, 150, 10, 100, 80);
        Zone mall = new Zone(ZoneType.MALL, 270, 80, 80, 100);
        Zone returns = new Zone(ZoneType.RETURNS, 150, 210, 100, 80);
        Zone corporate = new Zone(ZoneType.CORPORATE, 50, 100, 80, 80);

        map.addZone(soundstage);
        map.addZone(warehouse);
        map.addZone(mall);
        map.addZone(returns);
        map.addZone(corporate);

        // Fill zone tiles
        for (Zone zone : map.getZones()) {
            fillZone(map, zone);
        }

        // Connecting corridors (20 tiles wide)
        // Soundstage <-> Warehouse (north)
        fillCorridor(map, 190, 90, 20, 10, TileType.STUDIO_FLOOR);
        // Soundstage <-> Mall (east)
        fillCorridor(map, 260, 130, 10, 20, TileType.STUDIO_FLOOR);
        // Soundstage <-> Returns (south)
        fillCorridor(map, 190, 200, 20, 10, TileType.STUDIO_FLOOR);
        // Soundstage <-> Corporate (west)
        fillCorridor(map, 130, 130, 10, 20, TileType.STUDIO_FLOOR);

        // Zone decorations
        decorateSoundstage(map, soundstage);
        decorateWarehouse(map, warehouse);
        decorateMall(map, mall);
        decorateReturns(map, returns);
        decorateCorporate(map, corporate);

        return map;
    }

    public List<MapCollectible> generateCollectibles(TileMap map) {
        List<MapCollectible> collectibles = new ArrayList<>();
        for (Zone zone : map.getZones()) {
            collectibles.addAll(placeCollectiblesInZone(zone));
        }
        return collectibles;
    }

    private void fillZone(TileMap map, Zone zone) {
        TileType type = zone.getTileType();
        for (int x = zone.getStartX(); x < zone.getStartX() + zone.getWidth(); x++) {
            for (int y = zone.getStartY(); y < zone.getStartY() + zone.getHeight(); y++) {
                map.setTile(x, y, type);
            }
        }
    }

    private void fillCorridor(TileMap map, int startX, int startY, int w, int h, TileType type) {
        for (int x = startX; x < startX + w; x++) {
            for (int y = startY; y < startY + h; y++) {
                map.setTile(x, y, type);
            }
        }
    }

    private void decorateSoundstage(TileMap map, Zone zone) {
        // Scattered camera equipment: small wall clusters of 2-3 tiles
        int sx = zone.getStartX();
        int sy = zone.getStartY();
        int[][] clusters = {
            {sx + 15, sy + 12}, {sx + 16, sy + 12},
            {sx + 40, sy + 25}, {sx + 40, sy + 26}, {sx + 41, sy + 25},
            {sx + 80, sy + 15}, {sx + 81, sy + 15},
            {sx + 60, sy + 70}, {sx + 60, sy + 71}, {sx + 61, sy + 70},
            {sx + 25, sy + 55}, {sx + 26, sy + 55},
            {sx + 95, sy + 50}, {sx + 95, sy + 51}, {sx + 96, sy + 50},
            {sx + 55, sy + 40}, {sx + 56, sy + 40},
        };
        for (int[] pos : clusters) {
            map.setTile(pos[0], pos[1], TileType.WALL);
        }
    }

    private void decorateWarehouse(TileMap map, Zone zone) {
        // Shelving rows: horizontal wall lines with gaps
        int sx = zone.getStartX();
        int sy = zone.getStartY();
        for (int row = 0; row < 4; row++) {
            int y = sy + 15 + row * 15;
            for (int x = sx + 5; x < sx + zone.getWidth() - 5; x++) {
                // Leave gaps every 12 tiles for aisles
                if ((x - sx) % 12 != 0 && (x - sx) % 12 != 1) {
                    map.setTile(x, y, TileType.WALL);
                }
            }
        }
    }

    private void decorateMall(TileMap map, Zone zone) {
        int sx = zone.getStartX();
        int sy = zone.getStartY();
        int cx = sx + zone.getWidth() / 2;
        int cy = sy + zone.getHeight() / 2;

        // Fountain in center: small circle of walls
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                if (dx * dx + dy * dy >= 6 && dx * dx + dy * dy <= 10) {
                    map.setTile(cx + dx, cy + dy, TileType.WALL);
                }
            }
        }

        // Storefront walls on edges (top and bottom rows)
        for (int x = sx + 5; x < sx + zone.getWidth() - 5; x++) {
            if ((x - sx) % 10 < 7) {
                map.setTile(x, sy + 3, TileType.WALL);
                map.setTile(x, sy + zone.getHeight() - 4, TileType.WALL);
            }
        }
    }

    private void decorateReturns(TileMap map, Zone zone) {
        // Cubicle maze: grid-like wall pattern with openings
        int sx = zone.getStartX();
        int sy = zone.getStartY();
        for (int gx = 0; gx < 5; gx++) {
            for (int gy = 0; gy < 4; gy++) {
                int bx = sx + 10 + gx * 18;
                int by = sy + 10 + gy * 16;
                // Draw cubicle walls (3 sides, open on alternating side)
                for (int i = 0; i < 8; i++) {
                    map.setTile(bx + i, by, TileType.WALL);       // top
                    map.setTile(bx + i, by + 6, TileType.WALL);   // bottom
                }
                // Side walls with door gap
                for (int i = 1; i < 6; i++) {
                    if (i != 3) { // door gap
                        map.setTile(bx, by + i, TileType.WALL);
                    }
                    if (i != 3) {
                        map.setTile(bx + 7, by + i, TileType.WALL);
                    }
                }
            }
        }
    }

    private void decorateCorporate(TileMap map, Zone zone) {
        // Meeting rooms: rectangular wall enclosures with door gaps
        int sx = zone.getStartX();
        int sy = zone.getStartY();
        int[][] rooms = {
            {sx + 8, sy + 8, 20, 14},
            {sx + 8, sy + 50, 20, 14},
            {sx + 50, sy + 8, 22, 14},
            {sx + 50, sy + 50, 22, 14},
        };
        for (int[] room : rooms) {
            int rx = room[0];
            int ry = room[1];
            int rw = room[2];
            int rh = room[3];
            // Top/bottom walls
            for (int x = rx; x < rx + rw; x++) {
                map.setTile(x, ry, TileType.WALL);
                map.setTile(x, ry + rh - 1, TileType.WALL);
            }
            // Side walls with door gap in the middle
            for (int y = ry + 1; y < ry + rh - 1; y++) {
                int midY = ry + rh / 2;
                if (y != midY && y != midY + 1) {
                    map.setTile(rx, y, TileType.WALL);
                    map.setTile(rx + rw - 1, y, TileType.WALL);
                }
            }
        }
    }

    private List<MapCollectible> placeCollectiblesInZone(Zone zone) {
        List<MapCollectible> list = new ArrayList<>();
        int sx = zone.getStartX();
        int sy = zone.getStartY();
        int w = zone.getWidth();
        int h = zone.getHeight();

        switch (zone.getType()) {
            case SOUNDSTAGE:
                list.add(new MapCollectible(sx + w / 2.0, sy + h / 2.0, MapCollectibleType.COFFEE_MUG));
                list.add(new MapCollectible(sx + 10, sy + 10, MapCollectibleType.OVERSTOCK_CRATE));
                list.add(new MapCollectible(sx + w - 10, sy + h - 10, MapCollectibleType.GIFT_CARD));
                list.add(new MapCollectible(sx + w - 10, sy + 10, MapCollectibleType.MYSTERY_SAMPLE));
                break;
            case WAREHOUSE:
                list.add(new MapCollectible(sx + w / 2.0, sy + 5, MapCollectibleType.OVERSTOCK_CRATE));
                list.add(new MapCollectible(sx + 10, sy + h - 10, MapCollectibleType.FLOOR_MODEL));
                list.add(new MapCollectible(sx + w - 10, sy + h / 2.0, MapCollectibleType.GIFT_CARD));
                list.add(new MapCollectible(sx + w / 2.0, sy + h - 5, MapCollectibleType.COFFEE_MUG));
                break;
            case MALL:
                list.add(new MapCollectible(sx + w / 2.0, sy + h / 2.0, MapCollectibleType.GIFT_CARD));
                list.add(new MapCollectible(sx + 5, sy + 10, MapCollectibleType.EMPLOYEE_DISCOUNT));
                list.add(new MapCollectible(sx + w - 5, sy + h - 10, MapCollectibleType.MYSTERY_SAMPLE));
                list.add(new MapCollectible(sx + w / 2.0, sy + 10, MapCollectibleType.OVERSTOCK_CRATE));
                break;
            case RETURNS:
                list.add(new MapCollectible(sx + w / 2.0, sy + h / 2.0, MapCollectibleType.RECALL_NOTICE));
                list.add(new MapCollectible(sx + 15, sy + 15, MapCollectibleType.COFFEE_MUG));
                list.add(new MapCollectible(sx + w - 15, sy + h - 15, MapCollectibleType.GIFT_CARD));
                list.add(new MapCollectible(sx + 15, sy + h - 15, MapCollectibleType.MYSTERY_SAMPLE));
                break;
            case CORPORATE:
                list.add(new MapCollectible(sx + w / 2.0, sy + h / 2.0, MapCollectibleType.EMPLOYEE_DISCOUNT));
                list.add(new MapCollectible(sx + 5, sy + 5, MapCollectibleType.FLOOR_MODEL));
                list.add(new MapCollectible(sx + w - 5, sy + h - 5, MapCollectibleType.RECALL_NOTICE));
                list.add(new MapCollectible(sx + w / 2.0, sy + 5, MapCollectibleType.GIFT_CARD));
                break;
        }
        return list;
    }
}
