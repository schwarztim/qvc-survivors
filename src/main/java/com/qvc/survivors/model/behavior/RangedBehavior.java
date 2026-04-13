package com.qvc.survivors.model.behavior;

import com.qvc.survivors.model.entity.Enemy;

public class RangedBehavior implements EnemyBehavior {
    private static final double PREFERRED_MIN = 4.0;
    private static final double PREFERRED_MAX = 6.0;
    private static final double FLEE_RANGE = 2.0;

    @Override
    public void update(Enemy self, double targetX, double targetY, double deltaTime) {
        double dx = targetX - self.getX();
        double dy = targetY - self.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < FLEE_RANGE) {
            // Flee away from player
            self.getMovementComponent().setDirection(-dx, -dy);
        } else if (dist < PREFERRED_MIN) {
            // Too close, back up slowly
            double halfSpeed = self.getMovementComponent().getSpeed() * 0.5;
            double len = dist > 0 ? dist : 1.0;
            self.getMovementComponent().setVelocityX((-dx / len) * halfSpeed);
            self.getMovementComponent().setVelocityY((-dy / len) * halfSpeed);
        } else if (dist > PREFERRED_MAX) {
            // Too far, approach
            self.moveTowards(targetX, targetY);
        } else {
            // In preferred range, strafe
            double perpX = -dy;
            double perpY = dx;
            double len = Math.sqrt(perpX * perpX + perpY * perpY);
            if (len > 0) {
                double speed = self.getMovementComponent().getSpeed() * 0.5;
                self.getMovementComponent().setVelocityX((perpX / len) * speed);
                self.getMovementComponent().setVelocityY((perpY / len) * speed);
            }
        }
    }
}
