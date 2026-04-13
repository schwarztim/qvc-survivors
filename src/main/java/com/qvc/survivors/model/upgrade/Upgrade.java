package com.qvc.survivors.model.upgrade;

import java.util.HashMap;
import java.util.Map;
import lombok.Generated;

public class Upgrade {
   private final String name;
   private final String description;
   private final Map<StatModifier, Double> statModifiers;

   public Upgrade(String name, String description, StatModifier statModifier, double value) {
      this.name = name;
      this.description = description;
      this.statModifiers = new HashMap<>();
      this.statModifiers.put(statModifier, value);
   }

   public Upgrade(String name, String description, Map<StatModifier, Double> statModifiers) {
      this.name = name;
      this.description = description;
      this.statModifiers = new HashMap<>(statModifiers);
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getDescription() {
      return this.description;
   }

   @Generated
   public Map<StatModifier, Double> getStatModifiers() {
      return this.statModifiers;
   }
}
