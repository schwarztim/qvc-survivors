package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.entity.ShockwaveEffect;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class QVCPerfumeAura extends Weapon {
    private double damage = 0.5;
    private double radius = 2.5;
    private ShockwaveEffect activeAura;

    public QVCPerfumeAura() {
        super("perfume_aura", "QVC Perfume Aura",
                "Continuous damage ring around the player",
                WeaponType.AREA, 0.8);
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        activeAura = new ShockwaveEffect(playerX, playerY, radius * 15.0, damage);
        resetCooldown();
        return new ArrayList<>();
    }

    @Override
    public void onLevelUp() {
        switch (level) {
            case 2 -> { damage = 0.7; radius = 2.8; baseCooldown = 0.7; }
            case 3 -> { damage = 0.9; radius = 3.2; baseCooldown = 0.65; }
            case 4 -> { damage = 1.2; radius = 3.5; baseCooldown = 0.6; }
            case 5 -> { damage = 1.5; radius = 4.0; baseCooldown = 0.5; }
        }
    }

    public ShockwaveEffect getActiveAura() { return activeAura; }
    public void clearAura() { activeAura = null; }
    public double getDamage() { return damage; }
    public double getRadius() { return radius; }
}
