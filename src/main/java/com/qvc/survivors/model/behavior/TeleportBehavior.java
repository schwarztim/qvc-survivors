package com.qvc.survivors.model.behavior;

import com.qvc.survivors.model.entity.Enemy;

public class TeleportBehavior implements EnemyBehavior {
    private static final double TELEPORT_INTERVAL = 3.0;
    private static final double TELEPORT_RANGE = 5.0;
    private static final double SLOW_SPEED_FACTOR = 0.3;

    private double teleportTimer = 0.0;

    @Override
    public void update(Enemy self, double targetX, double targetY, double deltaTime) {
        teleportTimer += deltaTime;

        if (teleportTimer >= TELEPORT_INTERVAL) {
            teleportTimer = 0.0;
            double angle = Math.random() * Math.PI * 2.0;
            double dist = 1.0 + Math.random() * (TELEPORT_RANGE - 1.0);
            self.setX(targetX + Math.cos(angle) * dist);
            self.setY(targetY + Math.sin(angle) * dist);
            self.getMovementComponent().stop();
        } else {
            // Move slowly toward target between teleports
            double dx = targetX - self.getX();
            double dy = targetY - self.getY();
            double len = Math.sqrt(dx * dx + dy * dy);
            if (len > 0) {
                double speed = self.getMovementComponent().getSpeed() * SLOW_SPEED_FACTOR;
                self.getMovementComponent().setVelocityX((dx / len) * speed);
                self.getMovementComponent().setVelocityY((dy / len) * speed);
            }
        }
    }

    public void reset() {
        teleportTimer = 0.0;
    }

    public double getTeleportTimer() {
        return teleportTimer;
    }
}
