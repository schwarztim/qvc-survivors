package com.qvc.survivors.model.component;

import lombok.Generated;

public class MovementComponent {
   private double velocityX;
   private double velocityY;
   private double speed;

   public MovementComponent(double speed) {
      this.speed = speed;
      this.velocityX = 0.0;
      this.velocityY = 0.0;
   }

   public void setDirection(double dirX, double dirY) {
      double length = Math.sqrt(dirX * dirX + dirY * dirY);
      if (length > 0.0) {
         this.velocityX = dirX / length * this.speed;
         this.velocityY = dirY / length * this.speed;
      } else {
         this.velocityX = 0.0;
         this.velocityY = 0.0;
      }
   }

   public void stop() {
      this.velocityX = 0.0;
      this.velocityY = 0.0;
   }

   public void reset(double speed) {
      this.speed = speed;
      this.velocityX = 0.0;
      this.velocityY = 0.0;
   }

   @Generated
   public double getVelocityX() {
      return this.velocityX;
   }

   @Generated
   public double getVelocityY() {
      return this.velocityY;
   }

   @Generated
   public double getSpeed() {
      return this.speed;
   }

   @Generated
   public void setVelocityX(double velocityX) {
      this.velocityX = velocityX;
   }

   @Generated
   public void setVelocityY(double velocityY) {
      this.velocityY = velocityY;
   }

   @Generated
   public void setSpeed(double speed) {
      this.speed = speed;
   }
}
