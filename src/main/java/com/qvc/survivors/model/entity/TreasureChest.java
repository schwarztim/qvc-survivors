package com.qvc.survivors.model.entity;

public class TreasureChest extends Entity {
    private static final double SIZE = 1.5;
    private boolean collected;

    public TreasureChest(double x, double y) {
        super(x, y, SIZE, SIZE);
        this.collected = false;
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
}
