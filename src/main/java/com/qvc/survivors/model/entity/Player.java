package com.qvc.survivors.model.entity;

import com.qvc.survivors.model.component.HealthComponent;
import com.qvc.survivors.model.component.MovementComponent;
import com.qvc.survivors.model.meta.MetaProgression;
import com.qvc.survivors.model.meta.MetaUpgradeType;
import com.qvc.survivors.model.upgrade.PlayerStats;
import com.qvc.survivors.model.upgrade.StatModifier;
import com.qvc.survivors.model.weapon.PlayerInventory;
import com.qvc.survivors.model.weapon.impl.PackageLauncher;
import lombok.Generated;

public class Player extends Entity {
   private static final double PLAYER_SIZE = 1.0;
   private static final double PLAYER_SPEED = 14.0;
   private final HealthComponent healthComponent;
   private final MovementComponent movementComponent;
   private final PlayerStats stats = new PlayerStats();
   private final PlayerInventory inventory = new PlayerInventory();
   private final double xpMultiplier;
   private int level;
   private double experience;
   private double experienceThreshold;
   private int money;
   private int customersSatisfied;
   private double damageFlashTimer;
   private double survivalTime;
   private double facingAngle;
   private double fireTimer;
   private double invulnerabilityTimer;
   private double coffeeBreakTimer;
   private boolean employeeDiscountActive;

   public Player(double x, double y) {
      this(x, y, null);
   }

   public Player(double x, double y, MetaProgression metaProgression) {
      super(x, y, 1.0, 1.0);
      this.xpMultiplier = 1.0 + (metaProgression != null ? metaProgression.getUpgradeValue(MetaUpgradeType.XP_MULTIPLIER) / 100.0 : 0.0);
      this.applyMetaUpgrades(metaProgression);
      this.healthComponent = new HealthComponent(this.stats.getStat(StatModifier.MAX_HEALTH));
      this.movementComponent = new MovementComponent(35.0 + this.getMetaSpeedBonus(metaProgression));
      this.level = 1;
      this.experience = 0.0;
      this.experienceThreshold = 5.0;
      this.money = 0;
      this.customersSatisfied = 0;
      this.survivalTime = 0.0;
      this.fireTimer = 0.0;
      this.invulnerabilityTimer = 0.0;
      this.damageFlashTimer = 0.0;
      this.coffeeBreakTimer = 0.0;
      this.employeeDiscountActive = false;
      this.facingAngle = 0.0;
      this.inventory.addWeapon(new PackageLauncher());
   }

   private void applyMetaUpgrades(MetaProgression metaProgression) {
      if (metaProgression != null) {
         double healthBonus = metaProgression.getUpgradeValue(MetaUpgradeType.STARTING_HEALTH);
         this.stats.getStats().put(StatModifier.MAX_HEALTH, this.stats.getStat(StatModifier.MAX_HEALTH) + healthBonus);
         double damageBonus = metaProgression.getUpgradeValue(MetaUpgradeType.STARTING_DAMAGE);
         this.stats.getStats().put(StatModifier.PACKAGE_DAMAGE, this.stats.getStat(StatModifier.PACKAGE_DAMAGE) + damageBonus / 100.0);
         double fireRateBonus = metaProgression.getUpgradeValue(MetaUpgradeType.STARTING_FIRE_RATE);
         this.stats.getStats().put(StatModifier.FIRE_RATE, this.stats.getStat(StatModifier.FIRE_RATE) + fireRateBonus / 100.0);
         double pickupRangeBonus = metaProgression.getUpgradeValue(MetaUpgradeType.STARTING_PICKUP_RANGE);
         this.stats.getStats().put(StatModifier.PICKUP_RANGE, this.stats.getStat(StatModifier.PICKUP_RANGE) + pickupRangeBonus);
         double critBonus = metaProgression.getUpgradeValue(MetaUpgradeType.STARTING_CRIT_CHANCE);
         this.stats.getStats().put(StatModifier.CRITICAL_CHANCE, this.stats.getStat(StatModifier.CRITICAL_CHANCE) + critBonus / 100.0);
         double velocityBonus = metaProgression.getUpgradeValue(MetaUpgradeType.PACKAGE_VELOCITY);
         this.stats.getStats().put(StatModifier.PACKAGE_VELOCITY, this.stats.getStat(StatModifier.PACKAGE_VELOCITY) + velocityBonus);
      }
   }

   private double getMetaSpeedBonus(MetaProgression metaProgression) {
      return metaProgression == null ? 0.0 : metaProgression.getUpgradeValue(MetaUpgradeType.STARTING_SPEED);
   }

   @Override
   public void update(double deltaTime) {
      double speedMult = this.coffeeBreakTimer > 0.0 ? 1.3 : 1.0;
      double passiveSpeedBoost = 1.0 + this.inventory.getTotalStatBoost(StatModifier.MOVEMENT_SPEED);
      this.x = this.x + this.movementComponent.getVelocityX() * deltaTime * speedMult * passiveSpeedBoost;
      this.y = this.y + this.movementComponent.getVelocityY() * deltaTime * speedMult * passiveSpeedBoost;
      if (this.movementComponent.getVelocityX() != 0.0 || this.movementComponent.getVelocityY() != 0.0) {
         this.facingAngle = Math.atan2(this.movementComponent.getVelocityY(), this.movementComponent.getVelocityX());
      }
      this.survivalTime += deltaTime;
      this.fireTimer += deltaTime;
      if (this.invulnerabilityTimer > 0.0) {
         this.invulnerabilityTimer -= deltaTime;
      }

      if (this.damageFlashTimer > 0.0) {
         this.damageFlashTimer -= deltaTime;
      }

      if (this.coffeeBreakTimer > 0.0) {
         this.coffeeBreakTimer -= deltaTime;
      }
   }

   public void addExperience(double amount) {
      double xpBonus = 1.0 + this.inventory.getTotalStatBoost(StatModifier.XP_BONUS);
      double moneyBonus = 1.0 + this.inventory.getTotalStatBoost(StatModifier.MONEY_BONUS);
      double adjustedXp = amount * this.xpMultiplier * xpBonus;
      double adjustedMoney = amount * moneyBonus;
      this.experience += adjustedXp;
      this.money += (int)adjustedMoney;
   }

   public boolean canLevelUp() {
      return this.experience >= this.experienceThreshold;
   }

   public void levelUp() {
      this.level++;
      this.experience = this.experience - this.experienceThreshold;
      this.experienceThreshold = this.calculateNextThreshold();
   }

   private double calculateNextThreshold() {
      return 5 + (this.level - 1) * 8 + Math.pow(this.level - 1, 1.3) * 3.0;
   }

   public void takeDamage(double damage) {
      if (!(this.invulnerabilityTimer > 0.0)) {
         double reduction = this.inventory.getTotalStatBoost(StatModifier.DAMAGE_REDUCTION);
         double effectiveDamage = damage * Math.max(0.0, 1.0 - reduction);
         this.healthComponent.damage(effectiveDamage);
         this.damageFlashTimer = 0.2;
         this.invulnerabilityTimer = 1.5;
         if (!this.healthComponent.isAlive()) {
            this.active = false;
         }
      }
   }

   public boolean canFire() {
      double fireRate = this.stats.getStat(StatModifier.FIRE_RATE);
      if (this.coffeeBreakTimer > 0.0) {
         fireRate *= 1.5;
      }
      double fireInterval = 1.0 / fireRate;
      return this.fireTimer >= fireInterval;
   }

   public void resetFireTimer() {
      this.fireTimer = 0.0;
   }

   public void incrementCustomersSatisfied() {
      this.customersSatisfied++;
   }

   public void activateInvulnerability(double duration) {
      this.invulnerabilityTimer = duration;
   }

   public boolean isInvulnerable() {
      return this.invulnerabilityTimer > 0.0;
   }

   public boolean isDamageFlashing() {
      return this.damageFlashTimer > 0.0;
   }

   public void activateCoffeeBreak(double duration) {
      this.coffeeBreakTimer = duration;
   }

   public boolean isCoffeeBreakActive() {
      return this.coffeeBreakTimer > 0.0;
   }

   public double getCoffeeBreakTimer() {
      return this.coffeeBreakTimer;
   }

   public void activateEmployeeDiscount() {
      this.employeeDiscountActive = true;
   }

   public boolean isEmployeeDiscountActive() {
      return this.employeeDiscountActive;
   }

   public void consumeEmployeeDiscount() {
      this.employeeDiscountActive = false;
   }

   public double getEffectiveSpeed() {
      double baseSpeed = this.movementComponent.getSpeed();
      if (this.coffeeBreakTimer > 0.0) {
         return baseSpeed * 1.3;
      }
      return baseSpeed;
   }

   public PlayerInventory getInventory() {
      return this.inventory;
   }

   public double getFacingAngle() {
      return this.facingAngle;
   }

   @Generated
   public HealthComponent getHealthComponent() {
      return this.healthComponent;
   }

   @Generated
   public MovementComponent getMovementComponent() {
      return this.movementComponent;
   }

   @Generated
   public PlayerStats getStats() {
      return this.stats;
   }

   @Generated
   public double getXpMultiplier() {
      return this.xpMultiplier;
   }

   @Generated
   public int getLevel() {
      return this.level;
   }

   @Generated
   public double getExperience() {
      return this.experience;
   }

   @Generated
   public double getExperienceThreshold() {
      return this.experienceThreshold;
   }

   @Generated
   public int getMoney() {
      return this.money;
   }

   @Generated
   public int getCustomersSatisfied() {
      return this.customersSatisfied;
   }

   @Generated
   public double getDamageFlashTimer() {
      return this.damageFlashTimer;
   }

   @Generated
   public double getSurvivalTime() {
      return this.survivalTime;
   }

   @Generated
   public double getFireTimer() {
      return this.fireTimer;
   }

   @Generated
   public double getInvulnerabilityTimer() {
      return this.invulnerabilityTimer;
   }

   @Generated
   public void setLevel(int level) {
      this.level = level;
   }

   @Generated
   public void setExperience(double experience) {
      this.experience = experience;
   }

   @Generated
   public void setExperienceThreshold(double experienceThreshold) {
      this.experienceThreshold = experienceThreshold;
   }

   @Generated
   public void setMoney(int money) {
      this.money = money;
   }

   @Generated
   public void setCustomersSatisfied(int customersSatisfied) {
      this.customersSatisfied = customersSatisfied;
   }

   @Generated
   public void setDamageFlashTimer(double damageFlashTimer) {
      this.damageFlashTimer = damageFlashTimer;
   }

   @Generated
   public void setSurvivalTime(double survivalTime) {
      this.survivalTime = survivalTime;
   }

   @Generated
   public void setFireTimer(double fireTimer) {
      this.fireTimer = fireTimer;
   }

   @Generated
   public void setInvulnerabilityTimer(double invulnerabilityTimer) {
      this.invulnerabilityTimer = invulnerabilityTimer;
   }
}
