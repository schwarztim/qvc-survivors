package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.entity.ShockwaveEffect;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class SpilledCoffeeZone extends Weapon {
    private double damage = 0.8;
    private double zoneRadius = 2.5;
    private double zoneDuration = 3.0;
    private int zoneCount = 1;
    private final List<ShockwaveEffect> activeZones = new ArrayList<>();

    public SpilledCoffeeZone() {
        super("spilled_coffee", "Spilled Coffee Zone",
                "Throws coffee cups that create lingering damage zones",
                WeaponType.ZONE, 3.0);
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        activeZones.clear();
        for (int i = 0; i < zoneCount; i++) {
            double offsetX = (Math.random() - 0.5) * 10.0;
            double offsetY = (Math.random() - 0.5) * 10.0;
            ShockwaveEffect zone = new ShockwaveEffect(playerX + offsetX, playerY + offsetY,
                    zoneRadius * 15.0, damage);
            activeZones.add(zone);
        }
        resetCooldown();
        return new ArrayList<>();
    }

    @Override
    public void onLevelUp() {
        switch (level) {
            case 2 -> { damage = 1.1; zoneRadius = 2.8; baseCooldown = 2.7; }
            case 3 -> { damage = 1.4; zoneCount = 2; baseCooldown = 2.5; }
            case 4 -> { damage = 1.7; zoneRadius = 3.2; baseCooldown = 2.2; }
            case 5 -> { damage = 2.0; zoneCount = 3; zoneRadius = 3.5; baseCooldown = 2.0; }
        }
    }

    public List<ShockwaveEffect> getActiveZones() { return activeZones; }
    public void clearZones() { activeZones.clear(); }
    public double getDamage() { return damage; }
    public double getZoneRadius() { return zoneRadius; }
}
