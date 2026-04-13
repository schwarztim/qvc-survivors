package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class CountdownClock extends Weapon {
    private double freezeDuration = 2.0;
    private static final double SPEED = 50.0;

    public CountdownClock() {
        super("countdown_clock", "Countdown Clock",
                "Fires a clock projectile that freezes enemies",
                WeaponType.BEAM, 4.0);
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        List<Projectile> result = new ArrayList<>();
        Enemy nearest = findNearest(playerX, playerY, enemies);
        if (nearest == null) return result;

        double dirX = nearest.getX() - playerX;
        double dirY = nearest.getY() - playerY;
        double length = Math.sqrt(dirX * dirX + dirY * dirY);
        if (length > 0.0) {
            double vx = (dirX / length) * SPEED;
            double vy = (dirY / length) * SPEED;
            result.add(pool.obtainPackage(playerX, playerY, vx, vy, 0.1));
        }
        resetCooldown();
        return result;
    }

    @Override
    public void onLevelUp() {
        switch (level) {
            case 2 -> { freezeDuration = 3.0; baseCooldown = 3.5; }
            case 3 -> { freezeDuration = 3.5; baseCooldown = 3.0; }
            case 4 -> { freezeDuration = 4.5; baseCooldown = 2.5; }
            case 5 -> { freezeDuration = 6.0; baseCooldown = 2.0; }
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

    public double getFreezeDuration() { return freezeDuration; }
}
