package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.BoomerangProjectile;
import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class BlackFridayStampede extends Weapon {
    private double damage = 6.0;
    private int cartCount = 3;
    private int maxPierce = 20;
    private double maxRange = 20.0;
    private static final double SPEED = 70.0;

    public BlackFridayStampede() {
        super("evo_express_cart", "Black Friday Stampede",
                "Multiple high-speed carts bounce across the screen",
                WeaponType.PROJECTILE, 1.0);
        this.maxLevel = 1;
        this.level = 1;
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        List<Projectile> result = new ArrayList<>();
        for (int i = 0; i < cartCount; i++) {
            double angle = facingAngle + (i - cartCount / 2.0) * 0.8;
            double vx = Math.cos(angle) * SPEED;
            double vy = Math.sin(angle) * SPEED;
            result.add(new BoomerangProjectile(playerX, playerY, vx, vy, damage,
                    maxRange * 15.0, maxPierce));
        }
        resetCooldown();
        return result;
    }

    @Override
    public void onLevelUp() {
        // Evolved weapons don't level up further
    }

    public double getDamage() { return damage; }
    public int getCartCount() { return cartCount; }
}
