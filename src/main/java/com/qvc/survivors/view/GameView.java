package com.qvc.survivors.view;

import com.qvc.survivors.engine.Camera;
import com.qvc.survivors.model.entity.EnemyType;
import com.qvc.survivors.world.MapCollectibleType;
import com.qvc.survivors.world.TileMap;
import com.qvc.survivors.world.TileType;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lombok.Generated;

public class GameView extends Canvas {
   private static final int TILE_SIZE = 15;
   private static final Color BACKGROUND_COLOR = Color.rgb(20, 20, 25);
   private static final Color GRID_COLOR = Color.rgb(30, 35, 45, 0.3);
   private static final Font FONT_COURIER_15 = Font.font("Courier New", FontWeight.BOLD, 15.0);
   private static final Font FONT_ARIAL_10 = Font.font("Arial", FontWeight.BOLD, 10.0);
   private static final java.util.Map<Integer, Font> fontCache = new java.util.concurrent.ConcurrentHashMap<>();
   private static Font getCachedFont(int size) {
      return fontCache.computeIfAbsent(size, s -> Font.font("Courier New", FontWeight.BOLD, s));
   }
   private final int gridWidth;
   private final int gridHeight;
   private final GraphicsContext graphicsContext;
   private final ParticleSystem particleSystem;
   private double animationTime;
   private RadialGradient[] nebulaGradients;
   private static final double TILE_SIZE_HALF = 7.5;
   private long cachedFrameTime;
   private Camera camera;
   private TileMap tileMap;
   private final List<double[]> pendingGlows = new ArrayList<>();

   public GameView(int gridWidth, int gridHeight, double viewportWidth, double viewportHeight) {
      super(viewportWidth, viewportHeight);
      this.gridWidth = gridWidth;
      this.gridHeight = gridHeight;
      this.graphicsContext = this.getGraphicsContext2D();
      this.particleSystem = new ParticleSystem(2000);
      this.animationTime = 0.0;
      this.graphicsContext.setFont(FONT_COURIER_15);
      this.graphicsContext.setImageSmoothing(false);
      this.initializeNebulaGradients();
   }

   public void setCamera(Camera camera) {
      this.camera = camera;
   }

   public void setTileMap(TileMap tileMap) {
      this.tileMap = tileMap;
   }

   public void resizeCanvas(double width, double height) {
      this.setWidth(width);
      this.setHeight(height);
   }

   private void initializeNebulaGradients() {
      this.nebulaGradients = new RadialGradient[3];

      for (int i = 0; i < 3; i++) {
         this.nebulaGradients[i] = new RadialGradient(
            0.0,
            0.0,
            0.0,
            0.0,
            150.0,
            false,
            CycleMethod.NO_CYCLE,
            new Stop[]{new Stop(0.0, Color.rgb(100, 50, 200, 0.05)), new Stop(0.5, Color.rgb(50, 100, 255, 0.02)), new Stop(1.0, Color.TRANSPARENT)}
         );
      }
   }

   private void drawGlowEffect(double centerX, double centerY, Color color, double glowIntensity) {
      pendingGlows.add(new double[]{centerX, centerY, color.getRed(), color.getGreen(), color.getBlue(), glowIntensity});
   }

   private void flushGlows() {
      if (pendingGlows.isEmpty()) return;
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      for (double[] g : pendingGlows) {
         double glowRadius = 20.0 * g[5];
         this.graphicsContext.setFill(Color.color(g[2], g[3], g[4], Math.min(0.15 * g[5], 0.4)));
         this.graphicsContext.fillOval(g[0] - glowRadius, g[1] - glowRadius, glowRadius * 2.0, glowRadius * 2.0);
      }
      this.graphicsContext.restore();
      pendingGlows.clear();
   }

   public void updateParticles(double deltaTime) {
      this.particleSystem.update(deltaTime);
   }

   public void setFrameTime(long frameTime) {
      this.cachedFrameTime = frameTime;
   }

   public void clear() {
      this.graphicsContext.setFill(BACKGROUND_COLOR);
      this.graphicsContext.fillRect(0.0, 0.0, this.getWidth(), this.getHeight());
      this.drawBackgroundEffects();
      if (this.tileMap != null) {
         this.drawTileMap();
      } else {
         this.drawGrid();
      }
      this.drawScanlines();
      this.animationTime += 0.016;
   }

   private void drawBackgroundEffects() {
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);

      double parallaxOffsetX = camera.isEnabled() ? camera.getX() * TILE_SIZE * 0.5 : 0;
      double parallaxOffsetY = camera.isEnabled() ? camera.getY() * TILE_SIZE * 0.5 : 0;

      for (int i = 0; i < 50; i++) {
         double x = ((i * 137.5 + this.animationTime * 2.0) - parallaxOffsetX % this.getWidth() + this.getWidth()) % this.getWidth();
         double y = ((i * 73.3 + this.animationTime * 1.5) - parallaxOffsetY % this.getHeight() + this.getHeight()) % this.getHeight();
         double brightness = 0.3 + 0.2 * Math.sin(this.animationTime * 3.0 + i);
         this.graphicsContext.setFill(Color.rgb(50, 100, 150, brightness * 0.3));
         this.graphicsContext.fillOval(x - 1.0, y - 1.0, 2.0, 2.0);
      }

      for (int i = 0; i < 3; i++) {
         double centerX = ((i * 300 + this.animationTime * 10.0) - parallaxOffsetX % this.getWidth() + this.getWidth()) % this.getWidth();
         double centerY = this.getHeight() / 3.0 * (i + 1);
         this.graphicsContext.setFill(this.nebulaGradients[i]);
         this.graphicsContext.fillOval(centerX - 150.0, centerY - 150.0, 300.0, 300.0);
      }

      this.graphicsContext.restore();
   }

   private void drawGrid() {
      this.graphicsContext.setStroke(GRID_COLOR);
      this.graphicsContext.setLineWidth(0.5);

      int firstVisibleCol = Math.max(0, (int) Math.floor(camera.screenToWorldX(0)));
      int lastVisibleCol = Math.min(this.gridWidth, (int) Math.ceil(camera.screenToWorldX(this.getWidth())) + 1);
      int firstVisibleRow = Math.max(0, (int) Math.floor(camera.screenToWorldY(0)));
      int lastVisibleRow = Math.min(this.gridHeight, (int) Math.ceil(camera.screenToWorldY(this.getHeight())) + 1);

      for (int i = firstVisibleCol; i <= lastVisibleCol; i++) {
         double x = camera.worldToScreenX(i);
         this.graphicsContext.strokeLine(x, 0.0, x, this.getHeight());
      }

      for (int i = firstVisibleRow; i <= lastVisibleRow; i++) {
         double y = camera.worldToScreenY(i);
         this.graphicsContext.strokeLine(0.0, y, this.getWidth(), y);
      }
   }

   private void drawScanlines() {
      this.graphicsContext.setStroke(Color.rgb(0, 0, 0, 0.03));
      this.graphicsContext.setLineWidth(1.0);
      for (double y = 0; y < this.getHeight(); y += 4) {
         this.graphicsContext.strokeLine(0, y, this.getWidth(), y);
      }
   }

   private void drawTileMap() {
      int firstCol = Math.max(0, (int) Math.floor(camera.screenToWorldX(0)));
      int lastCol = Math.min(this.tileMap.getWidth() - 1, (int) Math.ceil(camera.screenToWorldX(this.getWidth())));
      int firstRow = Math.max(0, (int) Math.floor(camera.screenToWorldY(0)));
      int lastRow = Math.min(this.tileMap.getHeight() - 1, (int) Math.ceil(camera.screenToWorldY(this.getHeight())));

      for (int tx = firstCol; tx <= lastCol; tx++) {
         for (int ty = firstRow; ty <= lastRow; ty++) {
            TileType tile = this.tileMap.getTile(tx, ty);
            double screenX = camera.worldToScreenX(tx);
            double screenY = camera.worldToScreenY(ty);
            this.graphicsContext.setFill(tile.getBaseColor());
            this.graphicsContext.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
            if (tile == TileType.WALL) {
               this.graphicsContext.setStroke(tile.getGridColor());
               this.graphicsContext.setLineWidth(1.5);
               this.graphicsContext.strokeRect(screenX + 0.5, screenY + 0.5, TILE_SIZE - 1, TILE_SIZE - 1);
            } else {
               this.graphicsContext.setStroke(tile.getGridColor());
               this.graphicsContext.setLineWidth(0.5);
               this.graphicsContext.strokeRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
            }
         }
      }
   }

   public void drawMapCollectible(double x, double y, MapCollectibleType type, double pulse) {
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + TILE_SIZE_HALF;
      double centerY = pixelY + TILE_SIZE_HALF;
      Color color = type.getColor();

      // Glow
      this.drawGlowEffect(centerX, centerY, color, 0.8 + pulse * 0.4);

      double size = 10.0;
      double scale = 1.0 + Math.sin(pulse * 3.0) * 0.15;
      this.graphicsContext.save();
      this.graphicsContext.translate(centerX, centerY);
      this.graphicsContext.scale(scale, scale);

      // Diamond shape
      double half = size / 2.0;
      this.graphicsContext.setFill(color.darker());
      this.graphicsContext.fillPolygon(
         new double[]{0, half, 0, -half},
         new double[]{-half, 0, half, 0}, 4);
      this.graphicsContext.setStroke(color.brighter());
      this.graphicsContext.setLineWidth(1.5);
      this.graphicsContext.strokePolygon(
         new double[]{0, half, 0, -half},
         new double[]{-half, 0, half, 0}, 4);

      // Inner dot
      this.graphicsContext.setFill(color.brighter());
      this.graphicsContext.fillOval(-2, -2, 4, 4);
      this.graphicsContext.restore();
   }

   public void drawPackage(double x, double y, Color color, double glowIntensity) {
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + 7.5;
      double centerY = pixelY + 7.5;
      double bs = 12.0;
      this.drawGlowEffect(centerX, centerY, color, glowIntensity);
      double rotation = this.cachedFrameTime * 0.005;
      this.graphicsContext.save();
      this.graphicsContext.translate(centerX, centerY);
      this.graphicsContext.rotate(Math.toDegrees(rotation));
      // Cardboard box body
      Color cardboard = Color.rgb(180, 150, 100);
      Color shadow = Color.rgb(140, 110, 70);
      this.graphicsContext.setFill(cardboard);
      this.graphicsContext.fillRect(-bs/2, -bs/2, bs, bs);
      // Top-flap shading
      this.graphicsContext.setFill(cardboard.brighter());
      this.graphicsContext.fillRect(-bs/2, -bs/2, bs, bs * 0.3);
      // Box outline
      this.graphicsContext.setStroke(shadow);
      this.graphicsContext.setLineWidth(1.5);
      this.graphicsContext.strokeRect(-bs/2, -bs/2, bs, bs);
      // Tape: horizontal and vertical strips (brown tape)
      this.graphicsContext.setStroke(Color.rgb(200, 170, 90));
      this.graphicsContext.setLineWidth(2.5);
      this.graphicsContext.strokeLine(-bs/2, 0, bs/2, 0);         // horizontal tape
      this.graphicsContext.strokeLine(0, -bs/2, 0, bs/2);          // vertical tape
      this.graphicsContext.restore();
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      this.graphicsContext.setFill(Color.rgb(255, 200, 100, 0.3));
      this.graphicsContext.fillRect(centerX - bs/2, centerY - bs/2, bs, bs/3);
      this.graphicsContext.restore();
   }

   public void drawPlayer(double x, double y, Color color, double glowIntensity) {
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + 7.5;
      double centerY = pixelY + 7.5;
      this.drawGlowEffect(centerX, centerY, color, glowIntensity);
      double s = 13.5;
      double headR = s * 0.28;
      this.graphicsContext.save();
      this.graphicsContext.translate(centerX, centerY);
      // Head
      this.graphicsContext.setFill(Color.rgb(255, 220, 180));
      this.graphicsContext.fillOval(-headR, -s * 0.5, headR * 2, headR * 2);
      this.graphicsContext.setStroke(color);
      this.graphicsContext.setLineWidth(1.5);
      this.graphicsContext.strokeOval(-headR, -s * 0.5, headR * 2, headR * 2);
      // Body (blazer)
      this.graphicsContext.setFill(color.darker());
      this.graphicsContext.fillRect(-s * 0.28, -s * 0.5 + headR * 2, s * 0.56, s * 0.42);
      this.graphicsContext.setStroke(color);
      this.graphicsContext.setLineWidth(1.5);
      this.graphicsContext.strokeRect(-s * 0.28, -s * 0.5 + headR * 2, s * 0.56, s * 0.42);
      // Microphone arm (extended right)
      this.graphicsContext.setStroke(color.brighter());
      this.graphicsContext.setLineWidth(1.5);
      double micArmY = -s * 0.5 + headR * 2 + s * 0.12;
      this.graphicsContext.strokeLine(s * 0.28, micArmY, s * 0.55, micArmY - s * 0.18);
      // Mic head (ball)
      this.graphicsContext.setFill(Color.SILVER);
      this.graphicsContext.fillOval(s * 0.5, micArmY - s * 0.25, s * 0.14, s * 0.14);
      this.graphicsContext.setStroke(Color.LIGHTGRAY);
      this.graphicsContext.setLineWidth(1.0);
      this.graphicsContext.strokeOval(s * 0.5, micArmY - s * 0.25, s * 0.14, s * 0.14);
      // Legs
      this.graphicsContext.setStroke(color.darker());
      this.graphicsContext.setLineWidth(2.0);
      double legTop = -s * 0.5 + headR * 2 + s * 0.42;
      this.graphicsContext.strokeLine(-s * 0.12, legTop, -s * 0.16, s * 0.5);
      this.graphicsContext.strokeLine(s * 0.12, legTop, s * 0.16, s * 0.5);
      // Pulsing shield ring
      double pulseTime = this.cachedFrameTime * 0.003;
      double shieldRadius = s / 2.0 + 3.0 + Math.sin(pulseTime) * 2.0;
      this.graphicsContext.setStroke(color.brighter());
      this.graphicsContext.setLineWidth(1.5);
      this.graphicsContext.strokeOval(-shieldRadius, -shieldRadius, shieldRadius * 2.0, shieldRadius * 2.0);
      this.graphicsContext.restore();
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      this.graphicsContext.setFill(color.brighter());
      this.graphicsContext.fillOval(centerX - s / 4.0, centerY - s / 3.0, s / 3.0, s / 3.0);
      this.graphicsContext.restore();
   }

   public void drawEnemy(double x, double y, Color color, double glowIntensity, boolean isVIP) {
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + 7.5;
      double centerY = pixelY + 7.5;
      this.drawGlowEffect(centerX, centerY, color, glowIntensity);
      double size = isVIP ? 16.5 : 12.75;
      this.graphicsContext.save();
      this.graphicsContext.translate(centerX, centerY);
      this.graphicsContext.setFill(color.darker().darker());
      this.graphicsContext.fillOval(-size / 2.0, -size / 2.2, size, size * 1.2);
      this.graphicsContext.setFill(color.darker());
      this.graphicsContext.fillOval(-size / 2.5, -size / 3.0, size / 1.2, size / 1.5);
      this.graphicsContext.setStroke(color);
      this.graphicsContext.setLineWidth(2.0);
      this.graphicsContext.strokeOval(-size / 2.0, -size / 2.2, size, size * 1.2);
      double eyeSize = size / 6.0;
      this.graphicsContext.setFill(Color.WHITE);
      this.graphicsContext.fillOval(-size / 4.0 - eyeSize / 2.0, -size / 6.0, eyeSize, eyeSize);
      this.graphicsContext.fillOval(size / 4.0 - eyeSize / 2.0, -size / 6.0, eyeSize, eyeSize);
      this.graphicsContext.setFill(color.darker().darker().darker());
      this.graphicsContext.fillOval(-size / 4.0 - eyeSize / 4.0, -size / 6.0 + eyeSize / 4.0, eyeSize / 2.0, eyeSize / 2.0);
      this.graphicsContext.fillOval(size / 4.0 - eyeSize / 4.0, -size / 6.0 + eyeSize / 4.0, eyeSize / 2.0, eyeSize / 2.0);
      double mouthWidth = size / 3.0;
      this.graphicsContext.setStroke(color.darker().darker());
      this.graphicsContext.setLineWidth(1.5);
      this.graphicsContext.strokeLine(-mouthWidth / 2.0, size / 6.0, mouthWidth / 2.0, size / 6.0);
      this.graphicsContext.strokeLine(-mouthWidth / 2.0, size / 6.0, -mouthWidth / 2.0 + 2.0, size / 6.0 - 2.0);
      this.graphicsContext.strokeLine(mouthWidth / 2.0, size / 6.0, mouthWidth / 2.0 - 2.0, size / 6.0 - 2.0);
      if (isVIP) {
         double crownSize = size / 3.0;
         this.graphicsContext.setFill(Color.GOLD);
         this.graphicsContext
            .fillPolygon(
               new double[]{-crownSize, -crownSize / 2.0, 0.0, crownSize / 2.0, crownSize},
               new double[]{-size / 2.0, -size / 2.0 - crownSize / 2.0, -size / 2.0, -size / 2.0 - crownSize / 2.0, -size / 2.0},
               5
            );
         this.graphicsContext.setStroke(Color.GOLD.brighter());
         this.graphicsContext.setLineWidth(1.0);
         this.graphicsContext
            .strokePolygon(
               new double[]{-crownSize, -crownSize / 2.0, 0.0, crownSize / 2.0, crownSize},
               new double[]{-size / 2.0, -size / 2.0 - crownSize / 2.0, -size / 2.0, -size / 2.0 - crownSize / 2.0, -size / 2.0},
               5
            );
      }

      this.graphicsContext.restore();
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      this.graphicsContext.setFill(color.brighter());
      this.graphicsContext.fillOval(centerX - size / 3.0, centerY - size / 2.5, size / 3.0, size / 4.0);
      this.graphicsContext.restore();
   }

   public void drawDrone(double x, double y, Color color, double glowIntensity) {
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + 7.5;
      double centerY = pixelY + 7.5;
      this.drawGlowEffect(centerX, centerY, color, glowIntensity);
      double size = 10.5;
      double rotation = this.cachedFrameTime * 0.01;
      this.graphicsContext.save();
      this.graphicsContext.translate(centerX, centerY);
      this.graphicsContext.rotate(Math.toDegrees(rotation));

      for (int i = 0; i < 4; i++) {
         double angle = (Math.PI / 2) * i;
         double propX = Math.cos(angle) * size / 2.0;
         double propY = Math.sin(angle) * size / 2.0;
         this.graphicsContext.setStroke(color.darker());
         this.graphicsContext.setLineWidth(2.0);
         this.graphicsContext.strokeLine(0.0, 0.0, propX, propY);
         this.graphicsContext.setFill(color);
         this.graphicsContext.fillOval(propX - size / 6.0, propY - size / 6.0, size / 3.0, size / 3.0);
         this.graphicsContext.setStroke(color.brighter());
         this.graphicsContext.setLineWidth(1.0);
         this.graphicsContext.strokeOval(propX - size / 6.0, propY - size / 6.0, size / 3.0, size / 3.0);
      }

      this.graphicsContext.setFill(color.darker());
      this.graphicsContext.fillOval(-size / 4.0, -size / 4.0, size / 2.0, size / 2.0);
      this.graphicsContext.setStroke(color);
      this.graphicsContext.setLineWidth(2.0);
      this.graphicsContext.strokeOval(-size / 4.0, -size / 4.0, size / 2.0, size / 2.0);
      this.graphicsContext.setFill(color.brighter());
      this.graphicsContext.fillOval(-size / 8.0, -size / 8.0, size / 4.0, size / 4.0);
      this.graphicsContext.restore();
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      this.graphicsContext.setFill(color.brighter());
      this.graphicsContext.fillOval(centerX - size / 6.0, centerY - size / 6.0, size / 3.0, size / 3.0);
      this.graphicsContext.restore();
   }

   public void drawCollectible(double x, double y, Color color, double glowIntensity, boolean isHealthPack, boolean isPremium) {
      // Legacy overload: isPremium maps to tier 2, otherwise tier 0
      drawCollectible(x, y, color, glowIntensity, isHealthPack, isPremium ? 2 : -1);
   }

   /**
    * Draw a collectible pickup.
    * @param gemTier -1 = money/coin, 0 = small blue gem, 1 = medium green gem, 2 = large red gem
    */
   public void drawCollectible(double x, double y, Color color, double glowIntensity, boolean isHealthPack, int gemTier) {
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + 7.5;
      double centerY = pixelY + 7.5;
      this.drawGlowEffect(centerX, centerY, color, glowIntensity);
      double size = 9.0;
      double pulse = this.cachedFrameTime * 0.005;
      double scale = 1.0 + Math.sin(pulse) * 0.2;
      this.graphicsContext.save();
      this.graphicsContext.translate(centerX, centerY);
      this.graphicsContext.scale(scale, scale);
      if (isHealthPack) {
         // Green cross with white outline
         double crossSize = size / 1.8;
         double crossWidth = size / 4.0;
         this.graphicsContext.setFill(Color.WHITE);
         this.graphicsContext.fillRect(-crossSize / 2.0 - 1, -crossWidth / 2.0 - 1, crossSize + 2, crossWidth + 2);
         this.graphicsContext.fillRect(-crossWidth / 2.0 - 1, -crossSize / 2.0 - 1, crossWidth + 2, crossSize + 2);
         this.graphicsContext.setFill(Color.LIMEGREEN);
         this.graphicsContext.fillRect(-crossSize / 2.0, -crossWidth / 2.0, crossSize, crossWidth);
         this.graphicsContext.fillRect(-crossWidth / 2.0, -crossSize / 2.0, crossWidth, crossSize);
      } else if (gemTier >= 0) {
         // Diamond-shaped gem, size varies by tier
         double gemScale = gemTier == 0 ? 0.7 : gemTier == 1 ? 0.9 : 1.1;
         double half = size * gemScale / 2.0;
         this.graphicsContext.setFill(color.darker());
         this.graphicsContext.fillPolygon(
            new double[]{0, half, 0, -half},
            new double[]{-half * 1.3, 0, half * 1.3, 0}, 4);
         this.graphicsContext.setStroke(color.brighter());
         this.graphicsContext.setLineWidth(1.5);
         this.graphicsContext.strokePolygon(
            new double[]{0, half, 0, -half},
            new double[]{-half * 1.3, 0, half * 1.3, 0}, 4);
         // Inner highlight
         this.graphicsContext.setFill(color.brighter());
         double dotR = half * 0.3;
         this.graphicsContext.fillOval(-dotR, -dotR, dotR * 2, dotR * 2);
         // Large red gem gets extra glow ring
         if (gemTier == 2) {
            this.graphicsContext.setGlobalAlpha(0.3 + 0.2 * Math.sin(pulse * 3.0));
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(2.0);
            double glowR = half * 1.5;
            this.graphicsContext.strokeOval(-glowR, -glowR, glowR * 2, glowR * 2);
            this.graphicsContext.setGlobalAlpha(1.0);
         }
      } else {
         // Money/coin: gold circle with "$"
         this.graphicsContext.setFill(color.darker());
         this.graphicsContext.fillOval(-size / 2.0, -size / 2.0, size, size);
         this.graphicsContext.setStroke(color);
         this.graphicsContext.setLineWidth(2.0);
         this.graphicsContext.strokeOval(-size / 2.0, -size / 2.0, size, size);
         this.graphicsContext.setFill(color.brighter());
         this.graphicsContext.setFont(FONT_ARIAL_10);
         this.graphicsContext.fillText("$", -3.75, 3.75);
      }

      this.graphicsContext.restore();
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      this.graphicsContext.setFill(color.brighter());
      this.graphicsContext.fillOval(centerX - size / 4.0, centerY - size / 4.0, size / 2.0, size / 2.0);
      this.graphicsContext.restore();
   }

   public void drawBoomerang(double x, double y, double rotation) {
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + TILE_SIZE_HALF;
      double centerY = pixelY + TILE_SIZE_HALF;
      double size = 10.0;

      this.graphicsContext.save();
      this.graphicsContext.translate(centerX, centerY);
      this.graphicsContext.rotate(Math.toDegrees(rotation));

      // Cart body
      this.graphicsContext.setFill(Color.SILVER);
      this.graphicsContext.fillRect(-size / 2.0, -size / 4.0, size, size / 2.0);
      this.graphicsContext.setStroke(Color.LIGHTGRAY);
      this.graphicsContext.setLineWidth(2.0);
      this.graphicsContext.strokeRect(-size / 2.0, -size / 4.0, size, size / 2.0);

      // Handle
      this.graphicsContext.setStroke(Color.GRAY);
      this.graphicsContext.strokeLine(-size / 2.0, -size / 4.0, -size / 2.0 - 3, -size / 2.0);

      this.graphicsContext.restore();

      // Glow
      this.drawGlowEffect(centerX, centerY, Color.SILVER, 0.5);
   }

   public void drawArcSlash(double x, double y, double facingAngle, double range, double progress) {
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + TILE_SIZE_HALF;
      double centerY = pixelY + TILE_SIZE_HALF;
      double pixelRange = range;
      double alpha = Math.max(0.0, 1.0 - progress);

      this.graphicsContext.save();
      this.graphicsContext.setGlobalAlpha(alpha * 0.8);
      this.graphicsContext.setStroke(Color.CRIMSON);
      this.graphicsContext.setLineWidth(3.0 * (1.0 - progress * 0.5));

      double startAngleDeg = Math.toDegrees(-facingAngle) - 45;
      this.graphicsContext.strokeArc(
              centerX - pixelRange, centerY - pixelRange,
              pixelRange * 2, pixelRange * 2,
              startAngleDeg, 90,
              javafx.scene.shape.ArcType.OPEN);

      // Second slash line for visual effect
      this.graphicsContext.setStroke(Color.rgb(255, 100, 100, alpha * 0.5));
      this.graphicsContext.setLineWidth(6.0 * (1.0 - progress));
      this.graphicsContext.strokeArc(
              centerX - pixelRange * 0.7, centerY - pixelRange * 0.7,
              pixelRange * 1.4, pixelRange * 1.4,
              startAngleDeg + 10, 70,
              javafx.scene.shape.ArcType.OPEN);

      this.graphicsContext.restore();
   }

   public void drawShockwave(double x, double y, double currentRadius, double progress) {
      drawShockwave(x, y, currentRadius, progress, null);
   }

   public void drawShockwave(double x, double y, double currentRadius, double progress, Color shockColor) {
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + TILE_SIZE_HALF;
      double centerY = pixelY + TILE_SIZE_HALF;
      double pixelRadius = currentRadius;
      double alpha = Math.max(0.0, 1.0 - progress);
      Color c = shockColor != null ? shockColor : Color.MEDIUMPURPLE;

      this.graphicsContext.save();

      // Outer ring
      this.graphicsContext.setGlobalAlpha(alpha * 0.6);
      this.graphicsContext.setStroke(c);
      this.graphicsContext.setLineWidth(3.0);
      this.graphicsContext.strokeOval(centerX - pixelRadius, centerY - pixelRadius,
              pixelRadius * 2, pixelRadius * 2);

      // Inner fill
      this.graphicsContext.setGlobalAlpha(alpha * 0.15);
      this.graphicsContext.setFill(c);
      this.graphicsContext.fillOval(centerX - pixelRadius, centerY - pixelRadius,
              pixelRadius * 2, pixelRadius * 2);

      // Lightning effect for yellow/FlashSale shockwaves
      if (shockColor != null && shockColor.getRed() > 0.8 && shockColor.getGreen() > 0.8 && shockColor.getBlue() < 0.3) {
         this.graphicsContext.setGlobalAlpha(alpha * 0.8);
         this.graphicsContext.setStroke(Color.WHITE);
         this.graphicsContext.setLineWidth(1.5);
         java.util.Random rng = new java.util.Random((long)(progress * 100));
         for (int i = 0; i < 4; i++) {
            double angle = (Math.PI * 2 / 4) * i + progress * 2;
            double zx1 = centerX + Math.cos(angle) * pixelRadius * 0.5;
            double zy1 = centerY + Math.sin(angle) * pixelRadius * 0.5;
            double zx2 = centerX + Math.cos(angle) * pixelRadius + (rng.nextDouble() - 0.5) * 10;
            double zy2 = centerY + Math.sin(angle) * pixelRadius + (rng.nextDouble() - 0.5) * 10;
            this.graphicsContext.strokeLine(zx1, zy1, zx2, zy2);
         }
      }

      this.graphicsContext.restore();
   }

   public void drawText(String text, double x, double y, Color color, int fontSize) {
      Font originalFont = this.graphicsContext.getFont();
      this.graphicsContext.setFont(getCachedFont(fontSize));
      this.graphicsContext.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.2));
      this.graphicsContext.fillText(text, x + 1.0, y + 1.0);
      this.graphicsContext.setFill(color);
      this.graphicsContext.fillText(text, x, y);
      this.graphicsContext.setFont(originalFont);
   }

   public void drawBox(double x, double y, double width, double height, Color borderColor, Color fillColor) {
      if (fillColor != null) {
         this.graphicsContext.setFill(fillColor);
         this.graphicsContext.fillRect(x, y, width, height);
      }

      if (borderColor != Color.TRANSPARENT) {
         this.graphicsContext.setStroke(borderColor);
         this.graphicsContext.setLineWidth(2.0);
         this.graphicsContext.strokeRect(x, y, width, height);
      }
   }

   public void renderParticles() {
      this.particleSystem.render(this.graphicsContext, this.camera);
   }

   public void renderPendingGlows() {
      this.flushGlows();
   }

   public int getTileSize() {
      return 15;
   }

   @Generated
   public int getGridWidth() {
      return this.gridWidth;
   }

   @Generated
   public int getGridHeight() {
      return this.gridHeight;
   }

   @Generated
   public GraphicsContext getGraphicsContext() {
      return this.graphicsContext;
   }

   @Generated
   public ParticleSystem getParticleSystem() {
      return this.particleSystem;
   }

   @Generated
   public double getAnimationTime() {
      return this.animationTime;
   }

   @Generated
   public RadialGradient[] getNebulaGradients() {
      return this.nebulaGradients;
   }

   @Generated
   public long getCachedFrameTime() {
      return this.cachedFrameTime;
   }

   public Camera getCamera() {
      return this.camera;
   }

   public void drawGenericEnemy(double x, double y, Color color, double glowIntensity, EnemyType enemyType, double size, boolean hasShield, double shieldPercent) {
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + TILE_SIZE_HALF;
      double centerY = pixelY + TILE_SIZE_HALF;
      double s = size * (TILE_SIZE / 1.8) * 0.75;

      this.drawGlowEffect(centerX, centerY, color, glowIntensity);
      this.graphicsContext.save();
      this.graphicsContext.translate(centerX, centerY);

      switch (enemyType) {
         case REGULAR_CUSTOMER -> {
            // Small person with shopping bag - orange
            double headR = s * 0.25;
            this.graphicsContext.setFill(color.darker());
            this.graphicsContext.fillOval(-headR, -s/2, headR * 2, headR * 2); // head
            this.graphicsContext.fillRect(-s * 0.2, -s/2 + headR * 2, s * 0.4, s * 0.45); // body
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeOval(-headR, -s/2, headR * 2, headR * 2);
            // Legs
            this.graphicsContext.strokeLine(-s * 0.1, -s/2 + headR * 2 + s * 0.45, -s * 0.15, s/2);
            this.graphicsContext.strokeLine(s * 0.1, -s/2 + headR * 2 + s * 0.45, s * 0.15, s/2);
            // Shopping bag (right side)
            this.graphicsContext.setFill(color.brighter());
            this.graphicsContext.fillRect(s * 0.2, -s * 0.05, s * 0.3, s * 0.35);
            this.graphicsContext.setStroke(color.brighter().brighter());
            this.graphicsContext.setLineWidth(1.0);
            this.graphicsContext.strokeLine(s * 0.25, -s * 0.05, s * 0.35, -s * 0.15); // bag handle
         }

         case VIP_CUSTOMER -> {
            // Taller figure with crown and multiple bags - red
            double headR = s * 0.22;
            this.graphicsContext.setFill(color.darker());
            this.graphicsContext.fillOval(-headR, -s * 0.45, headR * 2, headR * 2); // head
            this.graphicsContext.fillRect(-s * 0.22, -s * 0.45 + headR * 2, s * 0.44, s * 0.5); // body
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeOval(-headR, -s * 0.45, headR * 2, headR * 2);
            // Legs
            this.graphicsContext.strokeLine(-s * 0.1, s * 0.05 + s * 0.15, -s * 0.15, s/2);
            this.graphicsContext.strokeLine(s * 0.1, s * 0.05 + s * 0.15, s * 0.15, s/2);
            // Crown (gold)
            double crownW = s * 0.35;
            double crownH = s * 0.18;
            this.graphicsContext.setFill(Color.GOLD);
            this.graphicsContext.fillPolygon(
               new double[]{-crownW/2, -crownW/4, 0, crownW/4, crownW/2},
               new double[]{-s * 0.45, -s * 0.45 - crownH, -s * 0.45, -s * 0.45 - crownH, -s * 0.45}, 5);
            // Two bags
            this.graphicsContext.setFill(color.brighter());
            this.graphicsContext.fillRect(-s * 0.45, 0, s * 0.2, s * 0.3);
            this.graphicsContext.fillRect(s * 0.25, -s * 0.05, s * 0.2, s * 0.3);
         }

         case KAREN -> {
            // Angular Karen haircut + pointing finger - reddish
            double headR = s * 0.25;
            this.graphicsContext.setFill(color.darker());
            this.graphicsContext.fillOval(-headR, -s * 0.3, headR * 2, headR * 2); // head
            this.graphicsContext.fillRect(-s * 0.2, -s * 0.3 + headR * 2, s * 0.4, s * 0.4); // body
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeOval(-headR, -s * 0.3, headR * 2, headR * 2);
            // Big angular Karen hair (triangle spikes on top)
            this.graphicsContext.setFill(color.darker().darker());
            this.graphicsContext.fillPolygon(
               new double[]{-headR - 3, -headR/2, 0, headR/2, headR + 3},
               new double[]{-s * 0.3, -s * 0.55, -s * 0.4, -s * 0.55, -s * 0.3}, 5);
            // Angry eyebrows
            this.graphicsContext.setStroke(Color.WHITE);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeLine(-headR * 0.6, -s * 0.22, -headR * 0.1, -s * 0.18);
            this.graphicsContext.strokeLine(headR * 0.6, -s * 0.22, headR * 0.1, -s * 0.18);
            // Pointing arm
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(2.0);
            this.graphicsContext.strokeLine(s * 0.2, -s * 0.05, s * 0.5, -s * 0.2);
            // Legs
            this.graphicsContext.strokeLine(-s * 0.1, -s * 0.3 + headR * 2 + s * 0.4, -s * 0.12, s/2);
            this.graphicsContext.strokeLine(s * 0.1, -s * 0.3 + headR * 2 + s * 0.4, s * 0.12, s/2);
         }

         case COUPON_CLIPPER -> {
            // Small hunched figure with scissors - yellowish
            double headR = s * 0.2;
            this.graphicsContext.setFill(color.darker());
            this.graphicsContext.fillOval(-headR, -s * 0.35, headR * 2, headR * 2); // head
            // Hunched body (tilted)
            this.graphicsContext.fillRect(-s * 0.2, -s * 0.35 + headR * 2 - 1, s * 0.35, s * 0.4);
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(1.0);
            this.graphicsContext.strokeOval(-headR, -s * 0.35, headR * 2, headR * 2);
            // Scissors shape (X with circles at ends)
            this.graphicsContext.setStroke(color.brighter());
            this.graphicsContext.setLineWidth(2.0);
            this.graphicsContext.strokeLine(s * 0.15, -s * 0.1, s * 0.4, -s * 0.25);
            this.graphicsContext.strokeLine(s * 0.15, -s * 0.1, s * 0.4, s * 0.05);
            this.graphicsContext.strokeOval(s * 0.38, -s * 0.3, 4, 4);
            this.graphicsContext.strokeOval(s * 0.38, s * 0.02, 4, 4);
            // Legs
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(1.0);
            this.graphicsContext.strokeLine(-s * 0.05, s * 0.05 + s * 0.1, -s * 0.1, s/2);
            this.graphicsContext.strokeLine(s * 0.1, s * 0.05 + s * 0.1, s * 0.05, s/2);
         }

         case CART_PUSHER -> {
            // Wide figure pushing shopping cart ahead - brown
            double headR = s * 0.2;
            this.graphicsContext.setFill(color.darker());
            this.graphicsContext.fillOval(-headR - s * 0.15, -s * 0.4, headR * 2, headR * 2); // head (offset left)
            this.graphicsContext.fillRect(-s * 0.35, -s * 0.4 + headR * 2, s * 0.4, s * 0.45); // wide body
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeOval(-headR - s * 0.15, -s * 0.4, headR * 2, headR * 2);
            // Shopping cart (rectangle with wheels)
            this.graphicsContext.setStroke(Color.SILVER);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeRect(s * 0.05, -s * 0.2, s * 0.4, s * 0.35);
            // Cart wheels
            this.graphicsContext.setFill(Color.GRAY);
            this.graphicsContext.fillOval(s * 0.1, s * 0.15, 3, 3);
            this.graphicsContext.fillOval(s * 0.35, s * 0.15, 3, 3);
            // Arms pushing cart
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeLine(s * 0.05, -s * 0.1, s * 0.05, -s * 0.2);
            // Legs
            this.graphicsContext.strokeLine(-s * 0.25, s * 0.05 + s * 0.1, -s * 0.3, s/2);
            this.graphicsContext.strokeLine(-s * 0.1, s * 0.05 + s * 0.1, -s * 0.05, s/2);
         }

         case SCALPER_BOT -> {
            // Robotic angular figure with antenna - cyan
            double flicker = (this.cachedFrameTime % 200 < 50) ? 0.7 : 1.0;
            this.graphicsContext.setGlobalAlpha(flicker);
            // Square head
            this.graphicsContext.setFill(color.darker());
            this.graphicsContext.fillRect(-s * 0.2, -s * 0.45, s * 0.4, s * 0.3);
            // Rectangular body
            this.graphicsContext.fillRect(-s * 0.25, -s * 0.15, s * 0.5, s * 0.4);
            this.graphicsContext.setStroke(color.brighter());
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeRect(-s * 0.2, -s * 0.45, s * 0.4, s * 0.3);
            this.graphicsContext.strokeRect(-s * 0.25, -s * 0.15, s * 0.5, s * 0.4);
            // Antenna
            this.graphicsContext.strokeLine(0, -s * 0.45, 0, -s * 0.6);
            this.graphicsContext.setFill(Color.RED);
            this.graphicsContext.fillOval(-2, -s * 0.63, 4, 4); // antenna tip
            // Glowing eyes
            this.graphicsContext.setFill(Color.rgb(0, 255, 255));
            this.graphicsContext.fillRect(-s * 0.12, -s * 0.38, s * 0.08, s * 0.06);
            this.graphicsContext.fillRect(s * 0.04, -s * 0.38, s * 0.08, s * 0.06);
            // Block legs
            this.graphicsContext.setFill(color.darker());
            this.graphicsContext.fillRect(-s * 0.2, s * 0.25, s * 0.15, s * 0.2);
            this.graphicsContext.fillRect(s * 0.05, s * 0.25, s * 0.15, s * 0.2);
            this.graphicsContext.setGlobalAlpha(1.0);
         }

         case INFLUENCER -> {
            // Figure holding phone/selfie stick - pink
            double headR = s * 0.22;
            this.graphicsContext.setFill(color.darker());
            this.graphicsContext.fillOval(-headR, -s * 0.4, headR * 2, headR * 2); // head
            this.graphicsContext.fillRect(-s * 0.18, -s * 0.4 + headR * 2, s * 0.36, s * 0.4); // body
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeOval(-headR, -s * 0.4, headR * 2, headR * 2);
            // Selfie stick + phone (rectangle held up)
            this.graphicsContext.setStroke(Color.SILVER);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeLine(s * 0.18, -s * 0.15, s * 0.4, -s * 0.45); // stick
            this.graphicsContext.setFill(Color.rgb(60, 60, 80));
            this.graphicsContext.fillRect(s * 0.32, -s * 0.58, s * 0.18, s * 0.25); // phone
            this.graphicsContext.setFill(Color.rgb(100, 180, 255));
            this.graphicsContext.fillRect(s * 0.34, -s * 0.55, s * 0.14, s * 0.18); // phone screen
            // Pink aura
            double auraSize = s * 1.2;
            this.graphicsContext.setGlobalAlpha(0.15);
            this.graphicsContext.setFill(color);
            this.graphicsContext.fillOval(-auraSize/2, -auraSize/2, auraSize, auraSize);
            this.graphicsContext.setGlobalAlpha(1.0);
            // Recording dot
            this.graphicsContext.setFill(Color.RED);
            double recPulse = Math.sin(this.cachedFrameTime * 0.005) > 0 ? 1.0 : 0.3;
            this.graphicsContext.setGlobalAlpha(recPulse);
            this.graphicsContext.fillOval(s * 0.36, -s * 0.6, 3, 3);
            this.graphicsContext.setGlobalAlpha(1.0);
            // Legs
            this.graphicsContext.setStroke(color);
            this.graphicsContext.strokeLine(-s * 0.08, s * 0.0 + s * 0.1, -s * 0.12, s/2);
            this.graphicsContext.strokeLine(s * 0.08, s * 0.0 + s * 0.1, s * 0.12, s/2);
         }

         case RETURN_FRAUDSTER -> {
            // Figure carrying box with return arrow - brown
            double headR = s * 0.22;
            this.graphicsContext.setFill(color.darker());
            this.graphicsContext.fillOval(-headR, -s * 0.4, headR * 2, headR * 2); // head
            this.graphicsContext.fillRect(-s * 0.2, -s * 0.4 + headR * 2, s * 0.4, s * 0.42); // body
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeOval(-headR, -s * 0.4, headR * 2, headR * 2);
            // Box being carried
            this.graphicsContext.setFill(Color.rgb(180, 150, 100));
            this.graphicsContext.fillRect(-s * 0.3, -s * 0.12, s * 0.6, s * 0.3);
            this.graphicsContext.setStroke(Color.rgb(140, 110, 70));
            this.graphicsContext.setLineWidth(1.0);
            this.graphicsContext.strokeRect(-s * 0.3, -s * 0.12, s * 0.6, s * 0.3);
            // Return arrow on box (curved arrow)
            this.graphicsContext.setStroke(Color.RED);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeLine(-s * 0.1, 0, s * 0.1, 0);
            this.graphicsContext.strokeLine(s * 0.05, -s * 0.05, s * 0.1, 0);
            this.graphicsContext.strokeLine(s * 0.05, s * 0.05, s * 0.1, 0);
            // Legs
            this.graphicsContext.setStroke(color);
            this.graphicsContext.strokeLine(-s * 0.1, s * 0.02 + s * 0.2, -s * 0.12, s/2);
            this.graphicsContext.strokeLine(s * 0.1, s * 0.02 + s * 0.2, s * 0.12, s/2);
         }

         case QVC_SUPERFAN -> {
            // Figure with headset, waving - gold
            double headR = s * 0.23;
            this.graphicsContext.setFill(color.darker());
            this.graphicsContext.fillOval(-headR, -s * 0.4, headR * 2, headR * 2); // head
            this.graphicsContext.fillRect(-s * 0.18, -s * 0.4 + headR * 2, s * 0.36, s * 0.4); // body
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeOval(-headR, -s * 0.4, headR * 2, headR * 2);
            // Headset (arc over head + mic)
            this.graphicsContext.setStroke(Color.rgb(80, 80, 80));
            this.graphicsContext.setLineWidth(2.0);
            this.graphicsContext.strokeArc(-headR - 2, -s * 0.5, headR * 2 + 4, headR * 1.5,
               0, 180, javafx.scene.shape.ArcType.OPEN);
            this.graphicsContext.setFill(Color.rgb(80, 80, 80));
            this.graphicsContext.fillOval(-headR - 3, -s * 0.3, 4, 6); // earpiece
            // Mic boom
            this.graphicsContext.setStroke(Color.DARKGRAY);
            this.graphicsContext.setLineWidth(1.0);
            this.graphicsContext.strokeLine(-headR - 1, -s * 0.24, -headR + 3, -s * 0.15);
            // Waving arm
            this.graphicsContext.setStroke(color.brighter());
            this.graphicsContext.setLineWidth(2.0);
            double wave = Math.sin(this.cachedFrameTime * 0.008) * s * 0.1;
            this.graphicsContext.strokeLine(s * 0.18, -s * 0.1, s * 0.4, -s * 0.35 + wave);
            // Legs
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeLine(-s * 0.08, s * 0.0 + s * 0.1, -s * 0.12, s/2);
            this.graphicsContext.strokeLine(s * 0.08, s * 0.0 + s * 0.1, s * 0.12, s/2);
         }

         case MYSTERY_BOX -> {
            // Wrapped gift box with "?" and bow - purple
            double boxPulse = 1.0 + Math.sin(this.cachedFrameTime * 0.004) * 0.12;
            double bs = s * boxPulse;
            this.graphicsContext.setFill(color.darker());
            this.graphicsContext.fillRect(-bs/2, -bs/2, bs, bs);
            this.graphicsContext.setStroke(color.brighter());
            this.graphicsContext.setLineWidth(2.0);
            this.graphicsContext.strokeRect(-bs/2, -bs/2, bs, bs);
            // Ribbon cross
            this.graphicsContext.setStroke(Color.GOLD);
            this.graphicsContext.setLineWidth(2.0);
            this.graphicsContext.strokeLine(0, -bs/2, 0, bs/2);
            this.graphicsContext.strokeLine(-bs/2, 0, bs/2, 0);
            // Bow on top
            this.graphicsContext.setFill(Color.GOLD);
            this.graphicsContext.fillOval(-bs * 0.15, -bs/2 - bs * 0.15, bs * 0.15, bs * 0.15);
            this.graphicsContext.fillOval(0, -bs/2 - bs * 0.15, bs * 0.15, bs * 0.15);
            // Question mark
            this.graphicsContext.setFill(Color.WHITE);
            this.graphicsContext.setFont(getCachedFont(Math.max(8, (int)(bs * 0.45))));
            this.graphicsContext.fillText("?", -bs * 0.12, bs * 0.15);
         }
      }

      // Shield overlay for Return Fraudster
      if (hasShield && shieldPercent > 0) {
         this.graphicsContext.setGlobalAlpha(0.4 * shieldPercent);
         this.graphicsContext.setFill(Color.rgb(100, 200, 255));
         this.graphicsContext.fillRect(-s/2 - 3, -s/2, s/3, s);
         this.graphicsContext.setGlobalAlpha(1.0);
      }

      this.graphicsContext.restore();
   }

   public void drawBoss(double x, double y, Color color, double glowIntensity, String bossType, double healthPercent) {
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + TILE_SIZE_HALF;
      double centerY = pixelY + TILE_SIZE_HALF;
      double s = 30.0;

      this.drawGlowEffect(centerX, centerY, color, glowIntensity * 1.5);

      // Rage pulse when low health
      if (healthPercent < 0.5) {
         double rageAlpha = (0.5 - healthPercent) * 2.0;
         double ragePulse = 0.5 + 0.5 * Math.sin(this.cachedFrameTime * 0.01);
         this.graphicsContext.save();
         this.graphicsContext.setGlobalAlpha(rageAlpha * ragePulse * 0.25);
         this.graphicsContext.setFill(Color.RED);
         this.graphicsContext.fillOval(centerX - s, centerY - s, s * 2, s * 2);
         this.graphicsContext.restore();
      }

      this.graphicsContext.save();
      this.graphicsContext.translate(centerX, centerY);

      switch (bossType) {
         case "Executive Producer" -> {
            // Dark suit, red tie, briefcase
            double headR = s * 0.22;
            this.graphicsContext.setFill(Color.rgb(255, 220, 180));
            this.graphicsContext.fillOval(-headR, -s * 0.55, headR * 2, headR * 2);
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(2.0);
            this.graphicsContext.strokeOval(-headR, -s * 0.55, headR * 2, headR * 2);
            // Slicked hair
            this.graphicsContext.setFill(Color.rgb(30, 20, 10));
            this.graphicsContext.fillRect(-headR, -s * 0.55, headR * 2, headR * 0.7);
            // Dark suit body
            this.graphicsContext.setFill(Color.rgb(40, 40, 50));
            this.graphicsContext.fillRect(-s * 0.3, -s * 0.55 + headR * 2, s * 0.6, s * 0.55);
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(2.0);
            this.graphicsContext.strokeRect(-s * 0.3, -s * 0.55 + headR * 2, s * 0.6, s * 0.55);
            // Shirt + red tie
            this.graphicsContext.setFill(Color.WHITE);
            this.graphicsContext.fillRect(-s * 0.1, -s * 0.55 + headR * 2, s * 0.2, s * 0.3);
            this.graphicsContext.setFill(Color.RED);
            this.graphicsContext.fillPolygon(
               new double[]{-s * 0.04, s * 0.04, s * 0.02, -s * 0.02},
               new double[]{-s * 0.55 + headR * 2, -s * 0.55 + headR * 2, -s * 0.55 + headR * 2 + s * 0.28, -s * 0.55 + headR * 2 + s * 0.28}, 4);
            // Briefcase (right side)
            this.graphicsContext.setFill(Color.rgb(100, 70, 30));
            this.graphicsContext.fillRect(s * 0.32, -s * 0.1, s * 0.22, s * 0.22);
            this.graphicsContext.setStroke(Color.rgb(150, 110, 50));
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeRect(s * 0.32, -s * 0.1, s * 0.22, s * 0.22);
            this.graphicsContext.strokeLine(s * 0.38, -s * 0.1, s * 0.38, -s * 0.1 - s * 0.06); // handle
            this.graphicsContext.strokeLine(s * 0.49, -s * 0.1, s * 0.49, -s * 0.1 - s * 0.06);
            this.graphicsContext.strokeLine(s * 0.38, -s * 0.16, s * 0.49, -s * 0.16);
            // Legs
            this.graphicsContext.setStroke(Color.rgb(40, 40, 50));
            this.graphicsContext.setLineWidth(2.5);
            double legTop = -s * 0.55 + headR * 2 + s * 0.55;
            this.graphicsContext.strokeLine(-s * 0.15, legTop, -s * 0.18, s * 0.5);
            this.graphicsContext.strokeLine(s * 0.15, legTop, s * 0.18, s * 0.5);
            // Gold "BOSS" indicator crown
            this.graphicsContext.setFill(Color.GOLD);
            double cw = s * 0.35;
            this.graphicsContext.fillPolygon(
               new double[]{-cw, -cw/2, 0, cw/2, cw},
               new double[]{-s * 0.55, -s * 0.7, -s * 0.6, -s * 0.7, -s * 0.55}, 5);
         }

         case "Warehouse Manager" -> {
            // Yellow hard hat, clipboard, bulky figure
            double headR = s * 0.22;
            this.graphicsContext.setFill(Color.rgb(255, 220, 180));
            this.graphicsContext.fillOval(-headR, -s * 0.45, headR * 2, headR * 2);
            // Hard hat
            this.graphicsContext.setFill(Color.YELLOW);
            this.graphicsContext.fillArc(-headR - 4, -s * 0.55, headR * 2 + 8, headR * 1.6, 0, 180, javafx.scene.shape.ArcType.ROUND);
            this.graphicsContext.setStroke(Color.rgb(180, 150, 0));
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeArc(-headR - 4, -s * 0.55, headR * 2 + 8, headR * 1.6, 0, 180, javafx.scene.shape.ArcType.OPEN);
            // Wide body (hi-vis vest)
            this.graphicsContext.setFill(color.darker());
            this.graphicsContext.fillRect(-s * 0.35, -s * 0.45 + headR * 2, s * 0.7, s * 0.5);
            // Orange hi-vis stripes
            this.graphicsContext.setStroke(Color.ORANGE);
            this.graphicsContext.setLineWidth(2.5);
            this.graphicsContext.strokeLine(-s * 0.35, -s * 0.45 + headR * 2 + s * 0.15, s * 0.35, -s * 0.45 + headR * 2 + s * 0.15);
            this.graphicsContext.strokeLine(-s * 0.35, -s * 0.45 + headR * 2 + s * 0.28, s * 0.35, -s * 0.45 + headR * 2 + s * 0.28);
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(2.0);
            this.graphicsContext.strokeRect(-s * 0.35, -s * 0.45 + headR * 2, s * 0.7, s * 0.5);
            // Clipboard (left side)
            this.graphicsContext.setFill(Color.rgb(220, 210, 180));
            this.graphicsContext.fillRect(-s * 0.6, -s * 0.2, s * 0.22, s * 0.3);
            this.graphicsContext.setStroke(Color.GRAY);
            this.graphicsContext.setLineWidth(1.0);
            this.graphicsContext.strokeRect(-s * 0.6, -s * 0.2, s * 0.22, s * 0.3);
            this.graphicsContext.strokeLine(-s * 0.58, -s * 0.13, -s * 0.41, -s * 0.13); // lines on clipboard
            this.graphicsContext.strokeLine(-s * 0.58, -s * 0.07, -s * 0.41, -s * 0.07);
            this.graphicsContext.strokeLine(-s * 0.58, -s * 0.01, -s * 0.41, -s * 0.01);
            // Clip at top
            this.graphicsContext.setFill(Color.GRAY);
            this.graphicsContext.fillRect(-s * 0.52, -s * 0.23, s * 0.06, s * 0.06);
            // Legs
            this.graphicsContext.setStroke(color.darker());
            this.graphicsContext.setLineWidth(3.0);
            double legTop = -s * 0.45 + headR * 2 + s * 0.5;
            this.graphicsContext.strokeLine(-s * 0.18, legTop, -s * 0.2, s * 0.5);
            this.graphicsContext.strokeLine(s * 0.18, legTop, s * 0.2, s * 0.5);
         }

         case "Door Manager" -> {
            // Dark navy uniform, cap, gold badge
            double headR = s * 0.2;
            this.graphicsContext.setFill(Color.rgb(255, 220, 180));
            this.graphicsContext.fillOval(-headR, -s * 0.45, headR * 2, headR * 2);
            // Cap
            this.graphicsContext.setFill(Color.rgb(20, 30, 80));
            this.graphicsContext.fillRect(-headR - 3, -s * 0.45, headR * 2 + 6, headR * 0.8);
            this.graphicsContext.setFill(Color.rgb(10, 20, 60));
            this.graphicsContext.fillRect(-headR - 5, -s * 0.45 + headR * 0.8 - 2, headR * 2 + 10, 4); // brim
            // Navy uniform body
            this.graphicsContext.setFill(Color.rgb(20, 30, 80));
            this.graphicsContext.fillRect(-s * 0.28, -s * 0.45 + headR * 2, s * 0.56, s * 0.5);
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(2.0);
            this.graphicsContext.strokeRect(-s * 0.28, -s * 0.45 + headR * 2, s * 0.56, s * 0.5);
            // Gold epaulettes
            this.graphicsContext.setFill(Color.GOLD);
            this.graphicsContext.fillRect(-s * 0.3, -s * 0.45 + headR * 2, s * 0.1, s * 0.08);
            this.graphicsContext.fillRect(s * 0.2, -s * 0.45 + headR * 2, s * 0.1, s * 0.08);
            // Gold badge on chest
            double badgeX = s * 0.05;
            double badgeY = -s * 0.45 + headR * 2 + s * 0.12;
            this.graphicsContext.setFill(Color.GOLD);
            this.graphicsContext.fillPolygon(
               new double[]{badgeX, badgeX + s * 0.07, badgeX + s * 0.12, badgeX + s * 0.07, badgeX, badgeX - s * 0.05},
               new double[]{badgeY, badgeY + s * 0.02, badgeY + s * 0.08, badgeY + s * 0.14, badgeY + s * 0.12, badgeY + s * 0.08},
               6);
            // Arm extended (guarding door)
            this.graphicsContext.setStroke(Color.rgb(20, 30, 80));
            this.graphicsContext.setLineWidth(2.5);
            this.graphicsContext.strokeLine(s * 0.28, -s * 0.2, s * 0.5, -s * 0.05);
            // Legs
            this.graphicsContext.setStroke(Color.rgb(20, 30, 80));
            this.graphicsContext.setLineWidth(2.5);
            double legTop2 = -s * 0.45 + headR * 2 + s * 0.5;
            this.graphicsContext.strokeLine(-s * 0.13, legTop2, -s * 0.15, s * 0.5);
            this.graphicsContext.strokeLine(s * 0.13, legTop2, s * 0.15, s * 0.5);
         }

         case "Return Fraud Kingpin" -> {
            // Wide trench coat, fedora, surrounded by boxes
            double headR = s * 0.2;
            // Stacked return boxes around feet
            this.graphicsContext.setFill(Color.rgb(180, 150, 100));
            this.graphicsContext.fillRect(-s * 0.5, s * 0.2, s * 0.3, s * 0.25);
            this.graphicsContext.fillRect(s * 0.2, s * 0.1, s * 0.28, s * 0.32);
            this.graphicsContext.setStroke(Color.rgb(140, 110, 70));
            this.graphicsContext.setLineWidth(1.0);
            this.graphicsContext.strokeRect(-s * 0.5, s * 0.2, s * 0.3, s * 0.25);
            this.graphicsContext.strokeRect(s * 0.2, s * 0.1, s * 0.28, s * 0.32);
            // Big red tape X on boxes
            this.graphicsContext.setStroke(Color.RED);
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeLine(-s * 0.5, s * 0.2, -s * 0.2, s * 0.45);
            this.graphicsContext.strokeLine(-s * 0.2, s * 0.2, -s * 0.5, s * 0.45);
            // Fedora
            this.graphicsContext.setFill(Color.rgb(60, 45, 30));
            this.graphicsContext.fillOval(-headR - 6, -s * 0.55, headR * 2 + 12, headR); // brim
            this.graphicsContext.fillRect(-headR - 2, -s * 0.65, headR * 2 + 4, headR * 1.5); // crown
            this.graphicsContext.setStroke(Color.rgb(100, 75, 50));
            this.graphicsContext.setLineWidth(1.5);
            this.graphicsContext.strokeOval(-headR - 6, -s * 0.55, headR * 2 + 12, headR);
            this.graphicsContext.strokeRect(-headR - 2, -s * 0.65, headR * 2 + 4, headR * 1.5);
            // Head
            this.graphicsContext.setFill(Color.rgb(255, 220, 180));
            this.graphicsContext.fillOval(-headR, -s * 0.5, headR * 2, headR * 2);
            // Sneaky eyes
            this.graphicsContext.setFill(Color.rgb(50, 30, 10));
            this.graphicsContext.fillOval(-headR * 0.6, -s * 0.38, 3, 3);
            this.graphicsContext.fillOval(headR * 0.1, -s * 0.38, 3, 3);
            // Long trench coat (wide trapezoid body)
            this.graphicsContext.setFill(Color.rgb(90, 70, 50));
            double bodyTop = -s * 0.5 + headR * 2;
            this.graphicsContext.fillPolygon(
               new double[]{-s * 0.32, s * 0.32, s * 0.45, -s * 0.45},
               new double[]{bodyTop, bodyTop, s * 0.35, s * 0.35}, 4);
            this.graphicsContext.setStroke(Color.rgb(130, 100, 70));
            this.graphicsContext.setLineWidth(2.0);
            this.graphicsContext.strokePolygon(
               new double[]{-s * 0.32, s * 0.32, s * 0.45, -s * 0.45},
               new double[]{bodyTop, bodyTop, s * 0.35, s * 0.35}, 4);
            // Coat lapels
            this.graphicsContext.setFill(Color.rgb(110, 85, 60));
            this.graphicsContext.fillPolygon(
               new double[]{-s * 0.04, s * 0.04, 0},
               new double[]{bodyTop, bodyTop, bodyTop + s * 0.2}, 3);
         }

         case "Board of Directors" -> {
            // Three suits in formation
            double[] offsets = {-s * 0.38, 0, s * 0.38};
            Color[] suitColors = {Color.rgb(40, 40, 55), Color.rgb(30, 30, 50), Color.rgb(50, 40, 55)};
            for (int i = 0; i < 3; i++) {
               double ox = offsets[i];
               double oy = (i == 1) ? -s * 0.1 : 0; // center one slightly higher
               double sm = s * 0.32; // mini size
               double mhr = sm * 0.28;
               // Head
               this.graphicsContext.setFill(Color.rgb(255, 220, 180));
               this.graphicsContext.fillOval(ox - mhr, oy - sm * 0.55, mhr * 2, mhr * 2);
               // Body
               this.graphicsContext.setFill(suitColors[i]);
               this.graphicsContext.fillRect(ox - sm * 0.3, oy - sm * 0.55 + mhr * 2, sm * 0.6, sm * 0.5);
               // Red tie on center suit
               if (i == 1) {
                  this.graphicsContext.setFill(Color.RED);
                  this.graphicsContext.fillPolygon(
                     new double[]{ox - sm * 0.04, ox + sm * 0.04, ox + sm * 0.02, ox - sm * 0.02},
                     new double[]{oy - sm * 0.55 + mhr * 2, oy - sm * 0.55 + mhr * 2, oy - sm * 0.55 + mhr * 2 + sm * 0.28, oy - sm * 0.55 + mhr * 2 + sm * 0.28},
                     4);
               }
               this.graphicsContext.setStroke(color);
               this.graphicsContext.setLineWidth(1.5);
               this.graphicsContext.strokeOval(ox - mhr, oy - sm * 0.55, mhr * 2, mhr * 2);
               this.graphicsContext.strokeRect(ox - sm * 0.3, oy - sm * 0.55 + mhr * 2, sm * 0.6, sm * 0.5);
            }
            // Gold crown above center figure
            double cw = s * 0.28;
            this.graphicsContext.setFill(Color.GOLD);
            this.graphicsContext.fillPolygon(
               new double[]{-cw, -cw/2, 0, cw/2, cw},
               new double[]{-s * 0.65, -s * 0.78, -s * 0.7, -s * 0.78, -s * 0.65}, 5);
         }

         default -> {
            // Fallback generic boss blob
            this.graphicsContext.setFill(color.darker().darker());
            this.graphicsContext.fillOval(-s/2, -s/2.2, s, s * 1.1);
            this.graphicsContext.setStroke(color);
            this.graphicsContext.setLineWidth(3.0);
            this.graphicsContext.strokeOval(-s/2, -s/2.2, s, s * 1.1);
         }
      }

      this.graphicsContext.restore();

      // Highlight
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      this.graphicsContext.setFill(color.brighter());
      this.graphicsContext.fillOval(centerX - s/4, centerY - s/3, s/3, s/4);
      this.graphicsContext.restore();
   }

   public void drawBossIncoming(double alpha) {
      double centerX = this.getWidth() / 2.0;
      double centerY = this.getHeight() / 2.0;
      this.graphicsContext.save();
      this.graphicsContext.setGlobalAlpha(alpha);
      this.drawText("BOSS INCOMING", centerX - 100, centerY - 50, Color.rgb(255, 50, 50), 36);
      this.graphicsContext.restore();
   }
}
