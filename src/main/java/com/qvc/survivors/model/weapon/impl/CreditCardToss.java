package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class CreditCardToss extends Weapon {
    private double damage = 0.5;
    private int cardCount = 3;
    private static final double SPEED = 60.0;

    public CreditCardToss() {
        super("credit_card_toss", "Credit Card Toss",
                "Throws credit cards in random directions",
                WeaponType.PROJECTILE, 1.0);
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        List<Projectile> result = new ArrayList<>();
        for (int i = 0; i < cardCount; i++) {
            double angle = Math.random() * Math.PI * 2;
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
            case 2 -> { damage = 0.65; cardCount = 5; baseCooldown = 0.9; }
            case 3 -> { damage = 0.8; cardCount = 6; baseCooldown = 0.8; }
            case 4 -> { damage = 0.95; cardCount = 8; baseCooldown = 0.7; }
            case 5 -> { damage = 1.1; cardCount = 10; baseCooldown = 0.6; }
        }
    }

    public double getDamage() { return damage; }
    public int getCardCount() { return cardCount; }
}
