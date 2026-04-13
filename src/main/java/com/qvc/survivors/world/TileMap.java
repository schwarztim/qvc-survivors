package com.qvc.survivors.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileMap {
    private final int width;
    private final int height;
    private final TileType[][] tiles;
    private final List<Zone> zones;

    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new TileType[width][height];
        this.zones = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = TileType.VOID;
            }
        }
    }

    public TileType getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return TileType.VOID;
        }
        return tiles[x][y];
    }

    public void setTile(int x, int y, TileType type) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = type;
        }
    }

    public Zone getZoneAt(double worldX, double worldY) {
        for (Zone zone : zones) {
            if (zone.contains(worldX, worldY)) {
                return zone;
            }
        }
        return null;
    }

    public List<Zone> getZones() {
        return Collections.unmodifiableList(zones);
    }

    public void addZone(Zone zone) {
        zones.add(zone);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
