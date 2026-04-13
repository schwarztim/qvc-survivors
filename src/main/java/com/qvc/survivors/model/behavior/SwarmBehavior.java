package com.qvc.survivors.model.behavior;

import com.qvc.survivors.model.entity.Enemy;

public class SwarmBehavior implements EnemyBehavior {
    private static final double ORBIT_DISTANCE = 3.0;
    private static final double LUNGE_DURATION = 0.3;
    private static final double ORBIT_DURATION = 2.0;

    private double orbitAngle;
    private double stateTimer = 0.0;
    private boolean lunging = false;

    public SwarmBehavior() {
        this.orbitAngle = Math.random() * Math.PI * 2.0;
    }

    @Override
    public void update(Enemy self, double targetX, double targetY, double deltaTime) {
        stateTimer += deltaTime;
        double speed = self.getMovementComponent().getSpeed();

        if (lunging) {
            self.moveTowards(targetX, targetY);
            if (stateTimer >= LUNGE_DURATION) {
                lunging = false;
                stateTimer = 0.0;
            }
        } else {
            orbitAngle += speed * 0.05 * deltaTime;
            double orbitX = targetX + Math.cos(orbitAngle) * ORBIT_DISTANCE;
            double orbitY = targetY + Math.sin(orbitAngle) * ORBIT_DISTANCE;
            self.moveTowards(orbitX, orbitY);

            if (stateTimer >= ORBIT_DURATION) {
                lunging = true;
                stateTimer = 0.0;
            }
        }
    }

    public void reset() {
        orbitAngle = Math.random() * Math.PI * 2.0;
        stateTimer = 0.0;
        lunging = false;
    }
}
