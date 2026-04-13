package com.qvc.survivors.model.entity;

import com.qvc.survivors.model.component.DamageComponent;
import com.qvc.survivors.model.component.MovementComponent;
import lombok.Generated;

public abstract class Projectile extends Entity {
   protected final MovementComponent movementComponent = new MovementComponent(0.0);
   protected final DamageComponent damageComponent;

   public Projectile(double x, double y, double width, double height, double velocityX, double velocityY, double damage) {
      super(x, y, width, height);
      this.movementComponent.setVelocityX(velocityX);
      this.movementComponent.setVelocityY(velocityY);
      this.damageComponent = new DamageComponent(damage);
   }

   @Override
   public void update(double deltaTime) {
      this.x = this.x + this.movementComponent.getVelocityX() * deltaTime;
      this.y = this.y + this.movementComponent.getVelocityY() * deltaTime;
   }

   public boolean isOutOfBounds(double maxX, double maxY) {
      return this.x < -10.0 || this.x > maxX + 10.0 || this.y < -10.0 || this.y > maxY + 10.0;
   }

   @Generated
   public MovementComponent getMovementComponent() {
      return this.movementComponent;
   }

   @Generated
   public DamageComponent getDamageComponent() {
      return this.damageComponent;
   }
}
