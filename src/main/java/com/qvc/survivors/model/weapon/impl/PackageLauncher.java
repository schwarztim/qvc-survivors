package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class PackageLauncher extends Weapon {
    private double damage = 1.0;
    private int projectileCount = 1;
    private double velocity = 65.0;

    public PackageLauncher() {
        super("package_launcher", "Package Launcher",
                "Fires QVC packages at the nearest enemy",
                WeaponType.PROJECTILE, 1.0);
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        List<Projectile> result = new ArrayList<>();
        Enemy nearest = findNearest(playerX, playerY, enemies);
        if (nearest == null) return result;

        for (int i = 0; i < projectileCount; i++) {
            double angleOffset = (i - projectileCount / 2.0) * 0.2;
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
        switch (level) {
            case 2 -> { damage = 1.4; baseCooldown = 0.9; }
            case 3 -> { damage = 1.8; projectileCount = 2; baseCooldown = 0.8; }
            case 4 -> { damage = 2.2; baseCooldown = 0.7; }
            case 5 -> { damage = 2.5; projectileCount = 3; baseCooldown = 0.65; }
        }
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
