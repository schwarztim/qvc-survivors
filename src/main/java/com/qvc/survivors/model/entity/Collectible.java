package com.qvc.survivors.model.entity;

import lombok.Generated;

public class Collectible extends Entity {
   private static final double SIZE = 0.5;
   private int value;
   private boolean isHealthPack;

   public Collectible(double x, double y, int value) {
      super(x, y, 0.5, 0.5);
      this.value = value;
      this.isHealthPack = false;
   }

   public Collectible(double x, double y, int value, boolean isHealthPack) {
      super(x, y, 0.5, 0.5);
      this.value = value;
      this.isHealthPack = isHealthPack;
   }

   public void reset(double x, double y, int value, boolean isHealthPack) {
      super.reset(x, y);
      this.value = value;
      this.isHealthPack = isHealthPack;
   }

   @Override
   public void update(double deltaTime) {
   }

   @Generated
   public int getValue() {
      return this.value;
   }

   @Generated
   public boolean isHealthPack() {
      return this.isHealthPack;
   }
}
