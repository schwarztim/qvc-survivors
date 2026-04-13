package com.qvc.survivors.model.entity;

import java.util.List;

public abstract class BossEnemy extends Enemy {
    protected int currentPhase;
    protected double phaseTimer;
    protected double attackCooldown;
    protected double attackTimer;
    protected boolean isPhaseTransitioning;
    private final String bossName;

    public BossEnemy(double x, double y, double width, double height, double health,
                     double speed, double damage, int moneyDrop, String bossName) {
        super(x, y, width, height, health, speed, damage, moneyDrop);
        this.bossName = bossName;
        this.currentPhase = 0;
        this.phaseTimer = 0.0;
        this.attackCooldown = 0.0;
        this.attackTimer = 0.0;
        this.isPhaseTransitioning = false;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        phaseTimer += deltaTime;
        if (attackTimer > 0) {
            attackTimer -= deltaTime;
        }
    }

    public abstract void updatePhase(double deltaTime, double playerX, double playerY, List<Enemy> enemies);

    public abstract void onPhaseChange(int newPhase);

    protected void transitionToPhase(int newPhase) {
        if (newPhase != currentPhase) {
            isPhaseTransitioning = true;
            currentPhase = newPhase;
            phaseTimer = 0.0;
            onPhaseChange(newPhase);
            isPhaseTransitioning = false;
        }
    }

    public String getBossName() { return bossName; }
    public int getCurrentPhase() { return currentPhase; }
    public double getPhaseTimer() { return phaseTimer; }
    public boolean isPhaseTransitioning() { return isPhaseTransitioning; }
    public double getAttackTimer() { return attackTimer; }
}
