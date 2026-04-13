package com.qvc.survivors.world;

import javafx.scene.paint.Color;

public enum ZoneType {
    SOUNDSTAGE("The QVC Soundstage", TileType.STUDIO_FLOOR, Color.rgb(100, 50, 200), 1.0),
    WAREHOUSE("Warehouse District", TileType.WAREHOUSE_CONCRETE, Color.rgb(200, 150, 50), 1.5),
    MALL("Black Friday Mega Mall", TileType.MALL_TILE, Color.rgb(200, 50, 150), 2.0),
    RETURNS("Returns Department", TileType.RETURNS_TILE, Color.rgb(150, 100, 50), 2.5),
    CORPORATE("Corporate HQ", TileType.CORPORATE_FLOOR, Color.rgb(50, 100, 200), 3.0);

    private final String displayName;
    private final TileType tileType;
    private final Color accentColor;
    private final double difficultyMultiplier;

    ZoneType(String displayName, TileType tileType, Color accentColor, double difficultyMultiplier) {
        this.displayName = displayName;
        this.tileType = tileType;
        this.accentColor = accentColor;
        this.difficultyMultiplier = difficultyMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TileType getTileType() {
        return tileType;
    }

    public Color getAccentColor() {
        return accentColor;
    }

    public double getDifficultyMultiplier() {
        return difficultyMultiplier;
    }
}
