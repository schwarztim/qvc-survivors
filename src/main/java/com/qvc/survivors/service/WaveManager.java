package com.qvc.survivors.service;

import com.qvc.survivors.model.entity.Enemy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Generated;

public class WaveManager {
   private static final Random RANDOM = new Random();
   private static final double BASE_SPAWN_INTERVAL = 2.0;
   private static final double WAVE_DURATION = 30.0;
   private final double fieldWidth;
   private final double fieldHeight;
   private final EntityPoolManager entityPoolManager;
   private int currentWave;
   private double waveTimer;
   private double spawnTimer;
   private double spawnInterval;

   public WaveManager(double fieldWidth, double fieldHeight, EntityPoolManager entityPoolManager) {
      this.fieldWidth = fieldWidth;
      this.fieldHeight = fieldHeight;
      this.entityPoolManager = entityPoolManager;
      this.currentWave = 1;
      this.waveTimer = 0.0;
      this.spawnTimer = 0.0;
      this.spawnInterval = 2.0;
   }

   public void reset() {
      this.currentWave = 1;
      this.waveTimer = 0.0;
      this.spawnTimer = 0.0;
      this.spawnInterval = 2.0;
   }

   public void update(double deltaTime) {
      this.waveTimer += deltaTime;
      this.spawnTimer += deltaTime;
      if (this.waveTimer >= 30.0) {
         this.nextWave();
      }
   }

   private void nextWave() {
      this.currentWave++;
      this.waveTimer = 0.0;
      this.spawnInterval = 2.0 / (1.0 + this.currentWave * 0.1);
   }

   public List<Enemy> spawnEnemies() {
      List<Enemy> enemies = new ArrayList<>();
      if (this.spawnTimer >= this.spawnInterval) {
         this.spawnTimer = 0.0;
         int spawnCount = 1 + (this.currentWave - 1) / 3;

         for (int i = 0; i < spawnCount; i++) {
            enemies.add(this.createEnemy());
         }
      }

      return enemies;
   }

   private Enemy createEnemy() {
      int side = RANDOM.nextInt(4);

      record SpawnPosition(double x, double y) {
      }


      SpawnPosition position = switch (side) {
         case 0 -> new SpawnPosition(RANDOM.nextDouble() * this.fieldWidth, -5.0);
         case 1 -> new SpawnPosition(this.fieldWidth + 5.0, RANDOM.nextDouble() * this.fieldHeight);
         case 2 -> new SpawnPosition(RANDOM.nextDouble() * this.fieldWidth, this.fieldHeight + 5.0);
         default -> new SpawnPosition(-5.0, RANDOM.nextDouble() * this.fieldHeight);
      };
      double vipChance = 0.15 + this.currentWave * 0.02;
      boolean isVIP = RANDOM.nextDouble() < vipChance;
      return (Enemy)(isVIP
         ? this.entityPoolManager.obtainVIPCustomer(position.x, position.y)
         : this.entityPoolManager.obtainRegularCustomer(position.x, position.y));
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
}
