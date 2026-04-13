package com.qvc.survivors.model.entity;

import java.util.List;

public class WarehouseManager extends BossEnemy {
    private static final double HP = 200.0;
    private static final double SPEED = 12.0;
    private static final double DAMAGE = 20.0;
    private static final double SIZE = 4.5;
    private static final double CHARGE_PAUSE = 2.0;
    private static final double CHARGE_DURATION = 0.6;

    private static final int STATE_PAUSE = 0;
    private static final int STATE_CHARGING = 1;

    private int chargeState = STATE_PAUSE;
    private double chargeTimer = 0.0;
    private double chargeDirX;
    private double chargeDirY;
    private boolean pendingProjectileBurst;

    public WarehouseManager(double x, double y) {
        super(x, y, SIZE, SIZE, HP, SPEED, DAMAGE, 75, "Warehouse Manager");
        this.pendingProjectileBurst = false;
    }

    @Override
    public void updatePhase(double deltaTime, double playerX, double playerY, List<Enemy> enemies) {
        double healthPercent = healthComponent.getHealthPercentage();
        if (healthPercent <= 0.5 && currentPhase == 0) {
            transitionToPhase(1);
        }

        pendingProjectileBurst = false;
        chargeTimer += deltaTime;

        double pauseDuration = currentPhase == 1 ? CHARGE_PAUSE * 0.6 : CHARGE_PAUSE;

        if (chargeState == STATE_PAUSE) {
            movementComponent.stop();
            if (chargeTimer >= pauseDuration) {
                chargeState = STATE_CHARGING;
                chargeTimer = 0.0;
                double dx = playerX - x;
                double dy = playerY - y;
                double len = Math.sqrt(dx * dx + dy * dy);
                if (len > 0) {
                    chargeDirX = dx / len;
                    chargeDirY = dy / len;
                } else {
                    chargeDirX = 1.0;
                    chargeDirY = 0.0;
                }
                if (currentPhase == 1) {
                    pendingProjectileBurst = true;
                }
            }
        } else {
            double chargeSpeed = currentPhase == 1 ? SPEED * 3.0 : SPEED * 2.0;
            movementComponent.setVelocityX(chargeDirX * chargeSpeed);
            movementComponent.setVelocityY(chargeDirY * chargeSpeed);
            if (chargeTimer >= CHARGE_DURATION) {
                chargeState = STATE_PAUSE;
                chargeTimer = 0.0;
            }
        }
    }

    @Override
    public void onPhaseChange(int newPhase) {
        chargeState = STATE_PAUSE;
        chargeTimer = 0.0;
    }

    public boolean isPendingProjectileBurst() { return pendingProjectileBurst; }
    public void clearPendingProjectileBurst() { pendingProjectileBurst = false; }
    public boolean isCharging() { return chargeState == STATE_CHARGING; }
}
