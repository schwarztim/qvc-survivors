package com.qvc.survivors.model.entity;

import lombok.Generated;

public class Drone extends Projectile {
   private static final double SIZE = 1.2;
   private static final double ORBIT_SPEED = 100.0;
   private static final double ATTACK_COOLDOWN = 0.5;
   private final double orbitRadius;
   private final double orbitOffset;
   private double angle;
   private double attackTimer;

   public Drone(double x, double y, double damage, double orbitRadius, double orbitOffset) {
      super(x, y, 1.2, 1.2, 0.0, 0.0, damage);
      this.orbitRadius = orbitRadius;
      this.orbitOffset = orbitOffset;
      this.angle = orbitOffset;
      this.attackTimer = 0.0;
   }

   @Override
   public void update(double deltaTime) {
      this.angle += 100.0 * deltaTime * 0.01;
      if (this.angle > Math.PI * 2) {
         this.angle -= Math.PI * 2;
      }

      if (this.attackTimer > 0.0) {
         this.attackTimer -= deltaTime;
      }
   }

   public boolean canAttack() {
      return this.attackTimer <= 0.0;
   }

   public void resetAttackTimer() {
      this.attackTimer = 0.5;
   }

   public void updatePosition(double centerX, double centerY) {
      this.x = centerX + Math.cos(this.angle) * this.orbitRadius;
      this.y = centerY + Math.sin(this.angle) * this.orbitRadius;
   }

   @Generated
   public double getOrbitRadius() {
      return this.orbitRadius;
   }

   @Generated
   public double getOrbitOffset() {
      return this.orbitOffset;
   }

   @Generated
   public double getAngle() {
      return this.angle;
   }

   @Generated
   public double getAttackTimer() {
      return this.attackTimer;
   }
}
