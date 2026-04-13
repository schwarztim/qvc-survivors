package com.qvc.survivors.model.entity;

public class TreasureChest extends Entity {
    public static final int TIER_BRONZE = 0;
    public static final int TIER_SILVER = 1;
    public static final int TIER_GOLD = 2;

    private static final double SIZE = 1.5;
    private boolean collected;
    private int tier;

    public TreasureChest(double x, double y) {
        this(x, y, TIER_BRONZE);
    }

    public TreasureChest(double x, double y, int tier) {
        super(x, y, SIZE, SIZE);
        this.collected = false;
        this.tier = tier;
    }

    @Override
    public void update(double deltaTime) {
        // stationary
    }

    public void collect() {
        this.collected = true;
        this.active = false;
    }

    public boolean isCollected() {
        return collected;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }
}
