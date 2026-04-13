package com.qvc.survivors.model.entity;

import lombok.Generated;

public abstract class Entity {
   protected double x;
   protected double y;
   protected double width;
   protected double height;
   protected boolean active;

   public Entity(double x, double y, double width, double height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.active = true;
   }

   public abstract void update(double var1);

   public void reset(double x, double y) {
      this.x = x;
      this.y = y;
      this.active = true;
   }

   public boolean collidesWith(Entity other) {
      return other != null && other.isActive()
         ? this.x < other.x + other.width && this.x + this.width > other.x && this.y < other.y + other.height && this.y + this.height > other.y
         : false;
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
   public double getWidth() {
      return this.width;
   }

   @Generated
   public double getHeight() {
      return this.height;
   }

   @Generated
   public boolean isActive() {
      return this.active;
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
   public void setWidth(double width) {
      this.width = width;
   }

   @Generated
   public void setHeight(double height) {
      this.height = height;
   }

   @Generated
   public void setActive(boolean active) {
      this.active = active;
   }
}
