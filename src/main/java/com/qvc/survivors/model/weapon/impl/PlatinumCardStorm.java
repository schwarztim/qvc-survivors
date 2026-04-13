package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class PlatinumCardStorm extends Weapon {
    private double damage = 2.0;
    private int cardCount = 20;
    private static final double SPEED = 75.0;

    public PlatinumCardStorm() {
        super("evo_platinum_barrage", "Platinum Card Storm",
                "20+ piercing cards in all directions with auto-targeting",
                WeaponType.PROJECTILE, 0.5);
        this.maxLevel = 1;
        this.level = 1;
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        List<Projectile> result = new ArrayList<>();
        for (int i = 0; i < cardCount; i++) {
            double angle = (Math.PI * 2.0 / cardCount) * i;
            double vx = Math.cos(angle) * SPEED;
            double vy = Math.sin(angle) * SPEED;
            result.add(pool.obtainPackage(playerX, playerY, vx, vy, damage));
        }
        resetCooldown();
        return result;
    }

    @Override
    public void onLevelUp() {
        // Evolved weapons don't level up further
    }

    public double getDamage() { return damage; }
    public int getCardCount() { return cardCount; }
}
