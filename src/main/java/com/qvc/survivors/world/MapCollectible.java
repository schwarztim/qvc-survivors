package com.qvc.survivors.world;

public class MapCollectible {
    private static final double RESPAWN_TIME = 120.0;

    private final double x;
    private final double y;
    private final MapCollectibleType type;
    private boolean collected;
    private double respawnTimer;

    public MapCollectible(double x, double y, MapCollectibleType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.collected = false;
        this.respawnTimer = 0.0;
    }

    public void collect() {
        this.collected = true;
        this.respawnTimer = RESPAWN_TIME;
    }

    public boolean isAvailable() {
        return !collected;
    }

    public void update(double deltaTime) {
        if (collected) {
            respawnTimer -= deltaTime;
            if (respawnTimer <= 0.0) {
                collected = false;
                respawnTimer = 0.0;
            }
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public MapCollectibleType getType() {
        return type;
    }

    public boolean isCollected() {
        return collected;
    }

    public double getRespawnTimer() {
        return respawnTimer;
    }
}
