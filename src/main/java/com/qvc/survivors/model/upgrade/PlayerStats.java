package com.qvc.survivors.model.upgrade;

import com.qvc.survivors.model.weapon.PlayerInventory;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Generated;

public class PlayerStats {
   private final Map<StatModifier, Double> stats = new HashMap<>();
   private static final double BASE_FIRE_RATE = 1.0;
   private static final double BASE_PACKAGE_CAPACITY = 1.0;
   private static final double BASE_PICKUP_RANGE = 5.0;
   private static final double BASE_PACKAGE_DAMAGE = 1.0;
   private static final double BASE_CRITICAL_CHANCE = 0.0;
   private static final double BASE_MAX_HEALTH = 100.0;
   private static final double BASE_PACKAGE_VELOCITY = 65.0;
   private static final double BASE_DRONE_COUNT = 0.0;

   public PlayerStats() {
      this.stats.put(StatModifier.FIRE_RATE, 1.0);
      this.stats.put(StatModifier.PACKAGE_CAPACITY, 1.0);
      this.stats.put(StatModifier.PICKUP_RANGE, 5.0);
      this.stats.put(StatModifier.PACKAGE_DAMAGE, 1.0);
      this.stats.put(StatModifier.CRITICAL_CHANCE, 0.0);
      this.stats.put(StatModifier.MAX_HEALTH, 100.0);
      this.stats.put(StatModifier.PACKAGE_VELOCITY, 65.0);
      this.stats.put(StatModifier.DRONE_COUNT, 0.0);
   }

   public void applyUpgrade(Upgrade upgrade) {
      for (Entry<StatModifier, Double> entry : upgrade.getStatModifiers().entrySet()) {
         StatModifier modifier = entry.getKey();
         double value = entry.getValue();
         double currentValue = this.stats.getOrDefault(modifier, 0.0);
         this.stats.put(modifier, currentValue + value);
      }
   }

   public double getStat(StatModifier modifier) {
      return this.stats.getOrDefault(modifier, 0.0);
   }

   public double getEffectiveStat(StatModifier modifier, PlayerInventory inventory) {
      double base = getStat(modifier);
      if (inventory != null) {
         base += inventory.getTotalStatBoost(modifier);
      }
      return base;
   }

   @Generated
   public Map<StatModifier, Double> getStats() {
      return this.stats;
   }
}
