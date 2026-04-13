package com.qvc.survivors.view;

import com.qvc.survivors.engine.Camera;
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
   private final int gridWidth;
   private final int gridHeight;
   private final GraphicsContext graphicsContext;
   private final ParticleSystem particleSystem;
   private double animationTime;
   private RadialGradient[] nebulaGradients;
   private static final double TILE_SIZE_HALF = 7.5;
   private long cachedFrameTime;
   private Camera camera;

   public GameView(int gridWidth, int gridHeight, double viewportWidth, double viewportHeight) {
      super(viewportWidth, viewportHeight);
      this.gridWidth = gridWidth;
      this.gridHeight = gridHeight;
      this.graphicsContext = this.getGraphicsContext2D();
      this.particleSystem = new ParticleSystem(2000);
      this.animationTime = 0.0;
      Font font = Font.font("Courier New", FontWeight.BOLD, 15.0);
      this.graphicsContext.setFont(font);
      this.graphicsContext.setImageSmoothing(false);
      this.initializeNebulaGradients();
   }

   public void setCamera(Camera camera) {
      this.camera = camera;
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
      double glowRadius = 30.0 * glowIntensity;
      RadialGradient gradient = new RadialGradient(
         0.0,
         0.0,
         centerX,
         centerY,
         glowRadius,
         false,
         CycleMethod.NO_CYCLE,
         new Stop[]{
            new Stop(0.0, Color.rgb((int)(color.getRed() * 255.0), (int)(color.getGreen() * 255.0), (int)(color.getBlue() * 255.0), 0.3 * glowIntensity)),
            new Stop(1.0, Color.TRANSPARENT)
         }
      );
      this.graphicsContext.setFill(gradient);
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
      this.drawGrid();
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
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      double glowRadius = 30.0 * glowIntensity;
      RadialGradient gradient = new RadialGradient(
         0.0,
         0.0,
         centerX,
         centerY,
         glowRadius,
         false,
         CycleMethod.NO_CYCLE,
         new Stop[]{
            new Stop(0.0, Color.rgb((int)(color.getRed() * 255.0), (int)(color.getGreen() * 255.0), (int)(color.getBlue() * 255.0), 0.3 * glowIntensity)),
            new Stop(1.0, Color.TRANSPARENT)
         }
      );
      this.graphicsContext.setFill(gradient);
      this.graphicsContext.fillOval(centerX - glowRadius, centerY - glowRadius, glowRadius * 2.0, glowRadius * 2.0);
      this.graphicsContext.restore();
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
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      double glowRadius = 30.0 * glowIntensity;
      RadialGradient gradient = new RadialGradient(
         0.0,
         0.0,
         centerX,
         centerY,
         glowRadius,
         false,
         CycleMethod.NO_CYCLE,
         new Stop[]{
            new Stop(0.0, Color.rgb((int)(color.getRed() * 255.0), (int)(color.getGreen() * 255.0), (int)(color.getBlue() * 255.0), 0.3 * glowIntensity)),
            new Stop(1.0, Color.TRANSPARENT)
         }
      );
      this.graphicsContext.setFill(gradient);
      this.graphicsContext.fillOval(centerX - glowRadius, centerY - glowRadius, glowRadius * 2.0, glowRadius * 2.0);
      this.graphicsContext.restore();
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
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      double glowRadius = 30.0 * glowIntensity;
      RadialGradient gradient = new RadialGradient(
         0.0,
         0.0,
         centerX,
         centerY,
         glowRadius,
         false,
         CycleMethod.NO_CYCLE,
         new Stop[]{
            new Stop(0.0, Color.rgb((int)(color.getRed() * 255.0), (int)(color.getGreen() * 255.0), (int)(color.getBlue() * 255.0), 0.3 * glowIntensity)),
            new Stop(1.0, Color.TRANSPARENT)
         }
      );
      this.graphicsContext.setFill(gradient);
      this.graphicsContext.fillOval(centerX - glowRadius, centerY - glowRadius, glowRadius * 2.0, glowRadius * 2.0);
      this.graphicsContext.restore();
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
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      double glowRadius = 30.0 * glowIntensity;
      RadialGradient gradient = new RadialGradient(
         0.0,
         0.0,
         centerX,
         centerY,
         glowRadius,
         false,
         CycleMethod.NO_CYCLE,
         new Stop[]{
            new Stop(0.0, Color.rgb((int)(color.getRed() * 255.0), (int)(color.getGreen() * 255.0), (int)(color.getBlue() * 255.0), 0.3 * glowIntensity)),
            new Stop(1.0, Color.TRANSPARENT)
         }
      );
      this.graphicsContext.setFill(gradient);
      this.graphicsContext.fillOval(centerX - glowRadius, centerY - glowRadius, glowRadius * 2.0, glowRadius * 2.0);
      this.graphicsContext.restore();
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
         this.graphicsContext.setFont(Font.font("Arial", FontWeight.BOLD, 10.0));
         this.graphicsContext.fillText("$", -3.75, 3.75);
      }

      this.graphicsContext.restore();
      this.graphicsContext.save();
      this.graphicsContext.setGlobalBlendMode(BlendMode.ADD);
      this.graphicsContext.setFill(color.brighter());
      this.graphicsContext.fillOval(centerX - size / 4.0, centerY - size / 4.0, size / 2.0, size / 2.0);
      this.graphicsContext.restore();
   }

   public void drawText(String text, double x, double y, Color color, int fontSize) {
      Font originalFont = this.graphicsContext.getFont();
      this.graphicsContext.setFont(Font.font("Courier New", FontWeight.BOLD, fontSize));
      this.graphicsContext.setFill(Color.rgb((int)(color.getRed() * 255.0), (int)(color.getGreen() * 255.0), (int)(color.getBlue() * 255.0), 0.2));
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
}
