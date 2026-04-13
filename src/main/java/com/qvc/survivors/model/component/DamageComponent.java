package com.qvc.survivors.model.component;

import lombok.Generated;

public class DamageComponent {
   private double damage;

   public DamageComponent(double damage) {
      this.damage = damage;
   }

   @Generated
   public double getDamage() {
      return this.damage;
   }

   @Generated
   public void setDamage(double damage) {
      this.damage = damage;
   }
}
