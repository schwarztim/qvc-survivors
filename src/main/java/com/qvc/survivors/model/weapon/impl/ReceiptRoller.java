package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class ReceiptRoller extends Weapon {
    private double damage = 1.0;
    private int receiptCount = 1;
    private static final double SPEED = 35.0;

    public ReceiptRoller() {
        super("receipt_roller", "Receipt Roller",
                "Slow bouncing receipt that passes through enemies",
                WeaponType.PROJECTILE, 3.0);
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        List<Projectile> result = new ArrayList<>();
        for (int i = 0; i < receiptCount; i++) {
            double angle = facingAngle + (i - receiptCount / 2.0) * 0.5;
            double vx = Math.cos(angle) * SPEED;
            double vy = Math.sin(angle) * SPEED;
            result.add(pool.obtainPackage(playerX, playerY, vx, vy, damage));
        }
        resetCooldown();
        return result;
    }

    @Override
    public void onLevelUp() {
        switch (level) {
            case 2 -> { damage = 1.4; baseCooldown = 2.7; }
            case 3 -> { damage = 1.8; receiptCount = 2; baseCooldown = 2.5; }
            case 4 -> { damage = 2.2; baseCooldown = 2.2; }
            case 5 -> { damage = 2.8; receiptCount = 3; baseCooldown = 2.0; }
        }
    }

    public double getDamage() { return damage; }
    public int getReceiptCount() { return receiptCount; }
}
