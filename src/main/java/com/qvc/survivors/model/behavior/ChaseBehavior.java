package com.qvc.survivors.model.behavior;

import com.qvc.survivors.model.entity.Enemy;

public class ChaseBehavior implements EnemyBehavior {
    @Override
    public void update(Enemy self, double targetX, double targetY, double deltaTime) {
        self.moveTowards(targetX, targetY);
    }
}
