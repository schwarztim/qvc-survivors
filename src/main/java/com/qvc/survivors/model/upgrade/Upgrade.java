package com.qvc.survivors.model.upgrade;

import java.util.HashMap;
import java.util.Map;
import lombok.Generated;

public class Upgrade {
   public enum ChoiceType { WEAPON_LEVELUP, NEW_WEAPON, PASSIVE_LEVELUP, NEW_PASSIVE, LEGACY }

   private final String name;
   private final String description;
   private final Map<StatModifier, Double> statModifiers;
   private final ChoiceType choiceType;
   private final String targetId;

   public Upgrade(String name, String description, StatModifier statModifier, double value) {
      this.name = name;
      this.description = description;
      this.statModifiers = new HashMap<>();
      this.statModifiers.put(statModifier, value);
      this.choiceType = ChoiceType.LEGACY;
      this.targetId = null;
   }

   public Upgrade(String name, String description, Map<StatModifier, Double> statModifiers) {
      this.name = name;
      this.description = description;
      this.statModifiers = new HashMap<>(statModifiers);
      this.choiceType = ChoiceType.LEGACY;
      this.targetId = null;
   }

   public Upgrade(String name, String description, ChoiceType choiceType, String targetId) {
      this.name = name;
      this.description = description;
      this.statModifiers = new HashMap<>();
      this.choiceType = choiceType;
      this.targetId = targetId;
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

   public ChoiceType getChoiceType() {
      return this.choiceType;
   }

   public String getTargetId() {
      return this.targetId;
   }
}
