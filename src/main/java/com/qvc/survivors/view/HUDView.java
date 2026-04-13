package com.qvc.survivors.view;

import com.qvc.survivors.model.entity.Player;
import javafx.scene.paint.Color;

public class HUDView {
   private static final Color HUD_COLOR = Color.rgb(200, 220, 255);
   private static final Color ACCENT_COLOR = Color.rgb(100, 200, 255);
   private static final int FONT_SIZE = 14;
   private final GameView gameView;

   public HUDView(GameView gameView) {
      this.gameView = gameView;
   }

   public void render(Player player, int currentWave) {
      int canvasWidth = (int)this.gameView.getWidth();
      this.renderLeftHUD(player);
      this.renderRightHUD(player, currentWave, canvasWidth);
   }

   private void renderLeftHUD(Player player) {
      int y = 20;
      String healthText = String.format("Stamina: %.0f/%.0f", player.getHealthComponent().getCurrentHealth(), player.getHealthComponent().getMaxHealth());
      double healthPercent = player.getHealthComponent().getHealthPercentage();
      Color healthColor = healthPercent > 0.5 ? Color.LIGHTGREEN : (healthPercent > 0.25 ? Color.YELLOW : Color.RED);
      this.gameView.drawText(healthText, 10.0, y, healthColor, 14);
      y += 20;
      String levelText = String.format("Level: %d", player.getLevel());
      this.gameView.drawText(levelText, 10.0, y, ACCENT_COLOR, 14);
      y += 20;
      double xpPercent = player.getExperience() / player.getExperienceThreshold() * 100.0;
      String xpText = String.format("XP: %.0f/%.0f (%.0f%%)", player.getExperience(), player.getExperienceThreshold(), xpPercent);
      this.gameView.drawText(xpText, 10.0, y, HUD_COLOR, 14);
      this.renderExperienceBar(player, 10, y + 5);
      if (player.isInvulnerable()) {
         y += 35;
         String invulnText = String.format("☕ COFFEE BREAK: %.1fs", player.getInvulnerabilityTimer());
         this.gameView.drawText(invulnText, 10.0, y, Color.YELLOW, 14);
      }
   }

   private void renderExperienceBar(Player player, int x, int y) {
      int barWidth = 200;
      int barHeight = 10;
      double pulse = 0.3 + 0.2 * Math.sin(System.currentTimeMillis() * 0.005);
      Color glowColor = Color.rgb(100, 200, 255, pulse);
      this.gameView.drawBox(x - 2, y - 2, barWidth + 4, barHeight + 4, Color.TRANSPARENT, glowColor);
      this.gameView.drawBox(x, y, barWidth, barHeight, ACCENT_COLOR, Color.rgb(20, 20, 30));
      double progress = player.getExperience() / player.getExperienceThreshold();
      int filledWidth = (int)(barWidth * Math.min(progress, 1.0));
      if (filledWidth > 0) {
         Color barColor = Color.hsb(System.currentTimeMillis() * 0.1 % 360.0, 0.6, 1.0);
         this.gameView.drawBox(x, y, filledWidth, barHeight, Color.TRANSPARENT, barColor);
         Color brightGlow = Color.rgb((int)(barColor.getRed() * 255.0), (int)(barColor.getGreen() * 255.0), (int)(barColor.getBlue() * 255.0), 0.5);
         this.gameView.drawBox(x, y, filledWidth, barHeight / 2.0, Color.TRANSPARENT, brightGlow);
      }
   }

   private void renderRightHUD(Player player, int currentWave, int canvasWidth) {
      int x = canvasWidth - 200;
      int y = 20;
      String moneyText = String.format("Tips: $%d", player.getMoney());
      this.gameView.drawText(moneyText, x, y, Color.YELLOW, 14);
      y += 20;
      String customersText = String.format("Customers: %d", player.getCustomersSatisfied());
      this.gameView.drawText(customersText, x, y, HUD_COLOR, 14);
      y += 20;
      int minutes = (int)player.getSurvivalTime() / 60;
      int seconds = (int)player.getSurvivalTime() % 60;
      String timeText = String.format("Time: %02d:%02d", minutes, seconds);
      this.gameView.drawText(timeText, x, y, HUD_COLOR, 14);
      y += 20;
      String waveText = String.format("Wave: %d", currentWave);
      this.gameView.drawText(waveText, x, y, ACCENT_COLOR, 14);
   }
}
