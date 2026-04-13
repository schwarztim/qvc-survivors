package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class PrimeDeliveryCannon extends Weapon {
    private double damage = 5.0;
    private int projectileCount = 3;
    private double velocity = 80.0;

    public PrimeDeliveryCannon() {
        super("evo_mega_package", "Prime Delivery Cannon",
                "Triple homing packages with explosive impact",
                WeaponType.PROJECTILE, 0.6);
        this.maxLevel = 1;
        this.level = 1;
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        List<Projectile> result = new ArrayList<>();
        Enemy nearest = findNearest(playerX, playerY, enemies);
        if (nearest == null) return result;

        for (int i = 0; i < projectileCount; i++) {
            double angleOffset = (i - projectileCount / 2.0) * 0.3;
            double dirX = nearest.getX() - playerX;
            double dirY = nearest.getY() - playerY;
            double length = Math.sqrt(dirX * dirX + dirY * dirY);
            if (length > 0.0) {
                double angle = Math.atan2(dirY, dirX) + angleOffset;
                double vx = Math.cos(angle) * velocity;
                double vy = Math.sin(angle) * velocity;
                result.add(pool.obtainPackage(playerX, playerY, vx, vy, damage));
            }
        }
        resetCooldown();
        return result;
    }

    @Override
    public void onLevelUp() {
        // Evolved weapons don't level up further
    }

    private Enemy findNearest(double px, double py, List<Enemy> enemies) {
        Enemy nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Enemy e : enemies) {
            if (e.isActive()) {
                double dx = e.getX() - px;
                double dy = e.getY() - py;
                double dist = dx * dx + dy * dy;
                if (dist < minDist) {
                    minDist = dist;
                    nearest = e;
                }
            }
        }
        return nearest;
    }

    public double getDamage() { return damage; }
    public double getVelocity() { return velocity; }
    public int getProjectileCount() { return projectileCount; }
}
