package com.qvc.survivors.model.entity;

import com.qvc.survivors.model.component.DamageComponent;
import com.qvc.survivors.model.component.HealthComponent;
import com.qvc.survivors.model.component.MovementComponent;
import lombok.Generated;

public abstract class Enemy extends Entity {
   protected final HealthComponent healthComponent;
   protected final MovementComponent movementComponent;
   protected final DamageComponent damageComponent;
   protected final int moneyDrop;
   protected double damageFlashTimer;

   public Enemy(double x, double y, double width, double height, double health, double speed, double damage, int moneyDrop) {
      super(x, y, width, height);
      this.healthComponent = new HealthComponent(health);
      this.movementComponent = new MovementComponent(speed);
      this.damageComponent = new DamageComponent(damage);
      this.moneyDrop = moneyDrop;
      this.damageFlashTimer = 0.0;
   }

   @Override
   public void update(double deltaTime) {
      this.x = this.x + this.movementComponent.getVelocityX() * deltaTime;
      this.y = this.y + this.movementComponent.getVelocityY() * deltaTime;
      if (this.damageFlashTimer > 0.0) {
         this.damageFlashTimer -= deltaTime;
      }
   }

   public void moveTowards(double targetX, double targetY) {
      double dirX = targetX - this.x;
      double dirY = targetY - this.y;
      this.movementComponent.setDirection(dirX, dirY);
   }

   public void takeDamage(double damage) {
      this.healthComponent.damage(damage);
      this.damageFlashTimer = 0.15;
      if (!this.healthComponent.isAlive()) {
         this.active = false;
      }
   }

   public boolean isDamageFlashing() {
      return this.damageFlashTimer > 0.0;
   }

   @Generated
   public HealthComponent getHealthComponent() {
      return this.healthComponent;
   }

   @Generated
   public MovementComponent getMovementComponent() {
      return this.movementComponent;
   }

   @Generated
   public DamageComponent getDamageComponent() {
      return this.damageComponent;
   }

   @Generated
   public int getMoneyDrop() {
      return this.moneyDrop;
   }

   @Generated
   public double getDamageFlashTimer() {
      return this.damageFlashTimer;
   }
}
