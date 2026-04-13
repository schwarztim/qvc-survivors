package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class ThirtyDayReturnShield extends Weapon {
    private double shieldCapacity = 30.0;
    private double currentShield;
    private double rechargeRate = 5.0;

    public ThirtyDayReturnShield() {
        super("return_shield", "30-Day Return Shield",
                "Rechargeable damage-absorbing shield around the player",
                WeaponType.ZONE, 0.0);
        this.currentShield = shieldCapacity;
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        // Shield is passive - no projectiles
        return new ArrayList<>();
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (currentShield < shieldCapacity) {
            currentShield = Math.min(shieldCapacity, currentShield + rechargeRate * deltaTime);
        }
    }

    public double absorbDamage(double damage) {
        if (currentShield <= 0) return damage;
        double absorbed = Math.min(currentShield, damage);
        currentShield -= absorbed;
        return damage - absorbed;
    }

    @Override
    public void onLevelUp() {
        switch (level) {
            case 2 -> { shieldCapacity = 40.0; rechargeRate = 6.0; }
            case 3 -> { shieldCapacity = 55.0; rechargeRate = 7.0; }
            case 4 -> { shieldCapacity = 70.0; rechargeRate = 9.0; }
            case 5 -> { shieldCapacity = 90.0; rechargeRate = 12.0; }
        }
        currentShield = shieldCapacity;
    }

    public double getShieldCapacity() { return shieldCapacity; }
    public double getCurrentShield() { return currentShield; }
    public double getShieldPercent() { return currentShield / shieldCapacity; }
}
