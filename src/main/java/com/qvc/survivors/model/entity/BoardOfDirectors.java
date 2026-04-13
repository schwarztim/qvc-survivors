package com.qvc.survivors.model.entity;

import java.util.List;

public class BoardOfDirectors extends BossEnemy {
    private static final double CFO_HP = 150.0;
    private static final double CMO_HP = 200.0;
    private static final double CEO_HP = 350.0;
    private static final double SPEED = 10.0;
    private static final double DAMAGE = 20.0;
    private static final double SIZE = 5.0;

    private static final double PROJECTILE_INTERVAL = 3.0;
    private static final double SUMMON_INTERVAL = 8.0;
    private static final double REVERSE_INTERVAL = 12.0;
    private static final double REVERSE_DURATION = 3.0;

    private int subBossIndex = 0; // 0=CFO, 1=CMO, 2=CEO
    private double subBossMaxHp;
    private boolean pendingProjectile;
    private boolean pendingSummon;
    private boolean controlsReversed;
    private double reverseTimer = 0.0;
    private double summonTimer = 0.0;
    private boolean subBossDefeated;

    public BoardOfDirectors(double x, double y) {
        super(x, y, SIZE, SIZE, CFO_HP, SPEED, DAMAGE, 200, "Board of Directors");
        this.subBossMaxHp = CFO_HP;
        this.pendingProjectile = false;
        this.pendingSummon = false;
        this.controlsReversed = false;
        this.subBossDefeated = false;
    }

    @Override
    public void updatePhase(double deltaTime, double playerX, double playerY, List<Enemy> enemies) {
        pendingProjectile = false;
        pendingSummon = false;
        subBossDefeated = false;

        moveTowards(playerX, playerY);

        if (attackTimer <= 0) {
            pendingProjectile = true;
            attackTimer = PROJECTILE_INTERVAL;
        }

        switch (subBossIndex) {
            case 0: // CFO - money projectiles, heals from nearby kills
                break;
            case 1: // CMO - summons Influencers, reverses controls
                summonTimer += deltaTime;
                if (summonTimer >= SUMMON_INTERVAL) {
                    pendingSummon = true;
                    summonTimer = 0.0;
                }
                reverseTimer += deltaTime;
                if (controlsReversed) {
                    reverseTimer -= deltaTime * 2; // count down
                    if (reverseTimer <= 0) {
                        controlsReversed = false;
                        reverseTimer = 0.0;
                    }
                } else if (reverseTimer >= REVERSE_INTERVAL) {
                    controlsReversed = true;
                    reverseTimer = REVERSE_DURATION;
                }
                break;
            case 2: // CEO - all attacks combined
                summonTimer += deltaTime;
                if (summonTimer >= SUMMON_INTERVAL) {
                    pendingSummon = true;
                    summonTimer = 0.0;
                }
                break;
        }

        // Check if current sub-boss is dead
        if (!healthComponent.isAlive()) {
            advanceSubBoss();
        }
    }

    private void advanceSubBoss() {
        subBossDefeated = true;
        subBossIndex++;
        if (subBossIndex > 2) {
            // All defeated
            return;
        }

        switch (subBossIndex) {
            case 1: // CMO
                subBossMaxHp = CMO_HP;
                healthComponent.reset(CMO_HP);
                active = true;
                movementComponent.setSpeed(SPEED * 1.2);
                break;
            case 2: // CEO
                subBossMaxHp = CEO_HP;
                healthComponent.reset(CEO_HP);
                active = true;
                movementComponent.setSpeed(SPEED * 1.5);
                break;
        }
        summonTimer = 0.0;
        reverseTimer = 0.0;
        controlsReversed = false;
        attackTimer = 0.0;
    }

    @Override
    public void onPhaseChange(int newPhase) {
        // Phase transitions handled by advanceSubBoss
    }

    public int getSubBossIndex() { return subBossIndex; }
    public String getSubBossName() {
        return switch (subBossIndex) {
            case 0 -> "CFO";
            case 1 -> "CMO";
            case 2 -> "CEO";
            default -> "Board";
        };
    }
    public double getSubBossMaxHp() { return subBossMaxHp; }
    public boolean isPendingProjectile() { return pendingProjectile; }
    public void clearPendingProjectile() { pendingProjectile = false; }
    public boolean isPendingSummon() { return pendingSummon; }
    public void clearPendingSummon() { pendingSummon = false; }
    public boolean isControlsReversed() { return controlsReversed; }
    public boolean isSubBossDefeated() { return subBossDefeated; }
    public boolean isFullyDefeated() { return subBossIndex > 2; }

    public void healFromKill(double amount) {
        if (subBossIndex == 0) {
            healthComponent.heal(amount);
        }
    }
}
