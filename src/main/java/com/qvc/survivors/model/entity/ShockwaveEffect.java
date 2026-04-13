package com.qvc.survivors.model.entity;

import java.util.HashSet;
import java.util.Set;

public class ShockwaveEffect extends Entity {
    private final double maxRadius;
    private final double damage;
    private double lifetime;
    private static final double MAX_LIFETIME = 0.4;
    private double currentRadius;
    private final Set<Enemy> hitEnemies = new HashSet<>();

    public ShockwaveEffect(double x, double y, double maxRadius, double damage) {
        super(x, y, maxRadius * 2, maxRadius * 2);
        this.maxRadius = maxRadius;
        this.damage = damage;
        this.lifetime = MAX_LIFETIME;
        this.currentRadius = 0.0;
    }

    @Override
    public void update(double deltaTime) {
        lifetime -= deltaTime;
        double progress = getProgress();
        currentRadius = maxRadius * progress;
        if (lifetime <= 0.0) {
            this.active = false;
        }
    }

    public boolean isEnemyInRadius(Enemy enemy) {
        if (hitEnemies.contains(enemy)) return false;

        double dx = enemy.getX() - this.x;
        double dy = enemy.getY() - this.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist <= currentRadius;
    }

    public void markHit(Enemy enemy) {
        hitEnemies.add(enemy);
    }

    public double getProgress() {
        return 1.0 - (lifetime / MAX_LIFETIME);
    }

    public double getDamage() { return damage; }
    public double getMaxRadius() { return maxRadius; }
    public double getCurrentRadius() { return currentRadius; }
    public double getLifetime() { return lifetime; }
}
