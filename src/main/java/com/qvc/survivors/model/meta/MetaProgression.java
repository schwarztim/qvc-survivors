package com.qvc.survivors.model.meta;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
   private final Set<String> unlockedAchievements = new HashSet<>();

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

   public String toJson() {
      StringBuilder sb = new StringBuilder();
      sb.append("{\n");
      sb.append("  \"totalMoney\": ").append(totalMoney).append(",\n");
      sb.append("  \"lifetimeMoney\": ").append(lifetimeMoney).append(",\n");
      sb.append("  \"gamesPlayed\": ").append(gamesPlayed).append(",\n");
      sb.append("  \"totalKills\": ").append(totalKills).append(",\n");
      sb.append("  \"longestSurvivalTime\": ").append(longestSurvivalTime).append(",\n");
      sb.append("  \"highestWave\": ").append(highestWave).append(",\n");
      sb.append("  \"highestLevel\": ").append(highestLevel).append(",\n");
      sb.append("  \"upgradeLevels\": {");
      boolean first = true;
      for (Map.Entry<MetaUpgradeType, Integer> entry : upgradeLevels.entrySet()) {
         if (!first) sb.append(",");
         sb.append("\n    \"").append(entry.getKey().name()).append("\": ").append(entry.getValue());
         first = false;
      }
      sb.append("\n  },\n");
      sb.append("  \"unlockedAchievements\": [");
      boolean firstAch = true;
      for (String ach : unlockedAchievements) {
         if (!firstAch) sb.append(",");
         sb.append("\"").append(ach).append("\"");
         firstAch = false;
      }
      sb.append("]\n");
      sb.append("}");
      return sb.toString();
   }

   public static MetaProgression fromJson(String json) {
      MetaProgression p = new MetaProgression();
      // Parse simple key-value pairs from JSON
      String[] lines = json.split("\n");
      boolean inUpgrades = false;
      boolean inAchievements = false;
      for (String line : lines) {
         line = line.trim();
         if (line.startsWith("\"unlockedAchievements\"")) {
            inAchievements = true;
            // Inline parse: extract bracket content
            int bracketStart = line.indexOf('[');
            int bracketEnd = line.indexOf(']');
            if (bracketStart >= 0 && bracketEnd > bracketStart) {
               String content = line.substring(bracketStart + 1, bracketEnd);
               for (String token : content.split(",")) {
                  String achName = token.trim().replace("\"", "");
                  if (!achName.isEmpty()) {
                     p.unlockedAchievements.add(achName);
                  }
               }
               inAchievements = false;
            }
            continue;
         }
         if (inAchievements) {
            if (line.contains("]")) { inAchievements = false; continue; }
            String achName = line.replace("\"", "").replace(",", "").trim();
            if (!achName.isEmpty()) p.unlockedAchievements.add(achName);
            continue;
         }
         if (line.startsWith("\"upgradeLevels\"")) {
            inUpgrades = true;
            continue;
         }
         if (inUpgrades) {
            if (line.startsWith("}")) {
               inUpgrades = false;
               continue;
            }
            int colonIdx = line.indexOf(':');
            if (colonIdx < 0) continue;
            String key = line.substring(0, colonIdx).trim().replace("\"", "");
            String value = line.substring(colonIdx + 1).trim().replace(",", "");
            try {
               MetaUpgradeType type = MetaUpgradeType.valueOf(key);
               p.upgradeLevels.put(type, Integer.parseInt(value));
            } catch (IllegalArgumentException ignored) {
               // Unknown upgrade type from old save, skip
            }
            continue;
         }
         int colonIdx = line.indexOf(':');
         if (colonIdx < 0) continue;
         String key = line.substring(0, colonIdx).trim().replace("\"", "");
         String value = line.substring(colonIdx + 1).trim().replace(",", "");
         switch (key) {
            case "totalMoney": p.totalMoney = Integer.parseInt(value); break;
            case "lifetimeMoney": p.lifetimeMoney = Integer.parseInt(value); break;
            case "gamesPlayed": p.gamesPlayed = Integer.parseInt(value); break;
            case "totalKills": p.totalKills = Integer.parseInt(value); break;
            case "longestSurvivalTime": p.longestSurvivalTime = Double.parseDouble(value); break;
            case "highestWave": p.highestWave = Integer.parseInt(value); break;
            case "highestLevel": p.highestLevel = Integer.parseInt(value); break;
            default: break;
         }
      }
      return p;
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

   public Set<String> getUnlockedAchievements() {
      return this.unlockedAchievements;
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
