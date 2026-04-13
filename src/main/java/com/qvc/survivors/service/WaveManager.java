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
   }

   public void setCameraPosition(double camX, double camY, double viewW, double viewH) {
      this.camX = camX;
      this.camY = camY;
      this.camViewW = viewW;
      this.camViewH = viewH;
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

      double halfViewW = (this.camViewW / TILE_SIZE) / 2.0 + 5;
      double halfViewH = (this.camViewH / TILE_SIZE) / 2.0 + 5;

      double spawnX;
      double spawnY;
      switch (side) {
         case 0: // top
            spawnX = this.camX + (RANDOM.nextDouble() * 2.0 - 1.0) * halfViewW;
            spawnY = this.camY - halfViewH;
            break;
         case 1: // right
            spawnX = this.camX + halfViewW;
            spawnY = this.camY + (RANDOM.nextDouble() * 2.0 - 1.0) * halfViewH;
            break;
         case 2: // bottom
            spawnX = this.camX + (RANDOM.nextDouble() * 2.0 - 1.0) * halfViewW;
            spawnY = this.camY + halfViewH;
            break;
         default: // left
            spawnX = this.camX - halfViewW;
            spawnY = this.camY + (RANDOM.nextDouble() * 2.0 - 1.0) * halfViewH;
            break;
      }

      spawnX = Math.max(0, Math.min(this.fieldWidth, spawnX));
      spawnY = Math.max(0, Math.min(this.fieldHeight, spawnY));

      SpawnPosition position = new SpawnPosition(spawnX, spawnY);
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
