package com.qvc.survivors.view;

import javafx.scene.paint.Color;
import lombok.Generated;

public class Particle {
   private double x;
   private double y;
   private double velocityX;
   private double velocityY;
   private double lifetime;
   private double maxLifetime;
   private Color color;
   private double size;
   private Particle.ParticleType type;
   private boolean active;
   private double rotation;
   private double rotationSpeed;
   private double gravity;

   public Particle(double x, double y, double velocityX, double velocityY, double lifetime, Color color, double size, Particle.ParticleType type) {
      this.x = x;
      this.y = y;
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      this.lifetime = lifetime;
      this.maxLifetime = lifetime;
      this.color = color;
      this.size = size;
      this.type = type;
      this.active = true;
      this.rotation = Math.random() * Math.PI * 2.0;
      this.rotationSpeed = (Math.random() - 0.5) * 5.0;
      this.gravity = 0.0;
   }

   public void update(double deltaTime) {
      if (this.active) {
         this.x = this.x + this.velocityX * deltaTime;
         this.y = this.y + this.velocityY * deltaTime;
         this.velocityY = this.velocityY + this.gravity * deltaTime;
         this.rotation = this.rotation + this.rotationSpeed * deltaTime;
         this.velocityX *= 0.98;
         this.velocityY *= 0.98;
         this.lifetime -= deltaTime;
         if (this.lifetime <= 0.0) {
            this.active = false;
         }
      }
   }

   public double getAlpha() {
      return Math.max(0.0, Math.min(1.0, this.lifetime / this.maxLifetime));
   }

   public void reset(double x, double y, double velocityX, double velocityY, double lifetime, Color color, double size, Particle.ParticleType type) {
      this.x = x;
      this.y = y;
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      this.lifetime = lifetime;
      this.maxLifetime = lifetime;
      this.color = color;
      this.size = size;
      this.type = type;
      this.active = true;
      this.rotation = Math.random() * Math.PI * 2.0;
      this.rotationSpeed = (Math.random() - 0.5) * 5.0;
      this.gravity = 0.0;
   }

   @Generated
   public double getX() {
      return this.x;
   }

   @Generated
   public double getY() {
      return this.y;
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
   public double getLifetime() {
      return this.lifetime;
   }

   @Generated
   public double getMaxLifetime() {
      return this.maxLifetime;
   }

   @Generated
   public Color getColor() {
      return this.color;
   }

   @Generated
   public double getSize() {
      return this.size;
   }

   @Generated
   public Particle.ParticleType getType() {
      return this.type;
   }

   @Generated
   public boolean isActive() {
      return this.active;
   }

   @Generated
   public double getRotation() {
      return this.rotation;
   }

   @Generated
   public double getRotationSpeed() {
      return this.rotationSpeed;
   }

   @Generated
   public double getGravity() {
      return this.gravity;
   }

   @Generated
   public void setX(double x) {
      this.x = x;
   }

   @Generated
   public void setY(double y) {
      this.y = y;
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
   public void setLifetime(double lifetime) {
      this.lifetime = lifetime;
   }

   @Generated
   public void setMaxLifetime(double maxLifetime) {
      this.maxLifetime = maxLifetime;
   }

   @Generated
   public void setColor(Color color) {
      this.color = color;
   }

   @Generated
   public void setSize(double size) {
      this.size = size;
   }

   @Generated
   public void setType(Particle.ParticleType type) {
      this.type = type;
   }

   @Generated
   public void setActive(boolean active) {
      this.active = active;
   }

   @Generated
   public void setRotation(double rotation) {
      this.rotation = rotation;
   }

   @Generated
   public void setRotationSpeed(double rotationSpeed) {
      this.rotationSpeed = rotationSpeed;
   }

   @Generated
   public void setGravity(double gravity) {
      this.gravity = gravity;
   }

   public static enum ParticleType {
      CIRCLE,
      SQUARE,
      STAR,
      SPARK,
      SMOKE,
      RING;
   }
}
