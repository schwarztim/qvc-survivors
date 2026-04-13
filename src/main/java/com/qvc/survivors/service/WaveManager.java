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
   private static final double BOSS_SPAWN_TIME = 600.0; // 10 minutes
   private static final double CORPORATE_BOSS_SPAWN_TIME = 720.0; // 12 minutes
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
   }

   public void update(double deltaTime) {
      this.waveTimer += deltaTime;
      this.spawnTimer += deltaTime;
      this.totalZoneTime += deltaTime;
      if (this.waveTimer >= 30.0) {
         this.nextWave();
      }
   }

   private void nextWave() {
      this.currentWave++;
      this.waveTimer = 0.0;
      this.spawnInterval = 2.0 / (1.0 + this.currentWave * 0.1) / this.zoneMultiplier;
   }

   public BossEnemy checkBossSpawn() {
      if (bossSpawned || bossActive || currentZone == null) return null;

      double threshold = currentZone == ZoneType.CORPORATE ? CORPORATE_BOSS_SPAWN_TIME : BOSS_SPAWN_TIME;
      if (totalZoneTime >= threshold) {
         bossSpawned = true;
         double angle = RANDOM.nextDouble() * Math.PI * 2.0;
         double dist = 10.0;
         double bossX = camX + Math.cos(angle) * dist;
         double bossY = camY + Math.sin(angle) * dist;
         bossX = Math.max(5, Math.min(fieldWidth - 5, bossX));
         bossY = Math.max(5, Math.min(fieldHeight - 5, bossY));

         BossEnemy boss = createBossForZone(currentZone, bossX, bossY);
         if (boss != null) {
            // Apply corporate stat boost
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
      return null;
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
      if (bossActive) return enemies; // No normal spawns during boss fight

      if (this.spawnTimer >= this.spawnInterval) {
         this.spawnTimer = 0.0;
         int spawnCount = 1 + (this.currentWave - 1) / 3;

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

      // Use legacy pool for basic types, generic pool for new types
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

      // Corporate zone +30% stats
      if (currentZone == ZoneType.CORPORATE) {
         double hp = enemy.getHealthComponent().getMaxHealth() * 1.3;
         enemy.getHealthComponent().setMaxHealth(hp);
         enemy.getHealthComponent().heal(hp);
         enemy.getDamageComponent().setDamage(enemy.getDamageComponent().getDamage() * 1.3);
      }

      return enemy;
   }

   private EnemyType selectEnemyType() {
      double roll = RANDOM.nextDouble();
      if (currentZone == null) {
         return roll < 0.85 ? EnemyType.REGULAR_CUSTOMER : EnemyType.VIP_CUSTOMER;
      }

      return switch (currentZone) {
         case SOUNDSTAGE -> selectSoundstageEnemy(roll);
         case WAREHOUSE -> selectWarehouseEnemy(roll);
         case MALL -> selectMallEnemy(roll);
         case RETURNS -> selectReturnsEnemy(roll);
         case CORPORATE -> selectCorporateEnemy(roll);
      };
   }

   private EnemyType selectSoundstageEnemy(double roll) {
      if (currentWave <= 3) {
         // Regular 80%, VIP 15%, Coupon Clipper 5%
         if (roll < 0.80) return EnemyType.REGULAR_CUSTOMER;
         if (roll < 0.95) return EnemyType.VIP_CUSTOMER;
         return EnemyType.COUPON_CLIPPER;
      } else if (currentWave <= 6) {
         // Regular 50%, VIP 20%, Coupon 15%, Karen 10%, Scalper 5%
         if (roll < 0.50) return EnemyType.REGULAR_CUSTOMER;
         if (roll < 0.70) return EnemyType.VIP_CUSTOMER;
         if (roll < 0.85) return EnemyType.COUPON_CLIPPER;
         if (roll < 0.95) return EnemyType.KAREN;
         return EnemyType.SCALPER_BOT;
      } else {
         // Wave 7+: all types, increasing VIP/Karen rates
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
      // Heavy on Cart Pushers and Return Fraudsters
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
      // High volume, lots of Clippers and Karens
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
      // Return Fraudsters dominant, Mystery Boxes from wave 3
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
      // All types from wave 1, Mystery Boxes every 2 waves
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
}
