package com.qvc.survivors.view;

import com.qvc.survivors.model.entity.BossEnemy;
import com.qvc.survivors.model.entity.BoardOfDirectors;
import com.qvc.survivors.model.entity.Player;
import com.qvc.survivors.world.ZoneType;
import javafx.scene.paint.Color;

public class HUDView {
   private static final Color HUD_COLOR = Color.rgb(200, 220, 255);
   private static final Color ACCENT_COLOR = Color.rgb(100, 200, 255);
   private static final Color BAR_BG_COLOR = Color.rgb(20, 20, 30);
   private static final Color BOSS_BAR_RED = Color.rgb(255, 50, 50);
   private static final Color BOSS_BAR_BG = Color.rgb(40, 10, 10);
   private static final Color BOSS_BAR_OVERLAY = Color.rgb(0, 0, 0, 0.7);
   private static final Color BOSS_NAME_COLOR = Color.rgb(255, 200, 200);
   private static final Color TOP_CENTER_ZONE = Color.rgb(180, 200, 230, 0.6);
   private static final Color TOP_CENTER_WEAPON = Color.rgb(100, 200, 255, 0.7);
   private static final Color TOP_XP_BG = Color.rgb(30, 35, 50, 0.6);
   private static final Color TOP_XP_FILL = Color.rgb(100, 200, 255, 0.8);
   private static final Color SHIELD_BLUE = Color.rgb(100, 200, 255);
   private static final int FONT_SIZE = 14;
   private final GameView gameView;

   public HUDView(GameView gameView) {
      this.gameView = gameView;
   }

   public void render(Player player, int currentWave) {
      render(player, currentWave, null);
   }

   public void render(Player player, int currentWave, ZoneType currentZone) {
      int canvasWidth = (int)this.gameView.getWidth();
      this.renderLeftHUD(player);
      this.renderRightHUD(player, currentWave, canvasWidth);
      this.renderTopCenter(player, currentZone, canvasWidth);
   }

   public void renderBossHealthBar(BossEnemy boss) {
      if (boss == null || !boss.isActive()) return;

      double canvasWidth = this.gameView.getWidth();
      double barWidth = canvasWidth * 0.6;
      double barHeight = 20;
      double barX = (canvasWidth - barWidth) / 2.0;
      double barY = 10;

      // Background
      this.gameView.drawBox(barX - 2, barY - 2, barWidth + 4, barHeight + 4, Color.TRANSPARENT, BOSS_BAR_OVERLAY);

      // Border
      this.gameView.drawBox(barX, barY, barWidth, barHeight, BOSS_BAR_RED, BOSS_BAR_BG);

      // Health fill
      double maxHp;
      if (boss instanceof BoardOfDirectors bod) {
         maxHp = bod.getSubBossMaxHp();
      } else {
         maxHp = boss.getHealthComponent().getMaxHealth();
      }
      double healthPercent = boss.getHealthComponent().getCurrentHealth() / maxHp;
      healthPercent = Math.max(0.0, Math.min(1.0, healthPercent));
      double fillWidth = barWidth * healthPercent;

      if (fillWidth > 0) {
         Color barColor = healthPercent > 0.5 ? BOSS_BAR_RED : (healthPercent > 0.25 ? Color.ORANGE : Color.YELLOW);
         this.gameView.drawBox(barX, barY, fillWidth, barHeight, Color.TRANSPARENT, barColor);
         // Highlight
         this.gameView.drawBox(barX, barY, fillWidth, barHeight / 2.0, Color.TRANSPARENT,
               Color.color(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), 0.3));
      }

      // Boss name
      String bossName = boss.getBossName();
      if (boss instanceof BoardOfDirectors bod) {
         bossName += " - " + bod.getSubBossName();
      }
      this.gameView.drawText(bossName, barX + barWidth / 2.0 - bossName.length() * 4, barY + barHeight + 16, BOSS_NAME_COLOR, 14);
   }

   private void renderLeftHUD(Player player) {
      int y = 20;
      String healthText = String.format("Energy: %.0f/%.0f", player.getHealthComponent().getCurrentHealth(), player.getHealthComponent().getMaxHealth());
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
         String invulnText = String.format("COFFEE BREAK: %.1fs", player.getInvulnerabilityTimer());
         this.gameView.drawText(invulnText, 10.0, y, Color.YELLOW, 14);
      }
   }

   private void renderExperienceBar(Player player, int x, int y) {
      int barWidth = 200;
      int barHeight = 10;
      double pulse = 0.3 + 0.2 * Math.sin(System.currentTimeMillis() * 0.005);
      Color glowColor = Color.color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), pulse);
      this.gameView.drawBox(x - 2, y - 2, barWidth + 4, barHeight + 4, Color.TRANSPARENT, glowColor);
      this.gameView.drawBox(x, y, barWidth, barHeight, ACCENT_COLOR, BAR_BG_COLOR);
      double progress = player.getExperience() / player.getExperienceThreshold();
      int filledWidth = (int)(barWidth * Math.min(progress, 1.0));
      if (filledWidth > 0) {
         Color barColor = Color.hsb(System.currentTimeMillis() * 0.1 % 360.0, 0.6, 1.0);
         this.gameView.drawBox(x, y, filledWidth, barHeight, Color.TRANSPARENT, barColor);
         Color brightGlow = Color.color(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), 0.5);
         this.gameView.drawBox(x, y, filledWidth, barHeight / 2.0, Color.TRANSPARENT, brightGlow);
      }
   }

   private void renderTopCenter(Player player, ZoneType currentZone, int canvasWidth) {
      double centerX = canvasWidth / 2.0;

      // Zone name (small, top-center)
      if (currentZone != null) {
         String zoneName = currentZone.getDisplayName();
         this.gameView.drawText(zoneName, centerX - zoneName.length() * 3.5, 18, TOP_CENTER_ZONE, 12);
      }

      // Weapon count
      int weaponCount = player.getInventory().getWeapons().size();
      String weaponText = "Weapons: " + weaponCount + "/6";
      this.gameView.drawText(weaponText, centerX - 50, 34, TOP_CENTER_WEAPON, 11);

      // Prominent XP bar (top center, wider)
      int barWidth = 300;
      int barHeight = 6;
      int barX = (int)(centerX - barWidth / 2.0);
      int barY = 40;
      double progress = player.getExperience() / player.getExperienceThreshold();
      progress = Math.min(progress, 1.0);
      this.gameView.drawBox(barX, barY, barWidth, barHeight, Color.TRANSPARENT, TOP_XP_BG);
      int filledWidth = (int)(barWidth * progress);
      if (filledWidth > 0) {
         this.gameView.drawBox(barX, barY, filledWidth, barHeight, Color.TRANSPARENT, TOP_XP_FILL);
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
