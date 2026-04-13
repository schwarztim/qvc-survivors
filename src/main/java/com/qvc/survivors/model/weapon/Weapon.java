package com.qvc.survivors.model.weapon;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.List;

public abstract class Weapon {
    protected final String id;
    protected final String name;
    protected final String description;
    protected final WeaponType type;
    protected int level;
    protected int maxLevel = 5;
    protected double cooldownTimer;
    protected double baseCooldown;

    protected Weapon(String id, String name, String description, WeaponType type, double baseCooldown) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.baseCooldown = baseCooldown;
        this.cooldownTimer = 0.0;
        this.level = 1;
    }

    public abstract List<Projectile> fire(double playerX, double playerY,
                                           double facingAngle,
                                           List<Enemy> enemies,
                                           EntityPoolManager pool);

    public abstract void onLevelUp();

    public void update(double deltaTime) {
        if (cooldownTimer > 0.0) {
            cooldownTimer -= deltaTime;
        }
    }

    public boolean isReady() {
        return cooldownTimer <= 0.0;
    }

    public void resetCooldown() {
        cooldownTimer = baseCooldown;
    }

    public void levelUp() {
        if (level < maxLevel) {
            level++;
            onLevelUp();
        }
    }

    public boolean isMaxLevel() {
        return level >= maxLevel;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public WeaponType getType() { return type; }
    public int getLevel() { return level; }
    public int getMaxLevel() { return maxLevel; }
    public double getBaseCooldown() { return baseCooldown; }
}
