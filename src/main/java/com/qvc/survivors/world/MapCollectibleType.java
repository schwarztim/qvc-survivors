package com.qvc.survivors.world;

import javafx.scene.paint.Color;

public enum MapCollectibleType {
    OVERSTOCK_CRATE("Overstock Crate", Color.rgb(200, 150, 50), "Spawns bonus money around you", Rarity.COMMON),
    COFFEE_MUG("Coffee Mug", Color.rgb(139, 90, 43), "Boosts fire rate and speed for 10s", Rarity.COMMON),
    MYSTERY_SAMPLE("Mystery Sample", Color.rgb(180, 50, 200), "Random beneficial effect", Rarity.UNCOMMON),
    EMPLOYEE_DISCOUNT("Employee Discount", Color.rgb(50, 200, 100), "Doubles next level-up stat values", Rarity.UNCOMMON),
    FLOOR_MODEL("Floor Model", Color.rgb(100, 200, 255), "Instant level up", Rarity.RARE),
    GIFT_CARD("Gift Card", Color.rgb(255, 215, 0), "Adds $25 instantly", Rarity.COMMON),
    RECALL_NOTICE("Recall Notice", Color.rgb(255, 80, 80), "Damages all active enemies", Rarity.UNCOMMON),

    EMERGENCY_BROADCAST("Emergency Broadcast", Color.rgb(255, 255, 255), "Kills all enemies on screen", Rarity.RARE),
    COMMERCIAL_BREAK("Commercial Break", Color.rgb(100, 100, 255), "Freezes all enemies for 10 seconds", Rarity.RARE),
    WAREHOUSE_SWEEP("Warehouse Sweep", Color.rgb(200, 255, 200), "Collects all XP gems on screen", Rarity.UNCOMMON);

    public enum Rarity { COMMON, UNCOMMON, RARE }

    private final String displayName;
    private final Color color;
    private final String description;
    private final Rarity rarity;

    MapCollectibleType(String displayName, Color color, String description, Rarity rarity) {
        this.displayName = displayName;
        this.color = color;
        this.description = description;
        this.rarity = rarity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public Rarity getRarity() {
        return rarity;
    }
}
