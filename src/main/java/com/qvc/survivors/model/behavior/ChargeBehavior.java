package com.qvc.survivors.model.behavior;

import com.qvc.survivors.model.entity.Enemy;

public class ChargeBehavior implements EnemyBehavior {
    private static final int STATE_IDLE = 0;
    private static final int STATE_CHARGING = 1;
    private static final int STATE_COOLDOWN = 2;

    private static final double IDLE_DURATION = 0.5;
    private static final double CHARGE_DURATION = 0.8;
    private static final double COOLDOWN_DURATION = 1.0;
    private static final double CHARGE_SPEED_MULTIPLIER = 2.0;

    private int state = STATE_IDLE;
    private double stateTimer = 0.0;
    private double lockedDirX;
    private double lockedDirY;

    @Override
    public void update(Enemy self, double targetX, double targetY, double deltaTime) {
        stateTimer += deltaTime;

        switch (state) {
            case STATE_IDLE:
                self.getMovementComponent().stop();
                if (stateTimer >= IDLE_DURATION) {
                    state = STATE_CHARGING;
                    stateTimer = 0.0;
                    double dx = targetX - self.getX();
                    double dy = targetY - self.getY();
                    double len = Math.sqrt(dx * dx + dy * dy);
                    if (len > 0) {
                        lockedDirX = dx / len;
                        lockedDirY = dy / len;
                    } else {
                        lockedDirX = 1.0;
                        lockedDirY = 0.0;
                    }
                }
                break;

            case STATE_CHARGING:
                double speed = self.getMovementComponent().getSpeed() * CHARGE_SPEED_MULTIPLIER;
                self.getMovementComponent().setVelocityX(lockedDirX * speed);
                self.getMovementComponent().setVelocityY(lockedDirY * speed);
                if (stateTimer >= CHARGE_DURATION) {
                    state = STATE_COOLDOWN;
                    stateTimer = 0.0;
                }
                break;

            case STATE_COOLDOWN:
                self.getMovementComponent().stop();
                if (stateTimer >= COOLDOWN_DURATION) {
                    state = STATE_IDLE;
                    stateTimer = 0.0;
                }
                break;
        }
    }

    public void reset() {
        state = STATE_IDLE;
        stateTimer = 0.0;
    }

    public int getState() {
        return state;
    }
}
