package com.qvc.survivors.world;

public class Zone {
    private final ZoneType type;
    private final int startX;
    private final int startY;
    private final int width;
    private final int height;
    private final TileType tileType;

    public Zone(ZoneType type, int startX, int startY, int width, int height) {
        this.type = type;
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.tileType = type.getTileType();
    }

    public boolean contains(double worldX, double worldY) {
        return worldX >= startX && worldX < startX + width
            && worldY >= startY && worldY < startY + height;
    }

    public ZoneType getType() {
        return type;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TileType getTileType() {
        return tileType;
    }
}
