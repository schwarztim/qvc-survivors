package com.qvc.survivors.model.entity;

import java.util.List;

public class ReturnFraudKingpin extends BossEnemy {
    private static final double HP = 300.0;
    private static final double SPEED = 10.0;
    private static final double DAMAGE = 18.0;
    private static final double SIZE = 4.5;
    private static final double SHIELD_MAX = 10.0;
    private static final double SHIELD_REGEN = 2.0;
    private static final double WEAPON_DISABLE_INTERVAL = 10.0;
    private static final double WEAPON_DISABLE_DURATION = 5.0;

    private double shieldHealth = SHIELD_MAX;
    private boolean shieldBroken = false;
    private double weaponDisableTimer = 0.0;
    private boolean weaponDisableActive = false;
    private double weaponDisableDuration = 0.0;
    private boolean pendingDecoySpawn = false;

    public ReturnFraudKingpin(double x, double y) {
        super(x, y, SIZE, SIZE, HP, SPEED, DAMAGE, 125, "Return Fraud Kingpin");
    }

    @Override
    public void updatePhase(double deltaTime, double playerX, double playerY, List<Enemy> enemies) {
        double healthPercent = healthComponent.getHealthPercentage();
        if (healthPercent <= 0.5 && currentPhase == 0) {
            transitionToPhase(1);
        }

        moveTowards(playerX, playerY);

        // Shield regen
        if (!shieldBroken && shieldHealth < SHIELD_MAX) {
            shieldHealth = Math.min(SHIELD_MAX, shieldHealth + SHIELD_REGEN * deltaTime);
        }

        // Weapon disable
        pendingDecoySpawn = false;
        weaponDisableTimer += deltaTime;
        if (weaponDisableActive) {
            weaponDisableDuration -= deltaTime;
            if (weaponDisableDuration <= 0) {
                weaponDisableActive = false;
            }
        } else if (weaponDisableTimer >= WEAPON_DISABLE_INTERVAL) {
            weaponDisableActive = true;
            weaponDisableDuration = WEAPON_DISABLE_DURATION;
            weaponDisableTimer = 0.0;
        }

        // Phase 2: spawn decoys
        if (currentPhase == 1 && phaseTimer < 0.1) {
            pendingDecoySpawn = true;
        }
    }

    @Override
    public void takeDamage(double damage) {
        if (!shieldBroken && shieldHealth > 0) {
            shieldHealth -= damage;
            this.damageFlashTimer = 0.15;
            if (shieldHealth <= 0) {
                double overflow = -shieldHealth;
                shieldHealth = 0;
                shieldBroken = true;
                if (overflow > 0) {
                    super.takeDamage(overflow);
                }
            }
            return;
        }
        super.takeDamage(damage);
        // Shield regenerates after being broken
        if (shieldBroken && healthComponent.isAlive()) {
            shieldBroken = false;
        }
    }

    @Override
    public void onPhaseChange(int newPhase) {
        shieldHealth = SHIELD_MAX;
        shieldBroken = false;
    }

    public double getShieldHealth() { return shieldHealth; }
    public double getShieldMax() { return SHIELD_MAX; }
    public boolean isShieldBroken() { return shieldBroken; }
    public boolean isWeaponDisableActive() { return weaponDisableActive; }
    public double getWeaponDisableDuration() { return weaponDisableDuration; }
    public boolean isPendingDecoySpawn() { return pendingDecoySpawn; }
    public void clearPendingDecoySpawn() { pendingDecoySpawn = false; }
}
