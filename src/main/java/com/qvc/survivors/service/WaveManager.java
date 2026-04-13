package com.qvc.survivors.service;

import com.qvc.survivors.model.entity.*;
import com.qvc.survivors.world.ZoneType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Generated;

public class WaveManager {
   private static final Random RANDOM = new Random();
   private static final double BASE_SPAWN_INTERVAL = 2.0;
   private static final double WAVE_DURATION = 30.0;
   private static final double TILE_SIZE = 15.0;
   private final double fieldWidth;
   private final double fieldHeight;
   private final EntityPoolManager entityPoolManager;
   private int currentWave;
   private double waveTimer;
   private double spawnTimer;
   private double spawnInterval;
   private double camX;
   private double camY;
   private double camViewW;
   private double camViewH;
   private double zoneMultiplier = 1.0;
   private ZoneType currentZone;
   private double totalZoneTime;
   private boolean bossActive;
   private BossEnemy activeBoss;
   private boolean bossSpawned;

   // Game time tracking for minute-based schedule
   private double gameTime;
   private int lastMinuteBoss = -1;
   private int auditorCount = 0;
   private boolean auditorSpawned = false;
   private double auditorSpawnTimer = 0.0;

   public WaveManager(double fieldWidth, double fieldHeight, EntityPoolManager entityPoolManager) {
      this.fieldWidth = fieldWidth;
      this.fieldHeight = fieldHeight;
      this.entityPoolManager = entityPoolManager;
      this.currentWave = 1;
      this.waveTimer = 0.0;
      this.spawnTimer = 0.0;
      this.spawnInterval = 2.0;
      this.camX = fieldWidth / 2.0;
      this.camY = fieldHeight / 2.0;
      this.camViewW = 1200;
      this.camViewH = 750;
      this.totalZoneTime = 0.0;
      this.bossActive = false;
      this.activeBoss = null;
      this.bossSpawned = false;
      this.gameTime = 0.0;
   }

   public void setCameraPosition(double camX, double camY, double viewW, double viewH) {
      this.camX = camX;
      this.camY = camY;
      this.camViewW = viewW;
      this.camViewH = viewH;
   }

   public void setCurrentZone(ZoneType zone) {
      if (zone != this.currentZone) {
         this.totalZoneTime = 0.0;
         this.bossSpawned = false;
      }
      this.currentZone = zone;
      this.zoneMultiplier = zone != null ? zone.getDifficultyMultiplier() : 1.0;
   }

   public void reset() {
      this.currentWave = 1;
      this.waveTimer = 0.0;
      this.spawnTimer = 0.0;
      this.spawnInterval = 2.0;
      this.totalZoneTime = 0.0;
      this.bossActive = false;
      this.activeBoss = null;
      this.bossSpawned = false;
      this.gameTime = 0.0;
      this.lastMinuteBoss = -1;
      this.auditorCount = 0;
      this.auditorSpawned = false;
      this.auditorSpawnTimer = 0.0;
   }

   public void update(double deltaTime) {
      this.waveTimer += deltaTime;
      this.spawnTimer += deltaTime;
      this.totalZoneTime += deltaTime;
      this.gameTime += deltaTime;
      if (this.waveTimer >= WAVE_DURATION) {
         this.nextWave();
      }
      // Auditor spawn timer (every 60s after 30:00)
      if (gameTime >= 1800.0) {
         auditorSpawnTimer += deltaTime;
      }
   }

   private void nextWave() {
      this.currentWave++;
      this.waveTimer = 0.0;
      this.spawnInterval = BASE_SPAWN_INTERVAL / (1.0 + this.currentWave * 0.1) / this.zoneMultiplier;
   }

   /** Returns the current game minute (0-based). */
   public int getGameMinute() {
      return (int)(gameTime / 60.0);
   }

   /** Returns chest tier for boss drops based on game time. */
   public int getBossChestTier() {
      int minute = getGameMinute();
      if (minute >= 20 || minute == 11 || minute == 21) {
         return TreasureChest.TIER_GOLD;
      } else if (minute >= 10) {
         return TreasureChest.TIER_SILVER;
      }
      return TreasureChest.TIER_BRONZE;
   }

   /**
    * Check if a scheduled boss should spawn based on game time.
    * Returns a BossEnemy if one should spawn, null otherwise.
    */
   public BossEnemy checkBossSpawn() {
      if (bossActive) return null;

      int minute = getGameMinute();

      // Check for The Auditor at 30:00+
      if (minute >= 30 && auditorSpawnTimer >= 60.0) {
         auditorSpawnTimer -= 60.0;
         auditorCount++;
         return spawnAuditor();
      }

      // Check minute-based boss schedule
      if (minute != lastMinuteBoss && shouldSpawnBoss(minute)) {
         lastMinuteBoss = minute;
         bossActive = true;

         double angle = RANDOM.nextDouble() * Math.PI * 2.0;
         double dist = 10.0;
         double bossX = Math.max(5, Math.min(fieldWidth - 5, camX + Math.cos(angle) * dist));
         double bossY = Math.max(5, Math.min(fieldHeight - 5, camY + Math.sin(angle) * dist));

         BossEnemy boss = createBossForMinute(minute, bossX, bossY);
         if (boss != null) {
            this.activeBoss = boss;
         }
         return boss;
      }

      // Fallback: zone-based boss spawn (existing system)
      if (!bossSpawned && currentZone != null) {
         double threshold = currentZone == ZoneType.CORPORATE ? 720.0 : 600.0;
         if (totalZoneTime >= threshold) {
            bossSpawned = true;
            double angle = RANDOM.nextDouble() * Math.PI * 2.0;
            double dist = 10.0;
            double bossX = Math.max(5, Math.min(fieldWidth - 5, camX + Math.cos(angle) * dist));
            double bossY = Math.max(5, Math.min(fieldHeight - 5, camY + Math.sin(angle) * dist));

            BossEnemy boss = createBossForZone(currentZone, bossX, bossY);
            if (boss != null) {
               if (currentZone == ZoneType.CORPORATE) {
                  double hp = boss.getHealthComponent().getMaxHealth() * 1.3;
                  boss.getHealthComponent().setMaxHealth(hp);
                  boss.getHealthComponent().heal(hp);
                  boss.getDamageComponent().setDamage(boss.getDamageComponent().getDamage() * 1.3);
               }
               this.activeBoss = boss;
               this.bossActive = true;
            }
            return boss;
         }
      }

      return null;
   }

   private boolean shouldSpawnBoss(int minute) {
      return minute == 5 || minute == 10 || minute == 15 || minute == 20 || minute == 25;
   }

   private BossEnemy createBossForMinute(int minute, double x, double y) {
      return switch (minute) {
         case 5 -> new WarehouseManager(x, y); // Floor Manager mini-boss
         case 10 -> new ExecutiveProducer(x, y); // Zone Boss
         case 15 -> new DoorManager(x, y); // Zone Boss
         case 20 -> new BoardOfDirectors(x, y); // Mega Boss
         case 25 -> new ReturnFraudKingpin(x, y); // Final Zone Boss
         default -> null;
      };
   }

   private BossEnemy spawnAuditor() {
      double angle = RANDOM.nextDouble() * Math.PI * 2.0;
      double dist = 12.0;
      double ax = Math.max(5, Math.min(fieldWidth - 5, camX + Math.cos(angle) * dist));
      double ay = Math.max(5, Math.min(fieldHeight - 5, camY + Math.sin(angle) * dist));
      // The Auditor is a BoardOfDirectors with massively boosted stats
      BoardOfDirectors auditor = new BoardOfDirectors(ax, ay);
      double playerLevel = Math.max(1, currentWave);
      auditor.getHealthComponent().setMaxHealth(655350.0 * playerLevel);
      auditor.getHealthComponent().heal(655350.0 * playerLevel);
      auditor.getDamageComponent().setDamage(65535.0);
      auditor.getMovementComponent().setSpeed(50.0);
      this.bossActive = true;
      this.activeBoss = auditor;
      return auditor;
   }

   private BossEnemy createBossForZone(ZoneType zone, double x, double y) {
      return switch (zone) {
         case SOUNDSTAGE -> new ExecutiveProducer(x, y);
         case WAREHOUSE -> new WarehouseManager(x, y);
         case MALL -> new DoorManager(x, y);
         case RETURNS -> new ReturnFraudKingpin(x, y);
         case CORPORATE -> new BoardOfDirectors(x, y);
      };
   }

   public void onBossDefeated() {
      this.bossActive = false;
      this.activeBoss = null;
   }

   public List<Enemy> spawnEnemies() {
      List<Enemy> enemies = new ArrayList<>();
      if (bossActive) return enemies;

      if (this.spawnTimer >= this.spawnInterval) {
         this.spawnTimer = 0.0;
         int minute = getGameMinute();
         int spawnCount = getSpawnCountForMinute(minute);

         // Mall zone gets 1.5x spawn rate
         if (currentZone == ZoneType.MALL) {
            spawnCount = (int) Math.ceil(spawnCount * 1.5);
         }

         for (int i = 0; i < spawnCount; i++) {
            enemies.add(this.createEnemy());
         }
      }

      return enemies;
   }

   private int getSpawnCountForMinute(int minute) {
      if (minute < 1) return 1;
      if (minute < 3) return 2;
      if (minute < 5) return 3;
      if (minute < 8) return 4;
      if (minute < 10) return 5;
      if (minute < 15) return 6;
      if (minute < 20) return 8;
      if (minute < 25) return 10;
      if (minute < 30) return 12;
      return 15; // Post-Auditor
   }

   private Enemy createEnemy() {
      int side = RANDOM.nextInt(4);
      double halfViewW = (this.camViewW / TILE_SIZE) / 2.0 + 5;
      double halfViewH = (this.camViewH / TILE_SIZE) / 2.0 + 5;

      double spawnX;
      double spawnY;
      switch (side) {
         case 0:
            spawnX = this.camX + (RANDOM.nextDouble() * 2.0 - 1.0) * halfViewW;
            spawnY = this.camY - halfViewH;
            break;
         case 1:
            spawnX = this.camX + halfViewW;
            spawnY = this.camY + (RANDOM.nextDouble() * 2.0 - 1.0) * halfViewH;
            break;
         case 2:
            spawnX = this.camX + (RANDOM.nextDouble() * 2.0 - 1.0) * halfViewW;
            spawnY = this.camY + halfViewH;
            break;
         default:
            spawnX = this.camX - halfViewW;
            spawnY = this.camY + (RANDOM.nextDouble() * 2.0 - 1.0) * halfViewH;
            break;
      }

      spawnX = Math.max(0, Math.min(this.fieldWidth, spawnX));
      spawnY = Math.max(0, Math.min(this.fieldHeight, spawnY));

      EnemyType type = selectEnemyType();

      Enemy enemy;
      if (type == EnemyType.REGULAR_CUSTOMER) {
         enemy = this.entityPoolManager.obtainRegularCustomer(spawnX, spawnY);
      } else if (type == EnemyType.VIP_CUSTOMER) {
         enemy = this.entityPoolManager.obtainVIPCustomer(spawnX, spawnY);
      } else {
         enemy = this.entityPoolManager.obtainGenericEnemy(spawnX, spawnY, type);
      }

      if (this.zoneMultiplier > 1.0) {
         double currentMax = enemy.getHealthComponent().getMaxHealth();
         enemy.getHealthComponent().setMaxHealth(currentMax * this.zoneMultiplier);
         enemy.getHealthComponent().heal(currentMax * this.zoneMultiplier);
         enemy.getDamageComponent().setDamage(enemy.getDamageComponent().getDamage() * this.zoneMultiplier);
      }

      if (currentZone == ZoneType.CORPORATE) {
         double hp = enemy.getHealthComponent().getMaxHealth() * 1.3;
         enemy.getHealthComponent().setMaxHealth(hp);
         enemy.getHealthComponent().heal(hp);
         enemy.getDamageComponent().setDamage(enemy.getDamageComponent().getDamage() * 1.3);
      }

      // Game time scaling: enemies get harder over time
      int minute = getGameMinute();
      if (minute >= 10) {
         double timeScale = 1.0 + (minute - 10) * 0.1;
         double hp = enemy.getHealthComponent().getMaxHealth() * timeScale;
         enemy.getHealthComponent().setMaxHealth(hp);
         enemy.getHealthComponent().heal(hp);
      }

      return enemy;
   }

   private EnemyType selectEnemyType() {
      int minute = getGameMinute();
      double roll = RANDOM.nextDouble();

      // Minute-based enemy selection (overrides zone selection for time-scaled play)
      if (minute >= 25) {
         // Mystery Boxes everywhere + all types
         if (roll < 0.15) return EnemyType.MYSTERY_BOX;
         return selectMixedEnemy(roll * 1.18); // scale roll past mystery box
      }
      if (minute >= 20) {
         // All types, triple density handled by spawn count
         return selectAllTypesEnemy(roll);
      }
      if (minute >= 15) {
         return selectAllTypesEnemy(roll);
      }
      if (minute >= 10) {
         return selectMixedEnemy(roll);
      }
      if (minute >= 8) {
         // Return Fraudsters + Scalper Bots heavy
         if (roll < 0.25) return EnemyType.RETURN_FRAUDSTER;
         if (roll < 0.50) return EnemyType.SCALPER_BOT;
         if (roll < 0.70) return EnemyType.VIP_CUSTOMER;
         return selectFromZoneOrDefault(roll);
      }
      if (minute >= 5) {
         // Cart Pushers + Influencers
         if (roll < 0.20) return EnemyType.CART_PUSHER;
         if (roll < 0.35) return EnemyType.INFLUENCER;
         return selectFromZoneOrDefault(roll);
      }
      if (minute >= 3) {
         // Karens + Regular
         if (roll < 0.25) return EnemyType.KAREN;
         return selectFromZoneOrDefault(roll);
      }
      if (minute >= 2) {
         // Coupon Clippers swarm
         if (roll < 0.40) return EnemyType.COUPON_CLIPPER;
         return selectFromZoneOrDefault(roll);
      }
      if (minute >= 1) {
         // Regular + VIP
         if (roll < 0.70) return EnemyType.REGULAR_CUSTOMER;
         return EnemyType.VIP_CUSTOMER;
      }
      // Minute 0: Regular Customers only
      return EnemyType.REGULAR_CUSTOMER;
   }

   private EnemyType selectFromZoneOrDefault(double roll) {
      if (currentZone == null) {
         return roll < 0.65 ? EnemyType.REGULAR_CUSTOMER : EnemyType.VIP_CUSTOMER;
      }
      return switch (currentZone) {
         case SOUNDSTAGE -> selectSoundstageEnemy(roll);
         case WAREHOUSE -> selectWarehouseEnemy(roll);
         case MALL -> selectMallEnemy(roll);
         case RETURNS -> selectReturnsEnemy(roll);
         case CORPORATE -> selectCorporateEnemy(roll);
      };
   }

   private EnemyType selectMixedEnemy(double roll) {
      if (roll < 0.10) return EnemyType.REGULAR_CUSTOMER;
      if (roll < 0.20) return EnemyType.VIP_CUSTOMER;
      if (roll < 0.30) return EnemyType.KAREN;
      if (roll < 0.40) return EnemyType.COUPON_CLIPPER;
      if (roll < 0.50) return EnemyType.CART_PUSHER;
      if (roll < 0.60) return EnemyType.SCALPER_BOT;
      if (roll < 0.70) return EnemyType.INFLUENCER;
      if (roll < 0.80) return EnemyType.RETURN_FRAUDSTER;
      if (roll < 0.90) return EnemyType.QVC_SUPERFAN;
      return EnemyType.MYSTERY_BOX;
   }

   private EnemyType selectAllTypesEnemy(double roll) {
      if (roll < 0.08) return EnemyType.REGULAR_CUSTOMER;
      if (roll < 0.16) return EnemyType.VIP_CUSTOMER;
      if (roll < 0.26) return EnemyType.KAREN;
      if (roll < 0.36) return EnemyType.COUPON_CLIPPER;
      if (roll < 0.46) return EnemyType.CART_PUSHER;
      if (roll < 0.56) return EnemyType.SCALPER_BOT;
      if (roll < 0.66) return EnemyType.INFLUENCER;
      if (roll < 0.76) return EnemyType.RETURN_FRAUDSTER;
      if (roll < 0.88) return EnemyType.QVC_SUPERFAN;
      return EnemyType.MYSTERY_BOX;
   }

   private EnemyType selectSoundstageEnemy(double roll) {
      if (currentWave <= 3) {
         if (roll < 0.80) return EnemyType.REGULAR_CUSTOMER;
         if (roll < 0.95) return EnemyType.VIP_CUSTOMER;
         return EnemyType.COUPON_CLIPPER;
      } else if (currentWave <= 6) {
         if (roll < 0.50) return EnemyType.REGULAR_CUSTOMER;
         if (roll < 0.70) return EnemyType.VIP_CUSTOMER;
         if (roll < 0.85) return EnemyType.COUPON_CLIPPER;
         if (roll < 0.95) return EnemyType.KAREN;
         return EnemyType.SCALPER_BOT;
      } else {
         if (roll < 0.25) return EnemyType.REGULAR_CUSTOMER;
         if (roll < 0.45) return EnemyType.VIP_CUSTOMER;
         if (roll < 0.55) return EnemyType.COUPON_CLIPPER;
         if (roll < 0.70) return EnemyType.KAREN;
         if (roll < 0.80) return EnemyType.SCALPER_BOT;
         if (roll < 0.87) return EnemyType.INFLUENCER;
         if (roll < 0.94) return EnemyType.QVC_SUPERFAN;
         return EnemyType.CART_PUSHER;
      }
   }

   private EnemyType selectWarehouseEnemy(double roll) {
      if (currentWave <= 3) {
         if (roll < 0.40) return EnemyType.REGULAR_CUSTOMER;
         if (roll < 0.55) return EnemyType.VIP_CUSTOMER;
         if (roll < 0.75) return EnemyType.CART_PUSHER;
         if (roll < 0.90) return EnemyType.RETURN_FRAUDSTER;
         return EnemyType.KAREN;
      } else {
         if (roll < 0.20) return EnemyType.REGULAR_CUSTOMER;
         if (roll < 0.35) return EnemyType.VIP_CUSTOMER;
         if (roll < 0.55) return EnemyType.CART_PUSHER;
         if (roll < 0.75) return EnemyType.RETURN_FRAUDSTER;
         if (roll < 0.85) return EnemyType.KAREN;
         if (roll < 0.93) return EnemyType.SCALPER_BOT;
         return EnemyType.QVC_SUPERFAN;
      }
   }

   private EnemyType selectMallEnemy(double roll) {
      if (currentWave <= 3) {
         if (roll < 0.30) return EnemyType.REGULAR_CUSTOMER;
         if (roll < 0.45) return EnemyType.VIP_CUSTOMER;
         if (roll < 0.65) return EnemyType.COUPON_CLIPPER;
         if (roll < 0.80) return EnemyType.KAREN;
         if (roll < 0.90) return EnemyType.INFLUENCER;
         return EnemyType.SCALPER_BOT;
      } else {
         if (roll < 0.15) return EnemyType.REGULAR_CUSTOMER;
         if (roll < 0.30) return EnemyType.VIP_CUSTOMER;
         if (roll < 0.50) return EnemyType.COUPON_CLIPPER;
         if (roll < 0.70) return EnemyType.KAREN;
         if (roll < 0.80) return EnemyType.INFLUENCER;
         if (roll < 0.90) return EnemyType.QVC_SUPERFAN;
         return EnemyType.SCALPER_BOT;
      }
   }

   private EnemyType selectReturnsEnemy(double roll) {
      boolean hasMysteryBoxes = currentWave >= 3;
      if (currentWave <= 2) {
         if (roll < 0.30) return EnemyType.REGULAR_CUSTOMER;
         if (roll < 0.45) return EnemyType.VIP_CUSTOMER;
         if (roll < 0.75) return EnemyType.RETURN_FRAUDSTER;
         if (roll < 0.85) return EnemyType.KAREN;
         return EnemyType.CART_PUSHER;
      } else {
         if (roll < 0.15) return EnemyType.REGULAR_CUSTOMER;
         if (roll < 0.25) return EnemyType.VIP_CUSTOMER;
         if (roll < 0.55) return EnemyType.RETURN_FRAUDSTER;
         if (roll < 0.65) return EnemyType.KAREN;
         if (roll < 0.75) return EnemyType.CART_PUSHER;
         if (roll < 0.85) return EnemyType.SCALPER_BOT;
         if (hasMysteryBoxes && roll < 0.92) return EnemyType.MYSTERY_BOX;
         return EnemyType.QVC_SUPERFAN;
      }
   }

   private EnemyType selectCorporateEnemy(double roll) {
      boolean hasMysteryBoxes = currentWave % 2 == 0;
      if (roll < 0.10) return EnemyType.REGULAR_CUSTOMER;
      if (roll < 0.20) return EnemyType.VIP_CUSTOMER;
      if (roll < 0.30) return EnemyType.KAREN;
      if (roll < 0.40) return EnemyType.COUPON_CLIPPER;
      if (roll < 0.50) return EnemyType.CART_PUSHER;
      if (roll < 0.60) return EnemyType.SCALPER_BOT;
      if (roll < 0.70) return EnemyType.INFLUENCER;
      if (roll < 0.80) return EnemyType.RETURN_FRAUDSTER;
      if (roll < 0.90) return EnemyType.QVC_SUPERFAN;
      if (hasMysteryBoxes) return EnemyType.MYSTERY_BOX;
      return EnemyType.VIP_CUSTOMER;
   }

   @Generated
   public double getFieldWidth() {
      return this.fieldWidth;
   }

   @Generated
   public double getFieldHeight() {
      return this.fieldHeight;
   }

   @Generated
   public EntityPoolManager getEntityPoolManager() {
      return this.entityPoolManager;
   }

   @Generated
   public int getCurrentWave() {
      return this.currentWave;
   }

   @Generated
   public double getWaveTimer() {
      return this.waveTimer;
   }

   @Generated
   public double getSpawnTimer() {
      return this.spawnTimer;
   }

   @Generated
   public double getSpawnInterval() {
      return this.spawnInterval;
   }

   public boolean isBossActive() {
      return this.bossActive;
   }

   public BossEnemy getActiveBoss() {
      return this.activeBoss;
   }

   public double getTotalZoneTime() {
      return this.totalZoneTime;
   }

   public double getGameTime() {
      return this.gameTime;
   }

   public boolean isAuditorPhase() {
      return gameTime >= 1800.0;
   }
}
