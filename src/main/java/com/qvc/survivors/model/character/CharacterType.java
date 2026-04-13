package com.qvc.survivors.model.character;

import javafx.scene.paint.Color;

public enum CharacterType {
    THE_HOST("The Host", "Balanced QVC presenter", "package_launcher",
            Color.CYAN, 1.0, 1.0, 1.0),
    THE_WAREHOUSE_WORKER("Warehouse Worker", "Fast but fragile", "shopping_cart_stampede",
            Color.ORANGE, 0.8, 1.3, 0.8),
    THE_VIP_SHOPPER("VIP Shopper", "Premium collector", "credit_card_toss",
            Color.GOLD, 1.2, 0.9, 0.9);

    private final String name;
    private final String description;
    private final String startingWeaponId;
    private final Color color;
    private final double healthMult;
    private final double speedMult;
    private final double damageMult;

    CharacterType(String name, String description, String startingWeaponId,
                  Color color, double healthMult, double speedMult, double damageMult) {
        this.name = name;
        this.description = description;
        this.startingWeaponId = startingWeaponId;
        this.color = color;
        this.healthMult = healthMult;
        this.speedMult = speedMult;
        this.damageMult = damageMult;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStartingWeaponId() { return startingWeaponId; }
    public Color getColor() { return color; }
    public double getHealthMult() { return healthMult; }
    public double getSpeedMult() { return speedMult; }
    public double getDamageMult() { return damageMult; }
}
