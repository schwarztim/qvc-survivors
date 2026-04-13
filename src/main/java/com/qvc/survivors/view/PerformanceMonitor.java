package com.qvc.survivors.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PerformanceMonitor {
   private static final int SAMPLE_SIZE = 60;
   private static final double UPDATE_INTERVAL = 0.5;
   private final long[] frameTimes = new long[60];
   private int frameIndex = 0;
   private double updateTimer = 0.0;
   private double currentFps = 0.0;
   private long currentMemoryUsed = 0L;
   private long currentMemoryTotal = 0L;
   private boolean enabled = true;

   public void update(double deltaTime, long frameTime) {
      this.frameTimes[this.frameIndex] = frameTime;
      this.frameIndex = (this.frameIndex + 1) % 60;
      this.updateTimer += deltaTime;
      if (this.updateTimer >= 0.5) {
         this.updateTimer = 0.0;
         this.calculateFps();
         this.updateMemoryStats();
      }
   }

   private void calculateFps() {
      long totalTime = 0L;
      int validSamples = 0;

      for (long frameTime : this.frameTimes) {
         if (frameTime > 0L) {
            totalTime += frameTime;
            validSamples++;
         }
      }

      if (validSamples > 0) {
         double averageFrameTime = (double)totalTime / validSamples;
         this.currentFps = 1.0E9 / averageFrameTime;
      }
   }

   private void updateMemoryStats() {
      Runtime runtime = Runtime.getRuntime();
      this.currentMemoryTotal = runtime.totalMemory();
      this.currentMemoryUsed = this.currentMemoryTotal - runtime.freeMemory();
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void render(GraphicsContext gc, double canvasWidth, double canvasHeight) {
      if (!this.enabled) return;
      double x = canvasWidth - 150.0;
      double y = canvasHeight - 60.0;
      gc.save();
      gc.setFill(Color.rgb(0, 0, 0, 0.7));
      gc.fillRect(x - 10.0, y - 5.0, 145.0, 55.0);
      gc.setStroke(Color.rgb(100, 100, 100));
      gc.setLineWidth(1.0);
      gc.strokeRect(x - 10.0, y - 5.0, 145.0, 55.0);
      gc.setFont(Font.font("Monospace", FontWeight.BOLD, 12.0));
      Color fpsColor = this.getFpsColor(this.currentFps);
      gc.setFill(fpsColor);
      gc.fillText(String.format("FPS: %.0f", this.currentFps), x, y + 10.0);
      double memoryUsedMb = this.currentMemoryUsed / 1048576.0;
      double memoryTotalMb = this.currentMemoryTotal / 1048576.0;
      double memoryPercent = (double)this.currentMemoryUsed / this.currentMemoryTotal * 100.0;
      Color memColor = this.getMemoryColor(memoryPercent);
      gc.setFill(memColor);
      gc.fillText(String.format("MEM: %.0f/%.0f MB", memoryUsedMb, memoryTotalMb), x, y + 25.0);
      gc.fillText(String.format("(%.0f%%)", memoryPercent), x, y + 40.0);
      gc.restore();
   }

   private Color getFpsColor(double fps) {
      if (fps >= 55.0) {
         return Color.rgb(0, 255, 0);
      } else if (fps >= 45.0) {
         return Color.rgb(255, 255, 0);
      } else {
         return fps >= 30.0 ? Color.rgb(255, 165, 0) : Color.rgb(255, 0, 0);
      }
   }

   private Color getMemoryColor(double percent) {
      if (percent < 60.0) {
         return Color.rgb(0, 255, 0);
      } else if (percent < 75.0) {
         return Color.rgb(255, 255, 0);
      } else {
         return percent < 85.0 ? Color.rgb(255, 165, 0) : Color.rgb(255, 0, 0);
      }
   }
}
