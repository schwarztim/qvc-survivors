package com.qvc.survivors.model.behavior;

import com.qvc.survivors.model.entity.Enemy;

public interface EnemyBehavior {
    void update(Enemy self, double targetX, double targetY, double deltaTime);
}
