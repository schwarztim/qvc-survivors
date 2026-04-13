package com.qvc.survivors.model.entity;

import lombok.Generated;

public class Collectible extends Entity {
   private static final double SIZE = 0.5;
   private static final double MAX_LIFETIME = 60.0;
   private int value;
   private boolean isHealthPack;
   private double lifetime;

   public Collectible(double x, double y, int value) {
      super(x, y, 0.5, 0.5);
      this.value = value;
      this.isHealthPack = false;
      this.lifetime = MAX_LIFETIME;
   }

   public Collectible(double x, double y, int value, boolean isHealthPack) {
      super(x, y, 0.5, 0.5);
      this.value = value;
      this.isHealthPack = isHealthPack;
      this.lifetime = MAX_LIFETIME;
   }

   public void reset(double x, double y, int value, boolean isHealthPack) {
      super.reset(x, y);
      this.value = value;
      this.isHealthPack = isHealthPack;
      this.lifetime = MAX_LIFETIME;
   }

   @Override
   public void update(double deltaTime) {
      this.lifetime -= deltaTime;
      if (this.lifetime <= 0.0) {
         this.setActive(false);
      }
   }

   @Generated
   public int getValue() {
      return this.value;
   }

   /** Returns gem tier: 0 = small blue (1-2 XP), 1 = medium green (3-9 XP), 2 = large red (10+ XP) */
   public int getTier() {
      if (this.value <= 2) return 0;
      if (this.value <= 9) return 1;
      return 2;
   }

   @Generated
   public boolean isHealthPack() {
      return this.isHealthPack;
   }
}
