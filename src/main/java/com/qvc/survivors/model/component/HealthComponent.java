package com.qvc.survivors.model.component;

import lombok.Generated;

public class HealthComponent {
   private double maxHealth;
   private double currentHealth;

   public HealthComponent(double maxHealth) {
      this.maxHealth = maxHealth;
      this.currentHealth = maxHealth;
   }

   public void damage(double amount) {
      this.currentHealth = Math.max(0.0, this.currentHealth - amount);
   }

   public void reset(double maxHealth) {
      this.maxHealth = maxHealth;
      this.currentHealth = maxHealth;
   }

   public void heal(double amount) {
      this.currentHealth = Math.min(this.maxHealth, this.currentHealth + amount);
   }

   public void increaseMaxHealth(double amount) {
      this.maxHealth += amount;
      this.currentHealth += amount;
   }

   public void setMaxHealth(double maxHealth) {
      this.maxHealth = maxHealth;
   }

   public boolean isAlive() {
      return this.currentHealth > 0.0;
   }

   public double getHealthPercentage() {
      return this.currentHealth / this.maxHealth;
   }

   @Generated
   public double getMaxHealth() {
      return this.maxHealth;
   }

   @Generated
   public double getCurrentHealth() {
      return this.currentHealth;
   }
}
