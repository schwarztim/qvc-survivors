package com.qvc.survivors.controller;

import com.qvc.survivors.model.GameState;
import com.qvc.survivors.model.entity.Collectible;
import com.qvc.survivors.model.entity.Drone;
import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.PackageEntity;
import com.qvc.survivors.model.entity.Player;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.entity.RegularCustomer;
import com.qvc.survivors.model.entity.VIPCustomer;
import com.qvc.survivors.model.meta.MetaUpgradeType;
import com.qvc.survivors.model.upgrade.StatModifier;
import com.qvc.survivors.model.upgrade.Upgrade;
import com.qvc.survivors.service.CollisionManager;
import com.qvc.survivors.service.EntityPoolManager;
import com.qvc.survivors.service.MetaProgressionManager;
import com.qvc.survivors.service.SoundEffectGenerator;
import com.qvc.survivors.service.UpgradeManager;
import com.qvc.survivors.service.WaveManager;
import com.qvc.survivors.view.GameView;
import com.qvc.survivors.view.HUDView;
import com.qvc.survivors.view.LevelUpView;
import com.qvc.survivors.view.MetaShopView;
import com.qvc.survivors.view.PerformanceMonitor;
import com.qvc.survivors.view.PreloaderView;
import com.qvc.survivors.view.TutorialView;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameController {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(GameController.class);
   private static final int GRID_WIDTH = 80;
   private static final int GRID_HEIGHT = 50;
   private final Stage stage;
   private final GameView gameView;
   private final HUDView hudView;
   private final LevelUpView levelUpView;
   private final PreloaderView preloaderView;
   private final TutorialView tutorialView;
   private final MetaShopView metaShopView;
   private final PerformanceMonitor performanceMonitor;
   private final InputHandler inputHandler;
   private final EntityPoolManager entityPoolManager;
   private final WaveManager waveManager;
   private final CollisionManager collisionManager;
   private final UpgradeManager upgradeManager;
   private final SoundEffectGenerator soundGenerator;
   private final MetaProgressionManager metaProgressionManager;
   private Player player;
   private List<Enemy> enemies;
   private List<Projectile> projectiles;
   private List<Collectible> collectibles;
   private List<Drone> drones;
   private GameState gameState;
   private List<Upgrade> currentUpgradeOptions;
   private long lastFrameTime;
   private long currentFrameTime;
   private boolean isTransitioning;
   private double transitionProgress;
   private GameState targetState;
   private static final double TRANSITION_DURATION = 0.5;
   private double preloaderAnimationTime;
   private boolean isFirstGame;
   private int selectedMetaUpgrade;
   private int moneyEarnedThisRun;

   public GameController(Stage stage, SoundEffectGenerator soundGenerator) {
      this.stage = stage;
      this.gameView = new GameView(80, 50);
      this.hudView = new HUDView(this.gameView);
      this.levelUpView = new LevelUpView(this.gameView);
      this.preloaderView = new PreloaderView(this.gameView);
      this.tutorialView = new TutorialView(this.gameView);
      this.metaShopView = new MetaShopView(this.gameView);
      this.performanceMonitor = new PerformanceMonitor();
      this.inputHandler = new InputHandler();
      this.entityPoolManager = new EntityPoolManager();
      this.waveManager = new WaveManager(80.0, 50.0, this.entityPoolManager);
      this.collisionManager = new CollisionManager(80, 50);
      this.upgradeManager = new UpgradeManager();
      this.soundGenerator = soundGenerator;
      this.metaProgressionManager = new MetaProgressionManager();
      this.preloaderAnimationTime = 0.0;
      this.isFirstGame = true;
      this.selectedMetaUpgrade = 0;
      this.moneyEarnedThisRun = 0;
      this.initializeGame();
      this.setupScene();
   }

   private void initializeGame() {
      this.player = new Player(40.0, 25.0, this.metaProgressionManager.getMetaProgression());
      this.enemies = new ArrayList<>();
      this.projectiles = new ArrayList<>();
      this.collectibles = new ArrayList<>();
      this.drones = new ArrayList<>();
      this.gameState = GameState.PRELOADER;
      this.currentUpgradeOptions = new ArrayList<>();
      this.isTransitioning = false;
      this.transitionProgress = 0.0;
      this.moneyEarnedThisRun = 0;
      this.targetState = null;
      this.soundGenerator.startIntroMusic();
   }

   private void setupScene() {
      StackPane root = new StackPane(new Node[]{this.gameView});
      Scene scene = new Scene(root);
      scene.setOnKeyPressed(event -> this.inputHandler.handleKeyPressed(event.getCode()));
      scene.setOnKeyReleased(event -> this.inputHandler.handleKeyReleased(event.getCode()));
      this.stage.setScene(scene);
      this.stage.setTitle("QVC Survivors");
      this.stage.setResizable(false);
      this.stage.show();
      this.startGameLoop();
   }

   private void startGameLoop() {
      this.lastFrameTime = System.nanoTime();
      AnimationTimer gameLoop = new AnimationTimer() {
         public void handle(long currentTime) {
            double deltaTime = (currentTime - GameController.this.lastFrameTime) / 1.0E9;
            long frameTime = currentTime - GameController.this.lastFrameTime;
            GameController.this.lastFrameTime = currentTime;
            GameController.this.performanceMonitor.update(deltaTime, frameTime);
            GameController.this.update(deltaTime);
            GameController.this.render();
         }
      };
      gameLoop.start();
   }

   private void update(double deltaTime) {
      this.currentFrameTime = System.currentTimeMillis();
      if (this.isTransitioning) {
         this.updateTransition(deltaTime);
      } else {
         switch (this.gameState) {
            case PRELOADER:
               this.updatePreloader(deltaTime);
               break;
            case TUTORIAL:
               this.updateTutorial();
               break;
            case PLAYING:
               this.updatePlaying(deltaTime);
               break;
            case LEVEL_UP:
               this.updateLevelUp();
               break;
            case GAME_OVER:
               this.updateGameOver();
               break;
            case META_SHOP:
               this.updateMetaShop();
               break;
            case PAUSED:
               this.updatePaused();
         }
      }
   }

   private void updateTransition(double deltaTime) {
      this.transitionProgress += deltaTime / 0.5;
      if (this.transitionProgress >= 1.0) {
         this.transitionProgress = 1.0;
         this.isTransitioning = false;
         this.gameState = this.targetState;
         this.targetState = null;
         if (this.gameState == GameState.PLAYING) {
            this.soundGenerator.stopIntroMusic();
         }
      }
   }

   private void startTransition(GameState newState) {
      this.isTransitioning = true;
      this.transitionProgress = 0.0;
      this.targetState = newState;
   }

   private void updatePreloader(double deltaTime) {
      this.preloaderAnimationTime += deltaTime;
      if (this.inputHandler.hasLastPressedKey()) {
         KeyCode key = this.inputHandler.getLastPressedKey();
         this.inputHandler.consumeLastPressedKey();
         if (key == KeyCode.SPACE) {
            GameState nextState = this.isFirstGame ? GameState.TUTORIAL : GameState.PLAYING;
            this.isFirstGame = false;
            this.startTransition(nextState);
         }
      }
   }

   private void updatePlaying(double deltaTime) {
      this.handlePlayerInput();
      this.player.update(deltaTime);
      this.constrainPlayerToBounds();
      this.gameView.updateParticles(deltaTime);
      this.waveManager.update(deltaTime);
      List<Enemy> newEnemies = this.waveManager.spawnEnemies();
      this.enemies.addAll(newEnemies);

      for (Enemy enemy : this.enemies) {
         if (enemy.isActive()) {
            enemy.moveTowards(this.player.getX(), this.player.getY());
            enemy.update(deltaTime);
         }
      }

      this.collisionManager.checkEnemyEnemyCollisions(this.enemies);
      if (this.player.canFire()) {
         boolean didFire = this.fireProjectiles();
         this.player.resetFireTimer();
         if (didFire) {
            this.soundGenerator.playShootSound();
         }
      }

      for (Projectile projectile : this.projectiles) {
         if (projectile.isActive()) {
            projectile.update(deltaTime);
            if (projectile.isOutOfBounds(80.0, 50.0)) {
               projectile.setActive(false);
            }

            if (Math.random() < 0.3) {
               this.gameView.getParticleSystem().createTrail(projectile.getX(), projectile.getY(), Color.LIGHTBLUE);
            }
         }
      }

      this.updateDrones(deltaTime);
      this.checkCollisionsWithEffects();
      this.spawnCollectiblesFromDeadEnemies();
      this.removeInactiveEntities();
      if (this.player.canLevelUp()) {
         this.currentUpgradeOptions = this.upgradeManager.generateUpgradeOptions();
         this.pushEnemiesAway();
         this.gameView.getParticleSystem().createLevelUpEffect(this.player.getX(), this.player.getY());
         this.soundGenerator.playLevelUpSound();
         this.gameState = GameState.LEVEL_UP;
      }

      if (!this.player.isActive()) {
         this.soundGenerator.playGameOverSound();
         this.gameState = GameState.GAME_OVER;
      }

      if (this.inputHandler.hasLastPressedKey()) {
         KeyCode key = this.inputHandler.getLastPressedKey();
         if (key == KeyCode.P || key == KeyCode.ESCAPE) {
            this.inputHandler.consumeLastPressedKey();
            this.gameState = GameState.PAUSED;
         }
      }
   }

   private void handlePlayerInput() {
      double dirX = 0.0;
      double dirY = 0.0;
      if (this.inputHandler.isKeyPressed(KeyCode.W) || this.inputHandler.isKeyPressed(KeyCode.UP)) {
         dirY--;
      }

      if (this.inputHandler.isKeyPressed(KeyCode.S) || this.inputHandler.isKeyPressed(KeyCode.DOWN)) {
         dirY++;
      }

      if (this.inputHandler.isKeyPressed(KeyCode.A) || this.inputHandler.isKeyPressed(KeyCode.LEFT)) {
         dirX--;
      }

      if (this.inputHandler.isKeyPressed(KeyCode.D) || this.inputHandler.isKeyPressed(KeyCode.RIGHT)) {
         dirX++;
      }

      this.player.getMovementComponent().setDirection(dirX, dirY);
   }

   private void constrainPlayerToBounds() {
      this.player.setX(Math.max(0.0, Math.min(79.0, this.player.getX())));
      this.player.setY(Math.max(0.0, Math.min(49.0, this.player.getY())));
   }

   private boolean fireProjectiles() {
      Enemy nearestEnemy = this.findNearestEnemy();
      if (nearestEnemy == null) {
         return false;
      } else {
         double packageDamage = this.player.getStats().getStat(StatModifier.PACKAGE_DAMAGE);
         double packageVelocity = this.player.getStats().getStat(StatModifier.PACKAGE_VELOCITY);
         int packageCapacity = (int)this.player.getStats().getStat(StatModifier.PACKAGE_CAPACITY);

         for (int i = 0; i < packageCapacity; i++) {
            double angleOffset = (i - packageCapacity / 2.0) * 0.2;
            double dirX = nearestEnemy.getX() - this.player.getX();
            double dirY = nearestEnemy.getY() - this.player.getY();
            double length = Math.sqrt(dirX * dirX + dirY * dirY);
            if (length > 0.0) {
               double angle = Math.atan2(dirY, dirX) + angleOffset;
               double velocityX = Math.cos(angle) * packageVelocity;
               double velocityY = Math.sin(angle) * packageVelocity;
               this.projectiles.add(this.entityPoolManager.obtainPackage(this.player.getX(), this.player.getY(), velocityX, velocityY, packageDamage));
            }
         }

         return true;
      }
   }

   private Enemy findNearestEnemy() {
      Enemy nearest = null;
      double minDistance = Double.MAX_VALUE;

      for (Enemy enemy : this.enemies) {
         if (enemy.isActive()) {
            double distance = Math.sqrt(Math.pow(enemy.getX() - this.player.getX(), 2.0) + Math.pow(enemy.getY() - this.player.getY(), 2.0));
            if (distance < minDistance) {
               minDistance = distance;
               nearest = enemy;
            }
         }
      }

      return nearest;
   }

   private void updateDrones(double deltaTime) {
      int droneCount = (int)this.player.getStats().getStat(StatModifier.DRONE_COUNT);

      while (this.drones.size() < droneCount) {
         double orbitRadius = 5.0;
         double orbitOffset = this.drones.size() * 2 * Math.PI / droneCount;
         double droneDamage = this.player.getStats().getStat(StatModifier.PACKAGE_DAMAGE) * 0.5;
         this.drones.add(new Drone(this.player.getX(), this.player.getY(), droneDamage, orbitRadius, orbitOffset));
      }

      for (Drone drone : this.drones) {
         drone.update(deltaTime);
         drone.updatePosition(this.player.getX(), this.player.getY());
         this.gameView.getParticleSystem().createDroneTrail(drone.getX(), drone.getY());
      }
   }

   private void checkCollisionsWithEffects() {
      for (Projectile projectile : this.projectiles) {
         if (projectile.isActive()) {
            for (Enemy enemy : this.enemies) {
               if (enemy.isActive() && this.collisionManager.checkCollision(projectile, enemy)) {
                  double damage = projectile.getDamageComponent().getDamage();
                  double critChance = this.player.getStats().getStat(StatModifier.CRITICAL_CHANCE);
                  boolean isCritical = Math.random() < critChance;
                  if (isCritical) {
                     damage *= 2.0;
                     this.gameView.getParticleSystem().createExplosion(enemy.getX(), enemy.getY(), Color.YELLOW, 15);
                  } else {
                     this.gameView.getParticleSystem().createImpact(projectile.getX(), projectile.getY(), Color.LIGHTBLUE);
                  }

                  enemy.takeDamage(damage);
                  projectile.setActive(false);
                  this.soundGenerator.playHitSound();
                  if (!enemy.isActive()) {
                     Color explosionColor = enemy instanceof VIPCustomer ? Color.RED : Color.ORANGE;
                     this.gameView.getParticleSystem().createExplosion(enemy.getX(), enemy.getY(), explosionColor, 20);
                     this.soundGenerator.playExplosionSound();
                     this.player.incrementCustomersSatisfied();
                  } else {
                     this.soundGenerator.playEnemyHurtSound();
                  }
                  break;
               }
            }
         }
      }

      for (Drone drone : this.drones) {
         for (Enemy enemyx : this.enemies) {
            if (enemyx.isActive() && this.collisionManager.checkCollision(drone, enemyx) && drone.canAttack()) {
               double damagex = drone.getDamageComponent().getDamage();
               double critChancex = this.player.getStats().getStat(StatModifier.CRITICAL_CHANCE);
               if (Math.random() < critChancex) {
                  damagex *= 2.0;
               }

               enemyx.takeDamage(damagex);
               drone.resetAttackTimer();
               this.gameView.getParticleSystem().createImpact(enemyx.getX(), enemyx.getY(), Color.LIGHTGREEN);
               this.soundGenerator.playHitSound();
               if (!enemyx.isActive()) {
                  Color explosionColor = enemyx instanceof VIPCustomer ? Color.RED : Color.ORANGE;
                  this.gameView.getParticleSystem().createExplosion(enemyx.getX(), enemyx.getY(), explosionColor, 20);
                  this.soundGenerator.playExplosionSound();
                  this.player.incrementCustomersSatisfied();
               } else {
                  this.soundGenerator.playEnemyHurtSound();
               }
            }
         }
      }

      this.collisionManager.checkPlayerEnemyCollisions(this.player, this.enemies, this.soundGenerator);
      double pickupRange = this.player.getStats().getStat(StatModifier.PICKUP_RANGE);

      for (Collectible collectible : this.collectibles) {
         if (collectible.isActive()) {
            double distance = Math.sqrt(Math.pow(this.player.getX() - collectible.getX(), 2.0) + Math.pow(this.player.getY() - collectible.getY(), 2.0));
            if (distance <= pickupRange) {
               if (collectible.isHealthPack()) {
                  this.player.getHealthComponent().heal(collectible.getValue());
                  this.gameView.getParticleSystem().createCollectionEffect(collectible.getX(), collectible.getY(), Color.LIGHTGREEN);
               } else {
                  int value = collectible.getValue();
                  this.player.addExperience(value);
                  this.moneyEarnedThisRun += value;
                  Color moneyColor = value >= 5 ? Color.GOLD : Color.YELLOW;
                  this.gameView.getParticleSystem().createCollectionEffect(collectible.getX(), collectible.getY(), moneyColor);
               }

               this.soundGenerator.playCollectSound();
               collectible.setActive(false);
            }
         }
      }
   }

   private void spawnCollectiblesFromDeadEnemies() {
      for (Enemy enemy : this.enemies) {
         if (!enemy.isActive() && enemy.getHealthComponent().getCurrentHealth() <= 0.0) {
            this.collectibles.add(this.entityPoolManager.obtainCollectible(enemy.getX(), enemy.getY(), enemy.getMoneyDrop(), false));
            double baseDropChance = 0.03;
            double bonusChance = this.metaProgressionManager.getMetaProgression().getUpgradeValue(MetaUpgradeType.HEALTH_PACK_DROP_CHANCE) / 100.0;
            double totalChance = baseDropChance + bonusChance;
            if (Math.random() < totalChance) {
               double healthX = enemy.getX() + 1.5;
               double healthY = enemy.getY() + 1.5;
               this.collectibles.add(this.entityPoolManager.obtainCollectible(healthX, healthY, 25, true));
            }
         }
      }
   }

   private void removeInactiveEntities() {
      for (Enemy enemy : this.enemies) {
         if (!enemy.isActive()) {
            if (enemy instanceof VIPCustomer vipCustomer) {
               this.entityPoolManager.freeVIPCustomer(vipCustomer);
            } else if (enemy instanceof RegularCustomer regularCustomer) {
               this.entityPoolManager.freeRegularCustomer(regularCustomer);
            }
         }
      }

      for (Projectile projectile : this.projectiles) {
         if (!projectile.isActive() && projectile instanceof PackageEntity packageEntity) {
            this.entityPoolManager.freePackage(packageEntity);
         }
      }

      for (Collectible collectible : this.collectibles) {
         if (!collectible.isActive()) {
            this.entityPoolManager.freeCollectible(collectible);
         }
      }

      this.enemies.removeIf(e -> !e.isActive());
      this.projectiles.removeIf(p -> !p.isActive());
      this.collectibles.removeIf(c -> !c.isActive());
   }

   private void pushEnemiesAway() {
      double knockbackDistance = 15.0;

      for (Enemy enemy : this.enemies) {
         if (enemy.isActive()) {
            double dirX = enemy.getX() - this.player.getX();
            double dirY = enemy.getY() - this.player.getY();
            double distance = Math.sqrt(dirX * dirX + dirY * dirY);
            if (distance > 0.0 && distance < 30.0) {
               double pushX = dirX / distance * knockbackDistance;
               double pushY = dirY / distance * knockbackDistance;
               enemy.setX(enemy.getX() + pushX);
               enemy.setY(enemy.getY() + pushY);
            }
         }
      }
   }

   private void updateLevelUp() {
      if (this.inputHandler.hasLastPressedKey()) {
         KeyCode key = this.inputHandler.consumeLastPressedKey();
         int choice = -1;
         if (key == KeyCode.DIGIT1 || key == KeyCode.NUMPAD1) {
            choice = 0;
         } else if (key == KeyCode.DIGIT2 || key == KeyCode.NUMPAD2) {
            choice = 1;
         } else if (key == KeyCode.DIGIT3 || key == KeyCode.NUMPAD3) {
            choice = 2;
         }

         if (choice >= 0 && choice < this.currentUpgradeOptions.size()) {
            Upgrade selectedUpgrade = this.currentUpgradeOptions.get(choice);
            this.player.getStats().applyUpgrade(selectedUpgrade);
            if (selectedUpgrade.getStatModifiers().containsKey(StatModifier.MAX_HEALTH)) {
               double healthIncrease = selectedUpgrade.getStatModifiers().get(StatModifier.MAX_HEALTH);
               this.player.getHealthComponent().increaseMaxHealth(healthIncrease);
            }

            this.player.levelUp();
            this.player.activateInvulnerability(2.5);
            this.gameState = GameState.PLAYING;
         }
      }
   }

   private void updateGameOver() {
      if (this.inputHandler.hasLastPressedKey()) {
         KeyCode key = this.inputHandler.consumeLastPressedKey();
         if (key == KeyCode.SPACE) {
            this.metaProgressionManager
               .getMetaProgression()
               .updateStats(
                  this.player.getCustomersSatisfied(),
                  this.player.getSurvivalTime(),
                  this.waveManager.getCurrentWave(),
                  this.player.getLevel(),
                  this.moneyEarnedThisRun
               );
            this.metaProgressionManager.save();
            this.selectedMetaUpgrade = 0;
            this.gameState = GameState.META_SHOP;
            this.soundGenerator.startMetaShopMusic();
         } else if (key == KeyCode.ESCAPE) {
            this.stage.close();
         }
      }
   }

   private void updatePaused() {
      if (this.inputHandler.hasLastPressedKey()) {
         KeyCode key = this.inputHandler.consumeLastPressedKey();
         if (key == KeyCode.P || key == KeyCode.ESCAPE) {
            this.gameState = GameState.PLAYING;
         }
      }
   }

   private void updateTutorial() {
      if (this.inputHandler.hasLastPressedKey()) {
         KeyCode key = this.inputHandler.consumeLastPressedKey();
         if (key == KeyCode.SPACE) {
            this.startTransition(GameState.PLAYING);
         }
      }
   }

   private void updateMetaShop() {
      if (this.inputHandler.hasLastPressedKey()) {
         KeyCode key = this.inputHandler.consumeLastPressedKey();
         MetaUpgradeType[] upgrades = MetaUpgradeType.values();
         if (key == KeyCode.W || key == KeyCode.UP) {
            this.selectedMetaUpgrade = Math.max(0, this.selectedMetaUpgrade - 2);
         } else if (key == KeyCode.S || key == KeyCode.DOWN) {
            this.selectedMetaUpgrade = Math.min(upgrades.length - 1, this.selectedMetaUpgrade + 2);
         } else if (key == KeyCode.A || key == KeyCode.LEFT) {
            this.selectedMetaUpgrade = Math.max(0, this.selectedMetaUpgrade - 1);
         } else if (key == KeyCode.D || key == KeyCode.RIGHT) {
            this.selectedMetaUpgrade = Math.min(upgrades.length - 1, this.selectedMetaUpgrade + 1);
         } else if (key == KeyCode.ENTER) {
            MetaUpgradeType selectedType = upgrades[this.selectedMetaUpgrade];
            if (this.metaProgressionManager.getMetaProgression().purchaseUpgrade(selectedType)) {
               this.metaProgressionManager.save();
               this.soundGenerator.playLevelUpSound();
            }
         } else if (key == KeyCode.ESCAPE) {
            this.soundGenerator.stopMetaShopMusic();
            this.soundGenerator.startIntroMusic();
            this.restartGame();
         }
      }
   }

   private void restartGame() {
      this.soundGenerator.stopIntroMusic();
      this.soundGenerator.stopMetaShopMusic();
      this.preloaderAnimationTime = 0.0;
      this.initializeGame();
      this.waveManager.reset();
   }

   private void render() {
      this.gameView.setFrameTime(this.currentFrameTime);
      this.gameView.clear();
      if (this.isTransitioning) {
         this.renderTransition();
      } else {
         this.renderCurrentState();
      }

      this.performanceMonitor.render(this.gameView.getGraphicsContext(), this.gameView.getWidth(), this.gameView.getHeight());
   }

   private void renderCurrentState() {
      if (this.gameState == GameState.PRELOADER) {
         this.preloaderView.render(this.preloaderAnimationTime);
      } else if (this.gameState == GameState.PLAYING || this.gameState == GameState.LEVEL_UP) {
         this.renderEntities();
         this.gameView.renderParticles();
         this.hudView.render(this.player, this.waveManager.getCurrentWave());
      }

      if (this.gameState == GameState.TUTORIAL) {
         this.tutorialView.render();
      } else if (this.gameState == GameState.LEVEL_UP) {
         this.levelUpView.render(this.currentUpgradeOptions);
      } else if (this.gameState == GameState.GAME_OVER) {
         this.renderGameOver();
      } else if (this.gameState == GameState.PAUSED) {
         this.renderPaused();
      } else if (this.gameState == GameState.META_SHOP) {
         this.metaShopView.render(this.metaProgressionManager.getMetaProgression(), this.selectedMetaUpgrade);
      }
   }

   private void renderTransition() {
      double fadeOutProgress = Math.min(1.0, this.transitionProgress * 2.0);
      double fadeInProgress = Math.max(0.0, (this.transitionProgress - 0.5) * 2.0);
      if (fadeOutProgress < 1.0) {
         if (this.gameState == GameState.TUTORIAL) {
            this.tutorialView.render();
         } else if (this.gameState == GameState.PLAYING) {
            this.renderEntities();
            this.gameView.renderParticles();
            this.hudView.render(this.player, this.waveManager.getCurrentWave());
         }

         this.gameView.drawBox(0.0, 0.0, this.gameView.getWidth(), this.gameView.getHeight(), Color.TRANSPARENT, Color.rgb(0, 0, 0, fadeOutProgress));
      } else {
         if (this.targetState == GameState.PLAYING) {
            this.renderEntities();
            this.gameView.renderParticles();
            this.hudView.render(this.player, this.waveManager.getCurrentWave());
         }

         this.gameView.drawBox(0.0, 0.0, this.gameView.getWidth(), this.gameView.getHeight(), Color.TRANSPARENT, Color.rgb(0, 0, 0, 1.0 - fadeInProgress));
      }
   }

   private void renderEntities() {
      for (Collectible collectible : this.collectibles) {
         if (collectible.isActive()) {
            boolean isHealthPack = collectible.isHealthPack();
            boolean isPremium = false;
            Color itemColor;
            if (isHealthPack) {
               itemColor = Color.LIGHTGREEN;
            } else {
               isPremium = collectible.getValue() >= 5;
               itemColor = isPremium ? Color.GOLD : Color.YELLOW;
            }

            double pulseIntensity = 0.5 + 0.5 * Math.sin(this.currentFrameTime * 0.005);
            this.gameView.drawCollectible(collectible.getX(), collectible.getY(), itemColor, 1.0 + pulseIntensity * 0.5, isHealthPack, isPremium);
         }
      }

      for (Projectile projectile : this.projectiles) {
         if (projectile.isActive()) {
            this.gameView.drawPackage(projectile.getX(), projectile.getY(), Color.LIGHTBLUE, 0.8);
         }
      }

      for (Drone drone : this.drones) {
         double pulseIntensity = 0.5 + 0.5 * Math.sin(this.currentFrameTime * 0.008);
         this.gameView.drawDrone(drone.getX(), drone.getY(), Color.LIGHTGREEN, 1.0 + pulseIntensity);
      }

      for (Enemy enemy : this.enemies) {
         if (enemy.isActive()) {
            Color enemyColor;
            if (enemy.isDamageFlashing()) {
               enemyColor = Color.WHITE;
            } else {
               double healthPercent = enemy.getHealthComponent().getHealthPercentage();
               if (enemy instanceof VIPCustomer) {
                  if (healthPercent > 0.66) {
                     enemyColor = Color.RED;
                  } else if (healthPercent > 0.33) {
                     enemyColor = Color.ORANGE;
                  } else {
                     enemyColor = Color.YELLOW;
                  }
               } else if (healthPercent > 0.5) {
                  enemyColor = Color.ORANGE;
               } else {
                  enemyColor = Color.YELLOW;
               }
            }

            double glowIntensity = enemy instanceof VIPCustomer ? 1.2 : 0.6;
            boolean isVIP = enemy instanceof VIPCustomer;
            this.gameView.drawEnemy(enemy.getX(), enemy.getY(), enemyColor, glowIntensity, isVIP);
         }
      }

      Color playerColor = Color.CYAN;
      double playerGlow = 1.0;
      if (this.player.isInvulnerable()) {
         boolean flash = this.currentFrameTime / 100L % 2L == 0L;
         playerColor = flash ? Color.WHITE : Color.CYAN;
         playerGlow = 2.0 + Math.sin(this.currentFrameTime * 0.01) * 0.5;
         if (flash) {
            this.gameView.getParticleSystem().createTrail(this.player.getX(), this.player.getY(), Color.CYAN);
         }
      } else if (this.player.isDamageFlashing()) {
         playerColor = Color.RED;
         playerGlow = 1.5;
      }

      this.gameView.drawPlayer(this.player.getX(), this.player.getY(), playerColor, playerGlow);
   }

   private void renderGameOver() {
      double centerX = this.gameView.getWidth() / 2.0;
      double centerY = this.gameView.getHeight() / 2.0;
      this.gameView.drawBox(0.0, 0.0, this.gameView.getWidth(), this.gameView.getHeight(), Color.TRANSPARENT, Color.rgb(0, 0, 0, 0.85));
      this.gameView.drawText("╔════════════════╗", centerX - 130.0, centerY - 120.0, Color.rgb(255, 100, 100), 32);
      this.gameView.drawText(" SHIFT ENDED ", centerX - 100.0, centerY - 80.0, Color.rgb(255, 100, 100), 32);
      this.gameView.drawText("╚════════════════╝", centerX - 130.0, centerY - 40.0, Color.rgb(255, 100, 100), 32);
      int minutes = (int)this.player.getSurvivalTime() / 60;
      int seconds = (int)this.player.getSurvivalTime() % 60;
      this.gameView.drawText(String.format("Total Tips Earned: $%d", this.player.getMoney()), centerX - 120.0, centerY + 10.0, Color.rgb(100, 255, 200), 18);
      this.gameView.drawText(String.format("Shift Duration: %02d:%02d", minutes, seconds), centerX - 120.0, centerY + 40.0, Color.rgb(100, 200, 255), 18);
      this.gameView
         .drawText(String.format("Customers Satisfied: %d", this.player.getCustomersSatisfied()), centerX - 120.0, centerY + 70.0, Color.rgb(100, 200, 255), 18);
      this.gameView.drawText(String.format("Highest Level: %d", this.player.getLevel()), centerX - 120.0, centerY + 100.0, Color.rgb(100, 200, 255), 18);
      this.gameView
         .drawText(String.format("Wave Survived: %d", this.waveManager.getCurrentWave()), centerX - 120.0, centerY + 130.0, Color.rgb(100, 200, 255), 18);
      this.gameView.drawText("Press SPACE to visit Meta Shop", centerX - 140.0, centerY + 170.0, Color.rgb(255, 255, 100), 16);
      this.gameView.drawText("or ESC to quit", centerX - 60.0, centerY + 195.0, Color.rgb(150, 150, 150), 14);
   }

   private void renderPaused() {
      double centerX = this.gameView.getWidth() / 2.0;
      double centerY = this.gameView.getHeight() / 2.0;
      this.gameView.drawBox(0.0, 0.0, this.gameView.getWidth(), this.gameView.getHeight(), Color.TRANSPARENT, Color.rgb(0, 0, 0, 0.6));
      double boxWidth = 400.0;
      double boxHeight = 200.0;
      double boxX = centerX - boxWidth / 2.0;
      double boxY = centerY - boxHeight / 2.0;
      this.gameView.drawBox(boxX + 2.0, boxY + 2.0, boxWidth, boxHeight, Color.TRANSPARENT, Color.rgb(100, 200, 255, 0.2));
      this.gameView.drawBox(boxX, boxY, boxWidth, boxHeight, Color.rgb(100, 200, 255), Color.rgb(25, 25, 35));
      this.gameView.drawText("═══ PAUSED ═══", centerX - 100.0, centerY - 20.0, Color.rgb(100, 255, 200), 28);
      this.gameView.drawText("Press P or ESC to continue", centerX - 140.0, centerY + 40.0, Color.rgb(220, 230, 255), 16);
   }
}
