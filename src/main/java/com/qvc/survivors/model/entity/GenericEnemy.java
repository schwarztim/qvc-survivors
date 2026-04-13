package com.qvc.survivors.model.entity;

import com.qvc.survivors.model.behavior.*;

public class GenericEnemy extends Enemy {
    private EnemyType enemyType;
    private EnemyBehavior behavior;
    private double behaviorTimer;
    private int behaviorState;
    private double shieldHealth;
    private boolean shieldBroken;
    private double teleportTimer;
    private double spawnTimer;

    public GenericEnemy(double x, double y, EnemyType type) {
        super(x, y, type.getSize(), type.getSize(), type.getHp(), type.getSpeed(), type.getDamage(), type.getMoneyDrop());
        this.enemyType = type;
        this.behavior = createBehavior(type.getBehaviorId());
        this.behaviorTimer = 0.0;
        this.behaviorState = 0;
        this.shieldHealth = type == EnemyType.RETURN_FRAUDSTER ? 10.0 : 0.0;
        this.shieldBroken = false;
        this.teleportTimer = 0.0;
        this.spawnTimer = 0.0;
    }

    private static EnemyBehavior createBehavior(String behaviorId) {
        return switch (behaviorId) {
            case "chase" -> new ChaseBehavior();
            case "charge" -> new ChargeBehavior();
            case "swarm" -> new SwarmBehavior();
            case "circle" -> new CircleBehavior();
            case "ranged" -> new RangedBehavior();
            case "teleport" -> new TeleportBehavior();
            case "stationary" -> new StationaryBehavior();
            default -> new ChaseBehavior();
        };
    }

    public void updateBehavior(double targetX, double targetY, double deltaTime) {
        // Return Fraudster shield regen
        if (enemyType == EnemyType.RETURN_FRAUDSTER && !shieldBroken && shieldHealth < 10.0) {
            shieldHealth = Math.min(10.0, shieldHealth + 2.0 * deltaTime);
        }

        // Mystery Box spawn timer
        if (enemyType == EnemyType.MYSTERY_BOX) {
            spawnTimer += deltaTime;
        }

        behavior.update(this, targetX, targetY, deltaTime);
    }

    @Override
    public void takeDamage(double damage) {
        if (enemyType == EnemyType.RETURN_FRAUDSTER && !shieldBroken && shieldHealth > 0) {
            shieldHealth -= damage;
            this.damageFlashTimer = 0.15;
            if (shieldHealth <= 0) {
                shieldBroken = true;
                shieldHealth = 0;
            }
            return;
        }
        super.takeDamage(damage);
    }

    public void reset(double x, double y, EnemyType type) {
        super.reset(x, y);
        this.enemyType = type;
        this.width = type.getSize();
        this.height = type.getSize();
        this.healthComponent.reset(type.getHp());
        this.movementComponent.reset(type.getSpeed());
        this.damageComponent.setDamage(type.getDamage());
        this.damageFlashTimer = 0.0;
        this.behavior = createBehavior(type.getBehaviorId());
        this.behaviorTimer = 0.0;
        this.behaviorState = 0;
        this.shieldHealth = type == EnemyType.RETURN_FRAUDSTER ? 10.0 : 0.0;
        this.shieldBroken = false;
        this.teleportTimer = 0.0;
        this.spawnTimer = 0.0;
    }

    @Override
    public void reset(double x, double y) {
        super.reset(x, y);
        if (this.enemyType != null) {
            this.healthComponent.reset(enemyType.getHp());
            this.movementComponent.reset(enemyType.getSpeed());
            this.damageComponent.setDamage(enemyType.getDamage());
        }
        this.damageFlashTimer = 0.0;
        this.behaviorTimer = 0.0;
        this.behaviorState = 0;
    }

    public EnemyType getEnemyType() { return enemyType; }
    public EnemyBehavior getBehavior() { return behavior; }
    public double getBehaviorTimer() { return behaviorTimer; }
    public int getBehaviorState() { return behaviorState; }
    public double getShieldHealth() { return shieldHealth; }
    public boolean isShieldBroken() { return shieldBroken; }
    public double getTeleportTimer() { return teleportTimer; }
    public double getSpawnTimer() { return spawnTimer; }
    public void resetSpawnTimer() { this.spawnTimer = 0.0; }
}
