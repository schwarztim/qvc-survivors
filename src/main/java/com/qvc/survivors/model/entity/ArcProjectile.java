package com.qvc.survivors.model.entity;

import java.util.HashSet;
import java.util.Set;

public class ArcProjectile extends Projectile {
    private static final double SIZE = 0.5;
    private final double arcAngle;
    private final double range;
    private final double facingAngle;
    private double lifetime;
    private static final double MAX_LIFETIME = 0.15;
    private final Set<Enemy> hitEnemies = new HashSet<>();

    public ArcProjectile(double x, double y, double facingAngle, double arcAngle,
                          double range, double damage) {
        super(x, y, SIZE, SIZE, 0.0, 0.0, damage);
        this.facingAngle = facingAngle;
        this.arcAngle = arcAngle;
        this.range = range;
        this.lifetime = MAX_LIFETIME;
    }

    @Override
    public void update(double deltaTime) {
        lifetime -= deltaTime;
        if (lifetime <= 0.0) {
            this.active = false;
        }
    }

    public boolean isEnemyInArc(Enemy enemy) {
        if (hitEnemies.contains(enemy)) return false;

        double dx = enemy.getX() - this.x;
        double dy = enemy.getY() - this.y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > range) return false;

        double angleToEnemy = Math.atan2(dy, dx);
        double angleDiff = normalizeAngle(angleToEnemy - facingAngle);
        double halfArc = Math.toRadians(arcAngle) / 2.0;

        return Math.abs(angleDiff) <= halfArc;
    }

    public void markHit(Enemy enemy) {
        hitEnemies.add(enemy);
    }

    private double normalizeAngle(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }

    public double getArcAngle() { return arcAngle; }
    public double getRange() { return range; }
    public double getFacingAngle() { return facingAngle; }
    public double getLifetime() { return lifetime; }
    public double getProgress() { return 1.0 - (lifetime / MAX_LIFETIME); }
}
