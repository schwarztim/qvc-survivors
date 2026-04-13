package com.qvc.survivors.model.entity;

public class VIPCustomer extends Enemy {
   private static final double SIZE = 1.8;
   private static final double HEALTH = 3.0;
   private static final double SPEED = 22.0;
   private static final double DAMAGE = 10.0;
   private static final int MONEY_DROP = 5;

   public VIPCustomer(double x, double y) {
      super(x, y, 1.8, 1.8, 3.0, 22.0, 10.0, 5);
   }

   @Override
   public void reset(double x, double y) {
      super.reset(x, y);
      this.healthComponent.reset(3.0);
      this.movementComponent.reset(22.0);
      this.damageFlashTimer = 0.0;
   }
}
