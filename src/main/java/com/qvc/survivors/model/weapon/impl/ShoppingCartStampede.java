package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.BoomerangProjectile;
import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartStampede extends Weapon {
    private double damage = 1.2;
    private int maxPierce = 3;
    private double maxRange = 12.0;
    private static final double SPEED = 55.0;

    public ShoppingCartStampede() {
        super("shopping_cart", "Shopping Cart Stampede",
                "Fires a piercing boomerang cart that returns to you",
                WeaponType.PROJECTILE, 2.5);
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        List<Projectile> result = new ArrayList<>();
        Enemy nearest = findNearest(playerX, playerY, enemies);
        double angle = nearest != null
                ? Math.atan2(nearest.getY() - playerY, nearest.getX() - playerX)
                : facingAngle;

        double vx = Math.cos(angle) * SPEED;
        double vy = Math.sin(angle) * SPEED;
        result.add(new BoomerangProjectile(playerX, playerY, vx, vy, damage,
                maxRange * 15.0, maxPierce));
        resetCooldown();
        return result;
    }

    @Override
    public void onLevelUp() {
        switch (level) {
            case 2 -> { damage = 1.6; maxPierce = 5; baseCooldown = 2.2; }
            case 3 -> { damage = 2.0; maxPierce = 7; maxRange = 14.0; baseCooldown = 1.9; }
            case 4 -> { damage = 2.5; maxPierce = 10; baseCooldown = 1.6; }
            case 5 -> { damage = 3.0; maxPierce = 12; maxRange = 16.0; baseCooldown = 1.4; }
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
    public int getMaxPierce() { return maxPierce; }
}
