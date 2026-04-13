package com.qvc.survivors.model.upgrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UpgradeRegistry {
   private static final Random RANDOM = new Random();
   private final List<Upgrade> availableUpgrades = new ArrayList<>();
   private static final String DELIVERY_RATE_NAME = "Delivery Rate";
   private static final String PACKAGE_EFFICIENCY_NAME = "Package Efficiency";
   private static final String PACKAGE_CAPACITY_NAME = "Package Capacity";
   private static final String COLLECTION_RANGE_NAME = "Collection Range";
   private static final String EXPRESS_CHANCE_NAME = "EXPRESS Delivery";
   private static final String MAX_STAMINA_NAME = "Max Stamina";
   private static final String PACKAGE_SPEED_NAME = "Package Speed";
   private static final String DRONE_NAME = "Autonomous Drone";

   public UpgradeRegistry() {
      this.initializeUpgrades();
   }

   private void initializeUpgrades() {
      this.availableUpgrades.add(new Upgrade("TikTok Shop Integration", "+15% Delivery Rate", StatModifier.FIRE_RATE, 0.15));
      this.availableUpgrades.add(new Upgrade("Amazon Competitor Mode", "+20% Package Efficiency", StatModifier.PACKAGE_DAMAGE, 0.2));
      this.availableUpgrades.add(new Upgrade("Expanded Warehouse", "+1 Package Capacity", StatModifier.PACKAGE_CAPACITY, 1.0));
      this.availableUpgrades.add(new Upgrade("Drone Fleet", "+1 Autonomous Drone", StatModifier.DRONE_COUNT, 1.0));
      this.availableUpgrades.add(new Upgrade("Prime Partnership", "+40% Collection Range", StatModifier.PICKUP_RANGE, 1.2));
      this.availableUpgrades.add(new Upgrade("Bulk Shipping Discount", "+8% EXPRESS Delivery", StatModifier.CRITICAL_CHANCE, 0.08));
      this.availableUpgrades.add(new Upgrade("Customer Service Training", "+25 Max Stamina", StatModifier.MAX_HEALTH, 25.0));
      this.availableUpgrades.add(new Upgrade("Overnight Express", "+50% Package Speed", StatModifier.PACKAGE_VELOCITY, 100.0));
      Map<StatModifier, Double> speedyDelivery = new HashMap<>();
      speedyDelivery.put(StatModifier.FIRE_RATE, 0.12);
      speedyDelivery.put(StatModifier.PACKAGE_VELOCITY, 80.0);
      this.availableUpgrades.add(new Upgrade("Speedy Delivery", "+12% Delivery Rate, +40% Package Speed", speedyDelivery));
      Map<StatModifier, Double> efficientWarehouse = new HashMap<>();
      efficientWarehouse.put(StatModifier.PACKAGE_DAMAGE, 0.12);
      efficientWarehouse.put(StatModifier.PICKUP_RANGE, 0.9);
      this.availableUpgrades.add(new Upgrade("Efficient Warehouse", "+12% Package Efficiency, +30% Collection Range", efficientWarehouse));
      Map<StatModifier, Double> powerPackages = new HashMap<>();
      powerPackages.put(StatModifier.PACKAGE_DAMAGE, 0.15);
      powerPackages.put(StatModifier.CRITICAL_CHANCE, 0.06);
      this.availableUpgrades.add(new Upgrade("Power Packages", "+15% Package Efficiency, +6% EXPRESS Delivery", powerPackages));
      Map<StatModifier, Double> roboticAssistance = new HashMap<>();
      roboticAssistance.put(StatModifier.DRONE_COUNT, 1.0);
      roboticAssistance.put(StatModifier.PACKAGE_DAMAGE, 0.08);
      this.availableUpgrades.add(new Upgrade("Robotic Assistance", "+1 Autonomous Drone, +8% Package Efficiency", roboticAssistance));
      Map<StatModifier, Double> healthyWorker = new HashMap<>();
      healthyWorker.put(StatModifier.MAX_HEALTH, 20.0);
      healthyWorker.put(StatModifier.PICKUP_RANGE, 0.75);
      this.availableUpgrades.add(new Upgrade("Healthy Worker", "+20 Max Stamina, +25% Collection Range", healthyWorker));
      Map<StatModifier, Double> rushHour = new HashMap<>();
      rushHour.put(StatModifier.FIRE_RATE, 0.18);
      rushHour.put(StatModifier.PACKAGE_CAPACITY, 1.0);
      this.availableUpgrades.add(new Upgrade("Rush Hour Master", "+18% Delivery Rate, +1 Package Capacity", rushHour));
      Map<StatModifier, Double> premiumService = new HashMap<>();
      premiumService.put(StatModifier.CRITICAL_CHANCE, 0.08);
      premiumService.put(StatModifier.PACKAGE_VELOCITY, 70.0);
      this.availableUpgrades.add(new Upgrade("Premium Service", "+8% EXPRESS Delivery, +35% Package Speed", premiumService));
      Map<StatModifier, Double> massProduction = new HashMap<>();
      massProduction.put(StatModifier.FIRE_RATE, 0.15);
      massProduction.put(StatModifier.PACKAGE_DAMAGE, 0.1);
      this.availableUpgrades.add(new Upgrade("Mass Production", "+15% Delivery Rate, +10% Package Efficiency", massProduction));
      Map<StatModifier, Double> tripleCombo = new HashMap<>();
      tripleCombo.put(StatModifier.FIRE_RATE, 0.08);
      tripleCombo.put(StatModifier.PACKAGE_DAMAGE, 0.08);
      tripleCombo.put(StatModifier.PACKAGE_VELOCITY, 50.0);
      this.availableUpgrades.add(new Upgrade("WIN Strategy", "+8% Delivery Rate, +8% Package Efficiency, +25% Package Speed", tripleCombo));
   }

   public List<Upgrade> getRandomUpgrades(int count) {
      List<Upgrade> selected = new ArrayList<>();
      List<Upgrade> pool = new ArrayList<>(this.availableUpgrades);

      for (int i = 0; i < Math.min(count, pool.size()); i++) {
         int index = RANDOM.nextInt(pool.size());
         selected.add(pool.remove(index));
      }

      return selected;
   }
}
