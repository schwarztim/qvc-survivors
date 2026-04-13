package com.qvc.survivors.world;

import javafx.scene.paint.Color;

public enum TileType {
    STUDIO_FLOOR(Color.rgb(25, 25, 35), Color.rgb(35, 40, 55, 0.4)),
    WAREHOUSE_CONCRETE(Color.rgb(30, 28, 25), Color.rgb(50, 45, 40, 0.3)),
    MALL_TILE(Color.rgb(22, 20, 30), Color.rgb(40, 35, 50, 0.35)),
    RETURNS_TILE(Color.rgb(28, 25, 20), Color.rgb(45, 40, 35, 0.3)),
    CORPORATE_FLOOR(Color.rgb(20, 22, 28), Color.rgb(35, 38, 48, 0.35)),
    WALL(Color.rgb(50, 50, 60), Color.rgb(70, 70, 80, 0.5)),
    VOID(Color.rgb(10, 10, 12), Color.rgb(15, 15, 20, 0.2));

    private final Color baseColor;
    private final Color gridColor;

    TileType(Color baseColor, Color gridColor) {
        this.baseColor = baseColor;
        this.gridColor = gridColor;
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public Color getGridColor() {
        return gridColor;
    }
}
