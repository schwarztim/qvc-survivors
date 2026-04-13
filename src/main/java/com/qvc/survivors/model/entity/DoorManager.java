package com.qvc.survivors.model.entity;

import java.util.ArrayList;
import java.util.List;

public class DoorManager extends BossEnemy {
    private static final double HP = 250.0;
    private static final double SPEED = 6.0;
    private static final double DAMAGE = 12.0;
    private static final double SIZE = 4.0;
    private static final double PORTAL_SPAWN_INTERVAL = 2.0;
    private static final double DAMAGE_REDUCTION_RANGE = 5.0;
    private static final double DAMAGE_REDUCTION_FACTOR = 0.5;

    private final List<double[]> portalPositions = new ArrayList<>();
    private double portalSpawnTimer = 0.0;
    private boolean pendingPortalSpawn;

    public DoorManager(double x, double y) {
        super(x, y, SIZE, SIZE, HP, SPEED, DAMAGE, 100, "Door Manager");
        this.pendingPortalSpawn = false;
    }

    @Override
    public void updatePhase(double deltaTime, double playerX, double playerY, List<Enemy> enemies) {
        double healthPercent = healthComponent.getHealthPercentage();
        if (healthPercent <= 0.3 && currentPhase == 0) {
            transitionToPhase(1);
        }

        moveTowards(playerX, playerY);

        pendingPortalSpawn = false;
        portalSpawnTimer += deltaTime;

        double interval = currentPhase == 1 ? PORTAL_SPAWN_INTERVAL / 3.0 : PORTAL_SPAWN_INTERVAL;
        if (portalSpawnTimer >= interval) {
            portalSpawnTimer = 0.0;
            pendingPortalSpawn = true;
            // Place portal near boss
            double angle = Math.random() * Math.PI * 2.0;
            double dist = 3.0 + Math.random() * 3.0;
            portalPositions.add(new double[]{x + Math.cos(angle) * dist, y + Math.sin(angle) * dist});
            if (portalPositions.size() > 4) {
                portalPositions.remove(0);
            }
        }

        // Apply damage reduction aura to nearby enemies
        for (Enemy enemy : enemies) {
            if (enemy == this || !enemy.isActive()) continue;
            double dx = enemy.getX() - x;
            double dy = enemy.getY() - y;
            if (Math.sqrt(dx * dx + dy * dy) <= DAMAGE_REDUCTION_RANGE) {
                // Flag for aura (handled in rendering / collision)
            }
        }
    }

    @Override
    public void onPhaseChange(int newPhase) {
        portalSpawnTimer = 0.0;
    }

    public boolean isPendingPortalSpawn() { return pendingPortalSpawn; }
    public void clearPendingPortalSpawn() { pendingPortalSpawn = false; }
    public List<double[]> getPortalPositions() { return portalPositions; }
    public double getDamageReductionRange() { return DAMAGE_REDUCTION_RANGE; }
    public double getDamageReductionFactor() { return DAMAGE_REDUCTION_FACTOR; }

    public boolean isEnemyInAura(Enemy enemy) {
        double dx = enemy.getX() - x;
        double dy = enemy.getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= DAMAGE_REDUCTION_RANGE;
    }
}
