package com.qvc.survivors.model.entity;

public class RegularCustomer extends Enemy {
   private static final double SIZE = 1.8;
   private static final double HEALTH = 1.0;
   private static final double SPEED = 16.0;
   private static final double DAMAGE = 5.0;
   private static final int MONEY_DROP = 1;

   public RegularCustomer(double x, double y) {
      super(x, y, 1.8, 1.8, 1.0, 16.0, 5.0, 1);
   }

   @Override
   public void reset(double x, double y) {
      super.reset(x, y);
      this.healthComponent.reset(1.0);
      this.movementComponent.reset(16.0);
      this.damageFlashTimer = 0.0;
   }
}
