package com.qvc.survivors.controller;

import com.qvc.survivors.config.GameSettings;
import com.qvc.survivors.config.SettingsManager;
import com.qvc.survivors.engine.Camera;
import com.qvc.survivors.model.GameState;
import com.qvc.survivors.model.entity.*;
import com.qvc.survivors.model.weapon.PassiveItem;
import com.qvc.survivors.model.weapon.PlayerInventory;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.impl.DeliveryDroneSwarm;
import com.qvc.survivors.model.weapon.impl.HostMicrophone;
import com.qvc.survivors.model.meta.MetaUpgradeType;
import com.qvc.survivors.model.upgrade.StatModifier;
import com.qvc.survivors.model.upgrade.Upgrade;
import com.qvc.survivors.service.CollisionManager;
import com.qvc.survivors.service.EntityPoolManager;
import com.qvc.survivors.service.MetaProgressionManager;
import com.qvc.survivors.service.SoundEffectGenerator;
import com.qvc.survivors.service.UpgradeManager;
import com.qvc.survivors.service.WaveManager;
import com.qvc.survivors.world.MapCollectible;
import com.qvc.survivors.world.MapCollectibleType;
import com.qvc.survivors.world.TileMap;
import com.qvc.survivors.world.TileType;
import com.qvc.survivors.world.WorldGenerator;
import com.qvc.survivors.world.Zone;
import com.qvc.survivors.world.ZoneType;
import com.qvc.survivors.view.GameView;
import com.qvc.survivors.view.HUDView;
import com.qvc.survivors.view.LevelUpView;
import com.qvc.survivors.view.MetaShopView;
import com.qvc.survivors.view.PerformanceMonitor;
import com.qvc.survivors.view.PreloaderView;
import com.qvc.survivors.view.SettingsView;
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
   private static final int GRID_WIDTH = 400;
   private static final int GRID_HEIGHT = 300;
   private static final double VIEWPORT_WIDTH = 1200;
   private static final double VIEWPORT_HEIGHT = 750;
   private final Stage stage;
   private final Camera camera;
   private final GameView gameView;
   private final HUDView hudView;
   private final LevelUpView levelUpView;
   private final PreloaderView preloaderView;
   private final TutorialView tutorialView;
   private final MetaShopView metaShopView;
   private final SettingsView settingsView;
   private final PerformanceMonitor performanceMonitor;
   private final InputHandler inputHandler;
   private final EntityPoolManager entityPoolManager;
   private final WaveManager waveManager;
   private final CollisionManager collisionManager;
   private final UpgradeManager upgradeManager;
   private final SoundEffectGenerator soundGenerator;
   private final MetaProgressionManager metaProgressionManager;
   private final SettingsManager settingsManager;
   private GameSettings settings;
   private Player player;
   private List<Enemy> enemies;
   private List<Projectile> projectiles;
   private List<Collectible> collectibles;
   private List<ShockwaveEffect> shockwaves;
   private List<TreasureChest> treasureChests;
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
   private int selectedSettingsRow;
   private GameState preSettingsState;
   private TileMap tileMap;
   private List<MapCollectible> mapCollectibles;
   private ZoneType currentZone;
   private double zoneNameTimer;
   private String zoneNameDisplay;

   public GameController(Stage stage, SoundEffectGenerator soundGenerator) {
      this.stage = stage;
      this.settingsManager = new SettingsManager();
      this.settings = this.settingsManager.load();
      this.camera = new Camera(200.0, 150.0, this.settings.getWindowWidth(), this.settings.getWindowHeight());
      this.gameView = new GameView(GRID_WIDTH, GRID_HEIGHT, this.settings.getWindowWidth(), this.settings.getWindowHeight());
      this.gameView.setCamera(this.camera);
      this.hudView = new HUDView(this.gameView);
      this.levelUpView = new LevelUpView(this.gameView);
      this.preloaderView = new PreloaderView(this.gameView);
      this.tutorialView = new TutorialView(this.gameView);
      this.metaShopView = new MetaShopView(this.gameView);
      this.settingsView = new SettingsView(this.gameView);
      this.performanceMonitor = new PerformanceMonitor();
      this.inputHandler = new InputHandler();
      this.entityPoolManager = new EntityPoolManager();
      this.waveManager = new WaveManager(400.0, 300.0, this.entityPoolManager);
      this.collisionManager = new CollisionManager(400, 300);
      this.upgradeManager = new UpgradeManager();
      this.soundGenerator = soundGenerator;
      this.metaProgressionManager = new MetaProgressionManager();
      this.preloaderAnimationTime = 0.0;
      this.isFirstGame = true;
      this.selectedMetaUpgrade = 0;
      this.moneyEarnedThisRun = 0;
      this.selectedSettingsRow = 0;
      this.applySettings();
      this.initializeGame();
      this.setupScene();
   }

   private void initializeGame() {
      this.player = new Player(200.0, 150.0, this.metaProgressionManager.getMetaProgression());
      this.enemies = new ArrayList<>();
      this.projectiles = new ArrayList<>();
      this.collectibles = new ArrayList<>();
      this.shockwaves = new ArrayList<>();
      this.treasureChests = new ArrayList<>();
      this.gameState = GameState.PRELOADER;
      this.currentUpgradeOptions = new ArrayList<>();
      this.isTransitioning = false;
      this.transitionProgress = 0.0;
      this.moneyEarnedThisRun = 0;
      this.targetState = null;
      WorldGenerator gen = new WorldGenerator();
      this.tileMap = gen.generate(GRID_WIDTH, GRID_HEIGHT);
      this.mapCollectibles = gen.generateCollectibles(this.tileMap);
      this.gameView.setTileMap(this.tileMap);
      this.placeTreasureChests();
      this.currentZone = null;
      this.zoneNameTimer = 0.0;
      this.zoneNameDisplay = null;
      this.soundGenerator.startIntroMusic();
   }

   private void setupScene() {
      StackPane root = new StackPane(new Node[]{this.gameView});
      Scene scene = new Scene(root, this.settings.getWindowWidth(), this.settings.getWindowHeight());
      scene.setOnKeyPressed(event -> this.inputHandler.handleKeyPressed(event.getCode()));
      scene.setOnKeyReleased(event -> this.inputHandler.handleKeyReleased(event.getCode()));
      this.stage.setScene(scene);
      this.stage.setTitle("QVC Survivors");
      this.stage.setMinWidth(800);
      this.stage.setMinHeight(600);
      this.stage.setFullScreen(this.settings.isFullscreen());
      scene.widthProperty().addListener((obs, oldVal, newVal) -> {
         double w = newVal.doubleValue();
         double h = scene.getHeight();
         this.camera.setViewportSize(w, h);
         this.gameView.resizeCanvas(w, h);
      });
      scene.heightProperty().addListener((obs, oldVal, newVal) -> {
         double w = scene.getWidth();
         double h = newVal.doubleValue();
         this.camera.setViewportSize(w, h);
         this.gameView.resizeCanvas(w, h);
      });
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
               break;
            case SETTINGS:
               this.updateSettings();
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
         } else if (key == KeyCode.S) {
            this.preSettingsState = GameState.PRELOADER;
            this.selectedSettingsRow = 0;
            this.gameState = GameState.SETTINGS;
         }
      }
   }

   private void updatePlaying(double deltaTime) {
      this.handlePlayerInput();
      this.player.update(deltaTime);
      this.constrainPlayerToBounds();
      this.camera.follow(this.player.getX(), this.player.getY(), 5.0, deltaTime);
      this.camera.update(deltaTime);
      this.gameView.updateParticles(deltaTime);

      // Zone tracking
      Zone zone = this.tileMap.getZoneAt(this.player.getX(), this.player.getY());
      ZoneType newZone = zone != null ? zone.getType() : null;
      if (newZone != this.currentZone) {
         this.currentZone = newZone;
         if (newZone != null) {
            this.zoneNameDisplay = newZone.getDisplayName();
            this.zoneNameTimer = 3.0;
         }
      }
      this.waveManager.setCurrentZone(this.currentZone);
      if (this.zoneNameTimer > 0.0) {
         this.zoneNameTimer -= deltaTime;
      }

      // Update map collectibles
      for (MapCollectible mc : this.mapCollectibles) {
         mc.update(deltaTime);
      }
      this.checkMapCollectiblePickup();

      this.waveManager.setCameraPosition(
         this.camera.getX(), this.camera.getY(),
         this.camera.getViewportWidth(), this.camera.getViewportHeight()
      );
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

      // Weapon system update
      for (Weapon weapon : this.player.getInventory().getWeapons()) {
         weapon.update(deltaTime);
         if (weapon instanceof DeliveryDroneSwarm droneSwarm) {
            droneSwarm.updateDronePositions(this.player.getX(), this.player.getY());
            for (Drone drone : droneSwarm.getDrones()) {
               this.gameView.getParticleSystem().createDroneTrail(drone.getX(), drone.getY());
            }
         }
         if (weapon.isReady()) {
            List<Projectile> newProjectiles = weapon.fire(
                    this.player.getX(), this.player.getY(),
                    this.player.getFacingAngle(),
                    this.enemies, this.entityPoolManager);
            if (!newProjectiles.isEmpty()) {
               this.projectiles.addAll(newProjectiles);
               this.soundGenerator.playShootSound();
            }
            // Handle HostMicrophone shockwave
            if (weapon instanceof HostMicrophone mic) {
               ShockwaveEffect sw = mic.getActiveShockwave();
               if (sw != null) {
                  this.shockwaves.add(sw);
                  mic.clearShockwave();
               }
            }
         }
      }

      for (Projectile projectile : this.projectiles) {
         if (projectile.isActive()) {
            if (projectile instanceof BoomerangProjectile boom) {
               boom.setPlayerPosition(this.player.getX(), this.player.getY());
            }
            projectile.update(deltaTime);
            if (!(projectile instanceof BoomerangProjectile) && !(projectile instanceof ArcProjectile)
                    && projectile.isOutOfBounds(400.0, 300.0)) {
               projectile.setActive(false);
            }
            if (projectile.isActive() && !(projectile instanceof ArcProjectile) && Math.random() < 0.3) {
               this.gameView.getParticleSystem().createTrail(projectile.getX(), projectile.getY(), Color.LIGHTBLUE);
            }
         }
      }

      // Update shockwaves
      for (ShockwaveEffect sw : this.shockwaves) {
         if (sw.isActive()) {
            sw.update(deltaTime);
            for (Enemy enemy : this.enemies) {
               if (enemy.isActive() && sw.isEnemyInRadius(enemy)) {
                  enemy.takeDamage(sw.getDamage());
                  sw.markHit(enemy);
                  this.gameView.getParticleSystem().createImpact(enemy.getX(), enemy.getY(), Color.MEDIUMPURPLE);
                  if (!enemy.isActive()) {
                     this.player.incrementCustomersSatisfied();
                  }
               }
            }
         }
      }
      this.shockwaves.removeIf(sw -> !sw.isActive());
      this.checkCollisionsWithEffects();
      this.spawnCollectiblesFromDeadEnemies();
      this.removeInactiveEntities();
      // Treasure chest pickup
      this.checkTreasureChestPickup();

      if (this.player.canLevelUp()) {
         this.currentUpgradeOptions = this.upgradeManager.generateUpgradeOptions(this.player.getInventory());
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
      double px = Math.max(0.0, Math.min(399.0, this.player.getX()));
      double py = Math.max(0.0, Math.min(299.0, this.player.getY()));
      // Wall collision: prevent movement into WALL tiles
      if (this.tileMap != null) {
         int tileX = (int) px;
         int tileY = (int) py;
         if (this.tileMap.getTile(tileX, tileY) == TileType.WALL) {
            // Revert to previous position (snap back)
            px = Math.max(0.0, Math.min(399.0, this.player.getX() - this.player.getMovementComponent().getVelocityX() * 0.016));
            py = Math.max(0.0, Math.min(299.0, this.player.getY() - this.player.getMovementComponent().getVelocityY() * 0.016));
         }
      }
      this.player.setX(px);
      this.player.setY(py);
   }

   private void placeTreasureChests() {
      if (this.tileMap == null) return;
      for (com.qvc.survivors.world.Zone zone : this.tileMap.getZones()) {
         int cx = zone.getStartX() + zone.getWidth() / 2 + 15;
         int cy = zone.getStartY() + zone.getHeight() / 2 + 10;
         this.treasureChests.add(new TreasureChest(cx, cy));
      }
   }

   private void checkTreasureChestPickup() {
      double pickupRange = this.player.getStats().getStat(StatModifier.PICKUP_RANGE) + 1.0;
      for (TreasureChest chest : this.treasureChests) {
         if (!chest.isActive() || chest.isCollected()) continue;
         double dx = this.player.getX() - chest.getX();
         double dy = this.player.getY() - chest.getY();
         double dist = Math.sqrt(dx * dx + dy * dy);
         if (dist <= pickupRange) {
            chest.collect();
            this.soundGenerator.playCollectSound();
            this.gameView.getParticleSystem().createExplosion(chest.getX(), chest.getY(), Color.GOLD, 25);
            // Check evolution first
            com.qvc.survivors.model.weapon.EvolutionRecipe evo =
                    com.qvc.survivors.model.weapon.EvolutionRegistry.checkEvolutions(this.player.getInventory());
            if (evo != null) {
               // Grant a level-up to a random weapon/passive instead of actual evolution (evolution weapons not yet implemented)
               grantRandomLevelUp();
            } else {
               grantRandomLevelUp();
            }
         }
      }
   }

   private void grantRandomLevelUp() {
      PlayerInventory inv = this.player.getInventory();
      // Try to level up a random weapon
      List<Weapon> upgradeable = new ArrayList<>();
      for (Weapon w : inv.getWeapons()) {
         if (!w.isMaxLevel()) upgradeable.add(w);
      }
      List<PassiveItem> upgradeablePassives = new ArrayList<>();
      for (PassiveItem p : inv.getPassives()) {
         if (!p.isMaxLevel()) upgradeablePassives.add(p);
      }
      if (!upgradeable.isEmpty() && (upgradeablePassives.isEmpty() || Math.random() < 0.5)) {
         upgradeable.get((int)(Math.random() * upgradeable.size())).levelUp();
      } else if (!upgradeablePassives.isEmpty()) {
         upgradeablePassives.get((int)(Math.random() * upgradeablePassives.size())).levelUp();
      }
   }

   private void checkCollisionsWithEffects() {
      double critChance = this.player.getStats().getEffectiveStat(StatModifier.CRITICAL_CHANCE, this.player.getInventory());
      double critMultiplier = 2.0 + this.player.getInventory().getTotalStatBoost(StatModifier.CRIT_DAMAGE);
      double lifesteal = this.player.getInventory().getTotalStatBoost(StatModifier.LIFESTEAL);

      for (Projectile projectile : this.projectiles) {
         if (!projectile.isActive()) continue;

         // ArcProjectile: damage enemies in arc
         if (projectile instanceof ArcProjectile arc) {
            for (Enemy enemy : this.enemies) {
               if (enemy.isActive() && arc.isEnemyInArc(enemy)) {
                  double damage = arc.getDamageComponent().getDamage();
                  boolean isCritical = Math.random() < critChance;
                  if (isCritical) damage *= critMultiplier;
                  enemy.takeDamage(damage);
                  arc.markHit(enemy);
                  applyLifesteal(lifesteal, damage);
                  this.gameView.getParticleSystem().createImpact(enemy.getX(), enemy.getY(), Color.CRIMSON);
                  this.soundGenerator.playHitSound();
                  handleEnemyDeath(enemy);
               }
            }
            continue;
         }

         for (Enemy enemy : this.enemies) {
            if (enemy.isActive() && this.collisionManager.checkCollision(projectile, enemy)) {
               double damage = projectile.getDamageComponent().getDamage();
               boolean isCritical = Math.random() < critChance;
               if (isCritical) {
                  damage *= critMultiplier;
                  this.gameView.getParticleSystem().createExplosion(enemy.getX(), enemy.getY(), Color.YELLOW, 15);
               } else {
                  this.gameView.getParticleSystem().createImpact(projectile.getX(), projectile.getY(), Color.LIGHTBLUE);
               }

               enemy.takeDamage(damage);
               applyLifesteal(lifesteal, damage);
               this.soundGenerator.playHitSound();

               // Boomerang: pierce through
               if (projectile instanceof BoomerangProjectile boom) {
                  boom.incrementPierce();
                  if (!boom.canPierce()) {
                     // let it continue returning
                  }
               } else {
                  projectile.setActive(false);
               }

               handleEnemyDeath(enemy);
               if (!(projectile instanceof BoomerangProjectile)) break;
            }
         }
      }

      // Drone collisions (from weapon system)
      for (Weapon weapon : this.player.getInventory().getWeapons()) {
         if (weapon instanceof DeliveryDroneSwarm droneSwarm) {
            for (Drone drone : droneSwarm.getDrones()) {
               for (Enemy enemy : this.enemies) {
                  if (enemy.isActive() && this.collisionManager.checkCollision(drone, enemy) && drone.canAttack()) {
                     double damage = drone.getDamageComponent().getDamage();
                     if (Math.random() < critChance) damage *= critMultiplier;
                     enemy.takeDamage(damage);
                     drone.resetAttackTimer();
                     applyLifesteal(lifesteal, damage);
                     this.gameView.getParticleSystem().createImpact(enemy.getX(), enemy.getY(), Color.LIGHTGREEN);
                     this.soundGenerator.playHitSound();
                     handleEnemyDeath(enemy);
                  }
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

   private void handleEnemyDeath(Enemy enemy) {
      if (!enemy.isActive()) {
         Color explosionColor = enemy instanceof VIPCustomer ? Color.RED : Color.ORANGE;
         this.gameView.getParticleSystem().createExplosion(enemy.getX(), enemy.getY(), explosionColor, 20);
         this.soundGenerator.playExplosionSound();
         this.player.incrementCustomersSatisfied();
      } else {
         this.soundGenerator.playEnemyHurtSound();
      }
   }

   private void applyLifesteal(double lifestealPercent, double damageDealt) {
      if (lifestealPercent > 0.0) {
         double healAmount = damageDealt * lifestealPercent;
         this.player.getHealthComponent().heal(healAmount);
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

   private void checkMapCollectiblePickup() {
      double pickupRange = this.player.getStats().getStat(StatModifier.PICKUP_RANGE) + 1.0;
      for (MapCollectible mc : this.mapCollectibles) {
         if (!mc.isAvailable()) continue;
         double dx = this.player.getX() - mc.getX();
         double dy = this.player.getY() - mc.getY();
         double dist = Math.sqrt(dx * dx + dy * dy);
         if (dist <= pickupRange) {
            mc.collect();
            this.soundGenerator.playCollectSound();
            this.gameView.getParticleSystem().createCollectionEffect(mc.getX(), mc.getY(), mc.getType().getColor());
            applyMapCollectibleEffect(mc.getType());
         }
      }
   }

   private void applyMapCollectibleEffect(MapCollectibleType type) {
      switch (type) {
         case OVERSTOCK_CRATE:
            for (int i = 0; i < 20; i++) {
               double angle = Math.random() * Math.PI * 2;
               double dist = 2.0 + Math.random() * 4.0;
               double cx = this.player.getX() + Math.cos(angle) * dist;
               double cy = this.player.getY() + Math.sin(angle) * dist;
               this.collectibles.add(this.entityPoolManager.obtainCollectible(cx, cy, 1, false));
            }
            break;
         case COFFEE_MUG:
            this.player.activateCoffeeBreak(10.0);
            break;
         case GIFT_CARD:
            this.player.setMoney(this.player.getMoney() + 25);
            this.player.addExperience(25);
            this.moneyEarnedThisRun += 25;
            break;
         case RECALL_NOTICE:
            for (Enemy enemy : this.enemies) {
               if (enemy.isActive()) {
                  enemy.takeDamage(10);
                  this.gameView.getParticleSystem().createImpact(enemy.getX(), enemy.getY(), Color.RED);
               }
            }
            break;
         case FLOOR_MODEL:
            this.player.addExperience(this.player.getExperienceThreshold());
            break;
         case EMPLOYEE_DISCOUNT:
            this.player.activateEmployeeDiscount();
            break;
         case MYSTERY_SAMPLE:
            MapCollectibleType[] effects = {
               MapCollectibleType.OVERSTOCK_CRATE, MapCollectibleType.COFFEE_MUG,
               MapCollectibleType.GIFT_CARD, MapCollectibleType.RECALL_NOTICE,
               MapCollectibleType.FLOOR_MODEL, MapCollectibleType.EMPLOYEE_DISCOUNT
            };
            applyMapCollectibleEffect(effects[(int)(Math.random() * effects.length)]);
            break;
      }
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
            PlayerInventory inv = this.player.getInventory();

            switch (selectedUpgrade.getChoiceType()) {
               case WEAPON_LEVELUP -> {
                  Weapon w = inv.getWeapon(selectedUpgrade.getTargetId());
                  if (w != null) w.levelUp();
               }
               case NEW_WEAPON -> {
                  Weapon newWeapon = this.upgradeManager.createWeaponById(selectedUpgrade.getTargetId());
                  if (newWeapon != null) inv.addWeapon(newWeapon);
               }
               case PASSIVE_LEVELUP -> {
                  PassiveItem p = inv.getPassive(selectedUpgrade.getTargetId());
                  if (p != null) p.levelUp();
               }
               case NEW_PASSIVE -> {
                  PassiveItem newPassive = this.upgradeManager.createPassiveById(selectedUpgrade.getTargetId());
                  if (newPassive != null) inv.addPassive(newPassive);
               }
               case LEGACY -> {
                  // Employee discount: double stat values for legacy upgrades
                  if (this.player.isEmployeeDiscountActive()) {
                     for (StatModifier mod : selectedUpgrade.getStatModifiers().keySet()) {
                        double val = selectedUpgrade.getStatModifiers().get(mod);
                        selectedUpgrade.getStatModifiers().put(mod, val * 2.0);
                     }
                     this.player.consumeEmployeeDiscount();
                  }
                  this.player.getStats().applyUpgrade(selectedUpgrade);
                  if (selectedUpgrade.getStatModifiers().containsKey(StatModifier.MAX_HEALTH)) {
                     double healthIncrease = selectedUpgrade.getStatModifiers().get(StatModifier.MAX_HEALTH);
                     this.player.getHealthComponent().increaseMaxHealth(healthIncrease);
                  }
               }
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
         } else if (key == KeyCode.S) {
            this.preSettingsState = GameState.PAUSED;
            this.selectedSettingsRow = 0;
            this.gameState = GameState.SETTINGS;
         }
      }
   }

   private void updateSettings() {
      if (this.inputHandler.hasLastPressedKey()) {
         KeyCode key = this.inputHandler.consumeLastPressedKey();
         if (key == KeyCode.ESCAPE || key == KeyCode.SPACE) {
            this.settingsManager.save(this.settings);
            this.gameState = this.preSettingsState;
         } else if (key == KeyCode.W || key == KeyCode.UP) {
            this.selectedSettingsRow = Math.max(0, this.selectedSettingsRow - 1);
         } else if (key == KeyCode.S || key == KeyCode.DOWN) {
            this.selectedSettingsRow = Math.min(SettingsView.ROW_COUNT - 1, this.selectedSettingsRow + 1);
         } else if (key == KeyCode.A || key == KeyCode.LEFT) {
            this.adjustSetting(this.selectedSettingsRow, -1);
         } else if (key == KeyCode.D || key == KeyCode.RIGHT) {
            this.adjustSetting(this.selectedSettingsRow, 1);
         }
      }
   }

   private void adjustSetting(int row, int direction) {
      switch (row) {
         case 0:
            int resIdx = SettingsView.findResolutionIndex(this.settings.getWindowWidth(), this.settings.getWindowHeight());
            resIdx = Math.max(0, Math.min(SettingsView.getResolutionCount() - 1, resIdx + direction));
            int[] res = SettingsView.getResolution(resIdx);
            this.settings.setWindowWidth(res[0]);
            this.settings.setWindowHeight(res[1]);
            this.stage.setWidth(res[0]);
            this.stage.setHeight(res[1]);
            break;
         case 1:
            this.settings.setFullscreen(!this.settings.isFullscreen());
            this.stage.setFullScreen(this.settings.isFullscreen());
            break;
         case 2:
            this.settings.setMusicVolume(this.settings.getMusicVolume() + direction * 0.1);
            this.soundGenerator.setMusicVolumeLevel(this.settings.getMusicVolume());
            break;
         case 3:
            this.settings.setSfxVolume(this.settings.getSfxVolume() + direction * 0.1);
            this.soundGenerator.setMasterVolume(this.settings.getSfxVolume());
            break;
         case 4:
            this.settings.setMusicEnabled(!this.settings.isMusicEnabled());
            this.soundGenerator.setMusicEnabled(this.settings.isMusicEnabled());
            break;
         case 5:
            this.settings.setSfxEnabled(!this.settings.isSfxEnabled());
            this.soundGenerator.setSfxEnabled(this.settings.isSfxEnabled());
            break;
         case 6:
            this.settings.setShowFPS(!this.settings.isShowFPS());
            this.performanceMonitor.setEnabled(this.settings.isShowFPS());
            break;
         case 7:
            this.settings.setDamageNumbers(!this.settings.isDamageNumbers());
            break;
         case 8:
            this.settings.setScreenShake(!this.settings.isScreenShake());
            break;
         default:
            break;
      }
   }

   private void applySettings() {
      this.soundGenerator.setMasterVolume(this.settings.getSfxVolume());
      this.soundGenerator.setMusicVolumeLevel(this.settings.getMusicVolume());
      this.soundGenerator.setSfxEnabled(this.settings.isSfxEnabled());
      this.soundGenerator.setMusicEnabled(this.settings.isMusicEnabled());
      this.performanceMonitor.setEnabled(this.settings.isShowFPS());
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
      this.camera.follow(200.0, 150.0, 1000.0, 1.0);
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
         this.camera.setEnabled(false);
         this.preloaderView.render(this.preloaderAnimationTime);
         this.camera.setEnabled(true);
      } else if (this.gameState == GameState.PLAYING || this.gameState == GameState.LEVEL_UP) {
         this.renderEntities();
         this.gameView.renderParticles();
         this.hudView.render(this.player, this.waveManager.getCurrentWave());
      }

      if (this.gameState == GameState.TUTORIAL) {
         this.camera.setEnabled(false);
         this.tutorialView.render();
         this.camera.setEnabled(true);
      } else if (this.gameState == GameState.LEVEL_UP) {
         this.levelUpView.render(this.currentUpgradeOptions);
      } else if (this.gameState == GameState.GAME_OVER) {
         this.renderGameOver();
      } else if (this.gameState == GameState.PAUSED) {
         this.renderPaused();
      } else if (this.gameState == GameState.META_SHOP) {
         this.metaShopView.render(this.metaProgressionManager.getMetaProgression(), this.selectedMetaUpgrade);
      } else if (this.gameState == GameState.SETTINGS) {
         this.settingsView.render(this.settings, this.selectedSettingsRow);
      }
   }

   private void renderTransition() {
      double fadeOutProgress = Math.min(1.0, this.transitionProgress * 2.0);
      double fadeInProgress = Math.max(0.0, (this.transitionProgress - 0.5) * 2.0);
      if (fadeOutProgress < 1.0) {
         if (this.gameState == GameState.TUTORIAL) {
            this.camera.setEnabled(false);
            this.tutorialView.render();
            this.camera.setEnabled(true);
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
      // Render map collectibles
      for (MapCollectible mc : this.mapCollectibles) {
         if (mc.isAvailable() && this.camera.isInView(mc.getX(), mc.getY(), 5)) {
            double pulse = this.currentFrameTime * 0.003;
            this.gameView.drawMapCollectible(mc.getX(), mc.getY(), mc.getType(), pulse);
         }
      }

      for (Collectible collectible : this.collectibles) {
         if (collectible.isActive() && this.camera.isInView(collectible.getX(), collectible.getY(), 5)) {
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
         if (projectile.isActive() && this.camera.isInView(projectile.getX(), projectile.getY(), 5)) {
            if (projectile instanceof BoomerangProjectile boom) {
               this.gameView.drawBoomerang(boom.getX(), boom.getY(), boom.getRotation());
            } else if (projectile instanceof ArcProjectile arc) {
               this.gameView.drawArcSlash(arc.getX(), arc.getY(), arc.getFacingAngle(), arc.getRange(), arc.getProgress());
            } else {
               this.gameView.drawPackage(projectile.getX(), projectile.getY(), Color.LIGHTBLUE, 0.8);
            }
         }
      }

      // Render shockwaves
      for (ShockwaveEffect sw : this.shockwaves) {
         if (sw.isActive() && this.camera.isInView(sw.getX(), sw.getY(), (int)sw.getMaxRadius())) {
            this.gameView.drawShockwave(sw.getX(), sw.getY(), sw.getCurrentRadius(), sw.getProgress());
         }
      }

      // Render treasure chests
      for (TreasureChest chest : this.treasureChests) {
         if (chest.isActive() && this.camera.isInView(chest.getX(), chest.getY(), 5)) {
            double pulse = 0.5 + 0.5 * Math.sin(this.currentFrameTime * 0.004);
            this.gameView.drawCollectible(chest.getX(), chest.getY(), Color.GOLD, 1.0 + pulse, false, true);
         }
      }

      // Render drones from weapon system
      for (Weapon weapon : this.player.getInventory().getWeapons()) {
         if (weapon instanceof DeliveryDroneSwarm droneSwarm) {
            for (Drone drone : droneSwarm.getDrones()) {
               if (this.camera.isInView(drone.getX(), drone.getY(), 5)) {
                  double pulseIntensity = 0.5 + 0.5 * Math.sin(this.currentFrameTime * 0.008);
                  this.gameView.drawDrone(drone.getX(), drone.getY(), Color.LIGHTGREEN, 1.0 + pulseIntensity);
               }
            }
         }
      }

      for (Enemy enemy : this.enemies) {
         if (enemy.isActive() && this.camera.isInView(enemy.getX(), enemy.getY(), 5)) {
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

      // Zone name banner
      if (this.zoneNameTimer > 0.0 && this.zoneNameDisplay != null) {
         double alpha = Math.min(1.0, this.zoneNameTimer);
         Color zoneColor = Color.rgb(220, 230, 255, alpha * 0.9);
         double centerX = this.gameView.getWidth() / 2.0;
         this.gameView.drawText(this.zoneNameDisplay, centerX - this.zoneNameDisplay.length() * 6, 60.0, zoneColor, 24);
      }

      // Coffee break indicator
      if (this.player.isCoffeeBreakActive()) {
         double remaining = this.player.getCoffeeBreakTimer();
         this.gameView.drawText(String.format("COFFEE BREAK! %.0fs", remaining), 10.0, this.gameView.getHeight() - 30.0, Color.rgb(139, 90, 43, 0.8), 14);
      }
      if (this.player.isEmployeeDiscountActive()) {
         this.gameView.drawText("EMPLOYEE DISCOUNT ACTIVE", 10.0, this.gameView.getHeight() - 50.0, Color.rgb(50, 200, 100, 0.8), 14);
      }
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
      this.gameView.drawText("Press S for Settings", centerX - 100.0, centerY + 70.0, Color.rgb(150, 200, 255), 14);
   }
}
