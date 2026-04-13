package com.qvc.survivors.view;

import com.qvc.survivors.engine.Camera;
import com.qvc.survivors.model.entity.EnemyType;
import com.qvc.survivors.world.MapCollectibleType;
import com.qvc.survivors.world.TileMap;
import com.qvc.survivors.world.TileType;
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
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      double glowRadius = 20.0 * glowIntensity;
      this.graphicsContext.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(0.15 * glowIntensity, 0.4)));
      this.graphicsContext.fillOval(centerX - glowRadius, centerY - glowRadius, glowRadius * 2.0, glowRadius * 2.0);
      this.graphicsContext.restore();
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
      this.graphicsContext.setStroke(Color.rgb(255, 255, 255, 0.015));
      this.graphicsContext.setLineWidth(1.0);
      double offset = this.animationTime * 30.0 % 4.0;

      for (int i = 0; i < this.getHeight(); i += 4) {
         this.graphicsContext.strokeLine(0.0, i + offset, this.getWidth(), i + offset);
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
      double boxSize = 12.0;
      this.drawGlowEffect(centerX, centerY, color, glowIntensity);
      double rotation = this.cachedFrameTime * 0.005;
      this.graphicsContext.save();
      this.graphicsContext.translate(centerX, centerY);
      this.graphicsContext.rotate(Math.toDegrees(rotation));
      Color boxColor = color.darker();
      this.graphicsContext.setFill(boxColor);
      this.graphicsContext.fillRect(-boxSize / 2.0, -boxSize / 2.0, boxSize, boxSize);
      this.graphicsContext.setStroke(color);
      this.graphicsContext.setLineWidth(2.0);
      this.graphicsContext.strokeRect(-boxSize / 2.0, -boxSize / 2.0, boxSize, boxSize);
      this.graphicsContext.setStroke(color.brighter());
      this.graphicsContext.setLineWidth(1.0);
      this.graphicsContext.strokeLine(-boxSize / 2.0, 0.0, boxSize / 2.0, 0.0);
      this.graphicsContext.strokeLine(0.0, -boxSize / 2.0, 0.0, boxSize / 2.0);
      this.graphicsContext.restore();
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      this.graphicsContext.setFill(color.brighter());
      this.graphicsContext.fillRect(centerX - boxSize / 2.0, centerY - boxSize / 2.0, boxSize, boxSize / 3.0);
      this.graphicsContext.restore();
   }

   public void drawPlayer(double x, double y, Color color, double glowIntensity) {
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + 7.5;
      double centerY = pixelY + 7.5;
      this.drawGlowEffect(centerX, centerY, color, glowIntensity);
      double size = 13.5;
      this.graphicsContext.save();
      this.graphicsContext.translate(centerX, centerY);
      this.graphicsContext.setFill(color.darker());
      this.graphicsContext.fillOval(-size / 2.0, -size / 2.0, size, size);
      this.graphicsContext.setStroke(color);
      this.graphicsContext.setLineWidth(2.5);
      this.graphicsContext.strokeOval(-size / 2.0, -size / 2.0, size, size);
      this.graphicsContext.setFill(color.brighter());
      this.graphicsContext.fillText("Q", -5.0, 3.75);
      double pulseTime = this.cachedFrameTime * 0.003;
      double shieldRadius = size / 2.0 + 2.0 + Math.sin(pulseTime) * 2.0;
      this.graphicsContext.setStroke(color.brighter());
      this.graphicsContext.setLineWidth(1.5);
      this.graphicsContext.strokeOval(-shieldRadius, -shieldRadius, shieldRadius * 2.0, shieldRadius * 2.0);
      this.graphicsContext.restore();
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      this.graphicsContext.setFill(color.brighter());
      this.graphicsContext.fillOval(centerX - size / 4.0, centerY - size / 3.0, size / 3.0, size / 3.0);
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
         double crossSize = size / 2.0;
         double crossWidth = size / 5.0;
         this.graphicsContext.setFill(Color.WHITE);
         this.graphicsContext.fillRect(-crossSize / 2.0, -crossWidth / 2.0, crossSize, crossWidth);
         this.graphicsContext.fillRect(-crossWidth / 2.0, -crossSize / 2.0, crossWidth, crossSize);
         this.graphicsContext.setFill(color);
         this.graphicsContext.fillRect(-crossSize / 2.0 + 1.0, -crossWidth / 2.0 + 1.0, crossSize - 2.0, crossWidth - 2.0);
         this.graphicsContext.fillRect(-crossWidth / 2.0 + 1.0, -crossSize / 2.0 + 1.0, crossWidth - 2.0, crossSize - 2.0);
         this.graphicsContext.setStroke(color.brighter());
         this.graphicsContext.setLineWidth(1.5);
         this.graphicsContext.strokeRect(-crossSize / 2.0, -crossWidth / 2.0, crossSize, crossWidth);
         this.graphicsContext.strokeRect(-crossWidth / 2.0, -crossSize / 2.0, crossWidth, crossSize);
      } else if (isPremium) {
         double starPoints = 8.0;
         double outerRadius = size / 2.0;
         double innerRadius = size / 4.0;
         double[] xPoints = new double[(int)starPoints * 2];
         double[] yPoints = new double[(int)starPoints * 2];

         for (int i = 0; i < starPoints * 2.0; i++) {
            double angle = Math.PI * i / starPoints;
            double radius = i % 2 == 0 ? outerRadius : innerRadius;
            xPoints[i] = Math.cos(angle) * radius;
            yPoints[i] = Math.sin(angle) * radius;
         }

         this.graphicsContext.setFill(color.darker());
         this.graphicsContext.fillPolygon(xPoints, yPoints, (int)starPoints * 2);
         this.graphicsContext.setStroke(color);
         this.graphicsContext.setLineWidth(2.0);
         this.graphicsContext.strokePolygon(xPoints, yPoints, (int)starPoints * 2);
         this.graphicsContext.setFill(color.brighter());
         this.graphicsContext.fillOval(-size / 6.0, -size / 6.0, size / 3.0, size / 3.0);
      } else {
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
      double pixelX = camera.worldToScreenX(x);
      double pixelY = camera.worldToScreenY(y);
      double centerX = pixelX + TILE_SIZE_HALF;
      double centerY = pixelY + TILE_SIZE_HALF;
      double pixelRadius = currentRadius;
      double alpha = Math.max(0.0, 1.0 - progress);

      this.graphicsContext.save();

      // Outer ring
      this.graphicsContext.setGlobalAlpha(alpha * 0.6);
      this.graphicsContext.setStroke(Color.MEDIUMPURPLE);
      this.graphicsContext.setLineWidth(3.0);
      this.graphicsContext.strokeOval(centerX - pixelRadius, centerY - pixelRadius,
              pixelRadius * 2, pixelRadius * 2);

      // Inner fill
      this.graphicsContext.setGlobalAlpha(alpha * 0.15);
      this.graphicsContext.setFill(Color.MEDIUMPURPLE);
      this.graphicsContext.fillOval(centerX - pixelRadius, centerY - pixelRadius,
              pixelRadius * 2, pixelRadius * 2);

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
      double baseSize = 30.0;

      // Large glow
      this.drawGlowEffect(centerX, centerY, color, glowIntensity);

      this.graphicsContext.save();
      this.graphicsContext.translate(centerX, centerY);

      // Main body - larger oval
      this.graphicsContext.setFill(color.darker().darker());
      this.graphicsContext.fillOval(-baseSize/2, -baseSize/2.2, baseSize, baseSize * 1.1);
      this.graphicsContext.setStroke(color);
      this.graphicsContext.setLineWidth(3.0);
      this.graphicsContext.strokeOval(-baseSize/2, -baseSize/2.2, baseSize, baseSize * 1.1);

      // Inner detail
      this.graphicsContext.setFill(color.darker());
      this.graphicsContext.fillOval(-baseSize/3, -baseSize/3, baseSize/1.5, baseSize/1.5);

      // Eyes (larger, more menacing)
      double eyeSize = baseSize / 5.0;
      this.graphicsContext.setFill(Color.WHITE);
      this.graphicsContext.fillOval(-baseSize/4 - eyeSize/2, -baseSize/6, eyeSize, eyeSize);
      this.graphicsContext.fillOval(baseSize/4 - eyeSize/2, -baseSize/6, eyeSize, eyeSize);
      this.graphicsContext.setFill(Color.RED);
      this.graphicsContext.fillOval(-baseSize/4, -baseSize/6 + eyeSize/4, eyeSize/2, eyeSize/2);
      this.graphicsContext.fillOval(baseSize/4 - eyeSize/4, -baseSize/6 + eyeSize/4, eyeSize/2, eyeSize/2);

      // Crown/indicator on top
      this.graphicsContext.setFill(Color.GOLD);
      double crownSize = baseSize / 3.0;
      this.graphicsContext.fillPolygon(
         new double[]{-crownSize, -crownSize/2, 0, crownSize/2, crownSize},
         new double[]{-baseSize/2, -baseSize/2 - crownSize*0.7, -baseSize/2 - crownSize*0.3, -baseSize/2 - crownSize*0.7, -baseSize/2},
         5);
      this.graphicsContext.setStroke(Color.GOLD.brighter());
      this.graphicsContext.setLineWidth(1.5);
      this.graphicsContext.strokePolygon(
         new double[]{-crownSize, -crownSize/2, 0, crownSize/2, crownSize},
         new double[]{-baseSize/2, -baseSize/2 - crownSize*0.7, -baseSize/2 - crownSize*0.3, -baseSize/2 - crownSize*0.7, -baseSize/2},
         5);

      // Health-based rage indicator
      if (healthPercent < 0.5) {
         double rageAlpha = (0.5 - healthPercent) * 2.0;
         double ragePulse = 0.5 + 0.5 * Math.sin(this.cachedFrameTime * 0.01);
         this.graphicsContext.setGlobalAlpha(rageAlpha * ragePulse * 0.3);
         this.graphicsContext.setFill(Color.RED);
         double rageSize = baseSize * 1.5;
         this.graphicsContext.fillOval(-rageSize/2, -rageSize/2, rageSize, rageSize);
         this.graphicsContext.setGlobalAlpha(1.0);
      }

      this.graphicsContext.restore();

      // Highlight
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      this.graphicsContext.setFill(color.brighter());
      this.graphicsContext.fillOval(centerX - baseSize/4, centerY - baseSize/3, baseSize/3, baseSize/4);
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
