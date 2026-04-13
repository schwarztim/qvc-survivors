package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.ArcProjectile;
import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class ClearanceAnnihilation extends Weapon {
    private double damage = 8.0;
    private double range = 5.0;

    public ClearanceAnnihilation() {
        super("evo_clearance_storm", "Clearance Annihilation",
                "360-degree devastating slash that passes through all enemies",
                WeaponType.MELEE, 0.6);
        this.maxLevel = 1;
        this.level = 1;
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        List<Projectile> result = new ArrayList<>();
        ArcProjectile arc = new ArcProjectile(playerX, playerY, facingAngle,
                360.0, range * 15.0, damage);
        result.add(arc);
        resetCooldown();
        return result;
    }

    @Override
    public void onLevelUp() {
        // Evolved weapons don't level up further
    }

    public double getDamage() { return damage; }
    public double getRange() { return range; }
}
