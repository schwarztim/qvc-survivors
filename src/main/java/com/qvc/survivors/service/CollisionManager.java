package com.qvc.survivors.service;

import com.qvc.survivors.model.entity.Collectible;
import com.qvc.survivors.model.entity.Drone;
import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Entity;
import com.qvc.survivors.model.entity.Player;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.upgrade.StatModifier;
import com.qvc.survivors.util.SpatialGrid;
import java.util.List;

public class CollisionManager {
   private static final int SPATIAL_GRID_CELL_SIZE = 5;
   private final SpatialGrid spatialGrid;

   public CollisionManager(int worldWidth, int worldHeight) {
      this.spatialGrid = new SpatialGrid(worldWidth, worldHeight, 5);
   }

   public boolean checkCollision(Entity entity1, Entity entity2) {
      return entity1.collidesWith(entity2);
   }

   public void checkProjectileEnemyCollisions(List<Projectile> projectiles, List<Enemy> enemies, Player player) {
      for (Projectile projectile : projectiles) {
         if (projectile.isActive()) {
            for (Enemy enemy : enemies) {
               if (enemy.isActive() && projectile.collidesWith(enemy)) {
                  enemy.takeDamage(projectile.getDamageComponent().getDamage());
                  projectile.setActive(false);
                  if (!enemy.getHealthComponent().isAlive()) {
                     player.incrementCustomersSatisfied();
                  }
                  break;
               }
            }
         }
      }
   }

   public void checkDroneEnemyCollisions(List<Drone> drones, List<Enemy> enemies, Player player) {
      for (Drone drone : drones) {
         for (Enemy enemy : enemies) {
            if (enemy.isActive() && drone.collidesWith(enemy)) {
               double damage = drone.getDamageComponent().getDamage();
               double critChance = player.getStats().getStat(StatModifier.CRITICAL_CHANCE);
               if (Math.random() < critChance) {
                  damage *= 2.0;
               }

               enemy.takeDamage(damage);
               if (!enemy.getHealthComponent().isAlive()) {
                  player.incrementCustomersSatisfied();
               }
            }
         }
      }
   }

   public void checkPlayerEnemyCollisions(Player player, List<Enemy> enemies, SoundEffectGenerator soundGenerator) {
      checkPlayerEnemyCollisions(player, enemies, (Runnable) soundGenerator::playPlayerHurtSound);
   }

   public void checkPlayerEnemyCollisions(Player player, List<Enemy> enemies, Runnable hurtSound) {
      for (Enemy enemy : enemies) {
         if (enemy.isActive() && player.collidesWith(enemy)) {
            player.takeDamage(enemy.getDamageComponent().getDamage());
            if (player.isActive()) {
               hurtSound.run();
            }
         }
      }
   }

   public void checkPlayerCollectibleCollisions(Player player, List<Collectible> collectibles) {
      double pickupRange = player.getStats().getStat(StatModifier.PICKUP_RANGE);

      for (Collectible collectible : collectibles) {
         if (collectible.isActive()) {
            double distance = Math.sqrt(Math.pow(player.getX() - collectible.getX(), 2.0) + Math.pow(player.getY() - collectible.getY(), 2.0));
            if (distance <= pickupRange) {
               if (collectible.isHealthPack()) {
                  player.getHealthComponent().heal(collectible.getValue());
               } else {
                  player.addExperience(collectible.getValue());
               }

               collectible.setActive(false);
            }
         }
      }
   }

   public void checkEnemyEnemyCollisions(List<Enemy> enemies) {
      this.spatialGrid.clear();

      for (Enemy enemy : enemies) {
         if (enemy.isActive()) {
            this.spatialGrid.insert(enemy);
         }
      }

      for (Enemy enemy1 : enemies) {
         if (enemy1.isActive()) {
            for (Entity entity : this.spatialGrid.query(enemy1)) {
               if (entity instanceof Enemy enemy2 && enemy2.isActive() && enemy1.collidesWith(enemy2)) {
                  this.separateEnemies(enemy1, enemy2);
               }
            }
         }
      }
   }

   private void separateEnemies(Enemy enemy1, Enemy enemy2) {
      double centerX1 = enemy1.getX() + enemy1.getWidth() / 2.0;
      double centerY1 = enemy1.getY() + enemy1.getHeight() / 2.0;
      double centerX2 = enemy2.getX() + enemy2.getWidth() / 2.0;
      double centerY2 = enemy2.getY() + enemy2.getHeight() / 2.0;
      double dx = centerX2 - centerX1;
      double dy = centerY2 - centerY1;
      double distance = Math.sqrt(dx * dx + dy * dy);
      if (distance == 0.0) {
         distance = 0.01;
         dx = Math.random() - 0.5;
         dy = Math.random() - 0.5;
      }

      double separationForce = 0.5;
      double pushX = dx / distance * separationForce;
      double pushY = dy / distance * separationForce;
      enemy1.setX(enemy1.getX() - pushX);
      enemy1.setY(enemy1.getY() - pushY);
      enemy2.setX(enemy2.getX() + pushX);
      enemy2.setY(enemy2.getY() + pushY);
   }
}
