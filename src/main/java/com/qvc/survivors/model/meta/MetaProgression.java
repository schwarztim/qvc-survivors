package com.qvc.survivors.model.meta;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;

public class MetaProgression implements Serializable {
   private static final long serialVersionUID = 1L;
   private int totalMoney = 0;
   private int lifetimeMoney = 0;
   private int gamesPlayed = 0;
   private int totalKills = 0;
   private double longestSurvivalTime = 0.0;
   private int highestWave = 1;
   private int highestLevel = 1;
   private final Map<MetaUpgradeType, Integer> upgradeLevels = new HashMap<>();

   public MetaProgression() {
      for (MetaUpgradeType type : MetaUpgradeType.values()) {
         this.upgradeLevels.put(type, 0);
      }
   }

   public void addMoney(int amount) {
      this.totalMoney += amount;
      this.lifetimeMoney += amount;
   }

   public boolean canAfford(MetaUpgradeType type) {
      int currentLevel = this.upgradeLevels.get(type);
      int cost = type.getCost(currentLevel);
      return cost > 0 && this.totalMoney >= cost;
   }

   public boolean purchaseUpgrade(MetaUpgradeType type) {
      int currentLevel = this.upgradeLevels.get(type);
      int cost = type.getCost(currentLevel);
      if (cost > 0 && this.totalMoney >= cost) {
         this.totalMoney -= cost;
         this.upgradeLevels.put(type, currentLevel + 1);
         return true;
      } else {
         return false;
      }
   }

   public int getUpgradeLevel(MetaUpgradeType type) {
      return this.upgradeLevels.getOrDefault(type, 0);
   }

   public double getUpgradeValue(MetaUpgradeType type) {
      int level = this.getUpgradeLevel(type);
      return type.getValue(level);
   }

   public void updateStats(int kills, double survivalTime, int wave, int level, int moneyEarned) {
      this.gamesPlayed++;
      this.totalKills += kills;
      this.addMoney(moneyEarned);
      if (survivalTime > this.longestSurvivalTime) {
         this.longestSurvivalTime = survivalTime;
      }

      if (wave > this.highestWave) {
         this.highestWave = wave;
      }

      if (level > this.highestLevel) {
         this.highestLevel = level;
      }
   }

   @Generated
   public int getTotalMoney() {
      return this.totalMoney;
   }

   @Generated
   public int getLifetimeMoney() {
      return this.lifetimeMoney;
   }

   @Generated
   public int getGamesPlayed() {
      return this.gamesPlayed;
   }

   @Generated
   public int getTotalKills() {
      return this.totalKills;
   }

   @Generated
   public double getLongestSurvivalTime() {
      return this.longestSurvivalTime;
   }

   @Generated
   public int getHighestWave() {
      return this.highestWave;
   }

   @Generated
   public int getHighestLevel() {
      return this.highestLevel;
   }

   @Generated
   public Map<MetaUpgradeType, Integer> getUpgradeLevels() {
      return this.upgradeLevels;
   }

   @Generated
   public void setTotalMoney(int totalMoney) {
      this.totalMoney = totalMoney;
   }

   @Generated
   public void setLifetimeMoney(int lifetimeMoney) {
      this.lifetimeMoney = lifetimeMoney;
   }

   @Generated
   public void setGamesPlayed(int gamesPlayed) {
      this.gamesPlayed = gamesPlayed;
   }

   @Generated
   public void setTotalKills(int totalKills) {
      this.totalKills = totalKills;
   }

   @Generated
   public void setLongestSurvivalTime(double longestSurvivalTime) {
      this.longestSurvivalTime = longestSurvivalTime;
   }

   @Generated
   public void setHighestWave(int highestWave) {
      this.highestWave = highestWave;
   }

   @Generated
   public void setHighestLevel(int highestLevel) {
      this.highestLevel = highestLevel;
   }
}
