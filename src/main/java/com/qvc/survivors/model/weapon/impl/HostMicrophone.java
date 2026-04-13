package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.entity.ShockwaveEffect;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class HostMicrophone extends Weapon {
    private double damage = 0.8;
    private double radius = 3.0;
    private ShockwaveEffect activeShockwave;

    public HostMicrophone() {
        super("host_microphone", "Host Microphone",
                "Emits a shockwave around you, damaging all nearby enemies",
                WeaponType.AREA, 2.0);
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
        switch (level) {
            case 2 -> { damage = 1.1; radius = 3.5; baseCooldown = 1.75; }
            case 3 -> { damage = 1.4; radius = 4.0; baseCooldown = 1.5; }
            case 4 -> { damage = 1.7; radius = 4.8; baseCooldown = 1.25; }
            case 5 -> { damage = 2.0; radius = 5.5; baseCooldown = 1.0; }
        }
    }

    public ShockwaveEffect getActiveShockwave() { return activeShockwave; }
    public void clearShockwave() { activeShockwave = null; }
    public double getDamage() { return damage; }
    public double getRadius() { return radius; }
}
