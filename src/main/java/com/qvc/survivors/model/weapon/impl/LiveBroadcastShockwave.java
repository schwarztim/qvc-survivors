package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.entity.ShockwaveEffect;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class LiveBroadcastShockwave extends Weapon {
    private double damage = 4.0;
    private double radius = 7.0;
    private double healPerHit = 0.5;
    private ShockwaveEffect activeShockwave;

    public LiveBroadcastShockwave() {
        super("evo_broadcast_nova", "Live Broadcast Shockwave",
                "Continuous pulsing aura that heals on hit",
                WeaponType.AREA, 0.6);
        this.maxLevel = 1;
        this.level = 1;
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        activeShockwave = new ShockwaveEffect(playerX, playerY, radius * 15.0, damage);
        resetCooldown();
        return new ArrayList<>();
    }

    @Override
    public void onLevelUp() {
        // Evolved weapons don't level up further
    }

    public ShockwaveEffect getActiveShockwave() { return activeShockwave; }
    public void clearShockwave() { activeShockwave = null; }
    public double getDamage() { return damage; }
    public double getRadius() { return radius; }
    public double getHealPerHit() { return healPerHit; }
}
