package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.ArcProjectile;
import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class PriceSlash extends Weapon {
    private double damage = 1.5;
    private double arcDegrees = 90.0;
    private double range = 2.0;

    public PriceSlash() {
        super("price_slash", "Price Slash",
                "Slashes an arc in front of you, hitting all enemies",
                WeaponType.MELEE, 1.2);
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        List<Projectile> result = new ArrayList<>();
        ArcProjectile arc = new ArcProjectile(playerX, playerY, facingAngle,
                arcDegrees, range * 15.0, damage);
        result.add(arc);
        resetCooldown();
        return result;
    }

    @Override
    public void onLevelUp() {
        switch (level) {
            case 2 -> { damage = 2.0; arcDegrees = 110; range = 2.3; baseCooldown = 1.1; }
            case 3 -> { damage = 2.7; arcDegrees = 130; range = 2.7; baseCooldown = 0.95; }
            case 4 -> { damage = 3.3; arcDegrees = 155; range = 3.0; baseCooldown = 0.8; }
            case 5 -> { damage = 4.0; arcDegrees = 180; range = 3.5; baseCooldown = 0.7; }
        }
    }

    public double getDamage() { return damage; }
    public double getArcDegrees() { return arcDegrees; }
    public double getRange() { return range; }
}
