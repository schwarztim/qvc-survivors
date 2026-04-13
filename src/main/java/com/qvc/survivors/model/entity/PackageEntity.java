package com.qvc.survivors.model.entity;

public class PackageEntity extends Projectile {
   private static final double SIZE = 1.5;

   public PackageEntity(double x, double y, double velocityX, double velocityY, double damage) {
      super(x, y, 1.5, 1.5, velocityX, velocityY, damage);
   }

   public void reset(double x, double y, double velocityX, double velocityY, double damage) {
      super.reset(x, y);
      this.movementComponent.setVelocityX(velocityX);
      this.movementComponent.setVelocityY(velocityY);
      this.damageComponent.setDamage(damage);
   }
}
