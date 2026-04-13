package com.qvc.survivors.model.meta;

import lombok.Generated;

public enum MetaUpgradeType {
   STARTING_HEALTH("Extra Health", "Increase starting health", 10.0, 5, 100.0),
   STARTING_DAMAGE("Damage Boost", "Increase starting damage", 5.0, 5, 50.0),
   STARTING_SPEED("Speed Boost", "Increase movement speed", 3.0, 5, 15.0),
   STARTING_FIRE_RATE("Fire Rate", "Increase starting fire rate", 5.0, 5, 25.0),
   XP_MULTIPLIER("XP Gain", "Increase experience gained", 10.0, 5, 50.0),
   STARTING_PICKUP_RANGE("Pickup Range", "Increase starting pickup range", 0.5, 5, 2.5),
   HEALTH_PACK_DROP_CHANCE("Health Pack Chance", "Increase health pack drop rate", 1.0, 5, 5.0),
   STARTING_CRIT_CHANCE("Critical Chance", "Increase starting critical chance", 2.0, 5, 10.0),
   PACKAGE_VELOCITY("Package Speed", "Increase starting package velocity", 10.0, 5, 50.0),
   REROLL_CHARGES("Reroll Charges", "Reroll level-up options per run", 1.0, 3, 3.0),
   SKIP_CHARGES("Skip Charges", "Skip level-up per run", 1.0, 3, 3.0),
   BANISH_CHARGES("Banish Charges", "Banish upgrades from pool per run", 1.0, 3, 3.0);

   private final String displayName;
   private final String description;
   private final double valuePerLevel;
   private final int maxLevel;
   private final double maxValue;

   private MetaUpgradeType(String displayName, String description, double valuePerLevel, int maxLevel, double maxValue) {
      this.displayName = displayName;
      this.description = description;
      this.valuePerLevel = valuePerLevel;
      this.maxLevel = maxLevel;
      this.maxValue = maxValue;
   }

   public int getCost(int currentLevel) {
      return currentLevel >= this.maxLevel ? -1 : 100 + currentLevel * 50;
   }

   public double getValue(int level) {
      return Math.min(level * this.valuePerLevel, this.maxValue);
   }

   @Generated
   public String getDisplayName() {
      return this.displayName;
   }

   @Generated
   public String getDescription() {
      return this.description;
   }

   @Generated
   public double getValuePerLevel() {
      return this.valuePerLevel;
   }

   @Generated
   public int getMaxLevel() {
      return this.maxLevel;
   }

   @Generated
   public double getMaxValue() {
      return this.maxValue;
   }
}
