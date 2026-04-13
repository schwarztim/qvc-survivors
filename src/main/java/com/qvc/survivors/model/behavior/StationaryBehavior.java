package com.qvc.survivors.model.behavior;

import com.qvc.survivors.model.entity.Enemy;

public class StationaryBehavior implements EnemyBehavior {
    @Override
    public void update(Enemy self, double targetX, double targetY, double deltaTime) {
        self.getMovementComponent().stop();
    }
}
