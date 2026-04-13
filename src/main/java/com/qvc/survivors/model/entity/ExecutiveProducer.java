package com.qvc.survivors.model.entity;

import java.util.List;

public class ExecutiveProducer extends BossEnemy {
    private static final double HP = 150.0;
    private static final double SPEED = 8.0;
    private static final double DAMAGE = 15.0;
    private static final double SIZE = 4.0;
    private static final double SPAWN_RING_INTERVAL = 5.0;
    private static final double PROJECTILE_INTERVAL = 3.0;
    private static final int RING_COUNT = 8;

    private boolean pendingSpawnRing;
    private boolean pendingProjectile;

    public ExecutiveProducer(double x, double y) {
        super(x, y, SIZE, SIZE, HP, SPEED, DAMAGE, 50, "Executive Producer");
        this.pendingSpawnRing = false;
        this.pendingProjectile = false;
    }

    @Override
    public void updatePhase(double deltaTime, double playerX, double playerY, List<Enemy> enemies) {
        double healthPercent = healthComponent.getHealthPercentage();
        if (healthPercent <= 0.5 && currentPhase == 0) {
            transitionToPhase(1);
        }

        pendingSpawnRing = false;
        pendingProjectile = false;

        if (currentPhase == 0) {
            moveTowards(playerX, playerY);
            if (attackTimer <= 0) {
                pendingSpawnRing = true;
                attackTimer = SPAWN_RING_INTERVAL;
            }
        } else {
            // Phase 2: doubled speed chase + projectiles
            movementComponent.setSpeed(SPEED * 2.0);
            moveTowards(playerX, playerY);
            if (attackTimer <= 0) {
                pendingProjectile = true;
                attackTimer = PROJECTILE_INTERVAL;
            }
        }
    }

    @Override
    public void onPhaseChange(int newPhase) {
        if (newPhase == 1) {
            movementComponent.setSpeed(SPEED * 2.0);
        }
    }

    public boolean isPendingSpawnRing() { return pendingSpawnRing; }
    public boolean isPendingProjectile() { return pendingProjectile; }
    public int getRingCount() { return RING_COUNT; }

    public void clearPendingSpawnRing() { pendingSpawnRing = false; }
    public void clearPendingProjectile() { pendingProjectile = false; }
}
