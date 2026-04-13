package com.qvc.survivors.model.behavior;

import com.qvc.survivors.model.entity.Enemy;

public class CircleBehavior implements EnemyBehavior {
    private static final double ORBIT_RANGE = 5.0;

    private double orbitAngle;

    public CircleBehavior() {
        this.orbitAngle = Math.random() * Math.PI * 2.0;
    }

    @Override
    public void update(Enemy self, double targetX, double targetY, double deltaTime) {
        double speed = self.getMovementComponent().getSpeed();
        orbitAngle += (speed * 0.04) * deltaTime;

        double goalX = targetX + Math.cos(orbitAngle) * ORBIT_RANGE;
        double goalY = targetY + Math.sin(orbitAngle) * ORBIT_RANGE;
        self.moveTowards(goalX, goalY);
    }

    public void reset() {
        orbitAngle = Math.random() * Math.PI * 2.0;
    }
}
