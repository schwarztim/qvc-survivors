package com.qvc.survivors.view;

import javafx.scene.paint.Color;

public class TutorialView {
   private static final Color OVERLAY_COLOR = Color.rgb(0, 0, 0, 0.85);
   private static final Color BOX_COLOR = Color.rgb(25, 25, 35);
   private static final Color BORDER_COLOR = Color.rgb(100, 200, 255);
   private static final Color BORDER_GLOW = Color.rgb(100, 200, 255, 0.3);
   private static final Color TEXT_COLOR = Color.rgb(220, 230, 255);
   private static final Color HIGHLIGHT_COLOR = Color.rgb(100, 255, 200);
   private final GameView gameView;

   public TutorialView(GameView gameView) {
      this.gameView = gameView;
   }

   public void render() {
      double width = this.gameView.getWidth();
      double height = this.gameView.getHeight();
      this.gameView.drawBox(0.0, 0.0, width, height, Color.TRANSPARENT, OVERLAY_COLOR);
      Color titleColor = Color.hsb(System.currentTimeMillis() * 0.1 % 360.0, 0.6, 1.0);
      double centerX = width / 2.0;
      this.gameView.drawText("╔═══════════════╗", centerX - 120.0, 40.0, titleColor, 24);
      this.gameView.drawText(" HOW TO PLAY ", centerX - 95.0, 65.0, HIGHLIGHT_COLOR, 24);
      this.gameView.drawText("╚═══════════════╝", centerX - 120.0, 90.0, titleColor, 24);
      double leftCol = 80.0;
      double rightCol = width / 2.0 + 50.0;
      double startY = 120.0;
      int tileSize = this.gameView.getTileSize();
      double visualX = 5.0;
      this.renderSection("CONTROLS", leftCol, startY, new String[]{"WASD / Arrows - Move", "1/2/3 - Select upgrade", "P / ESC - Pause game"});
      this.renderSection(
         "OBJECTIVE", rightCol, startY, new String[]{"Survive waves of customers", "Earn tips to unlock upgrades", "Level up for permanent bonuses"}
      );
      startY += 100.0;
      this.gameView.drawText("═ YOUR CHARACTER ═", leftCol, startY, HIGHLIGHT_COLOR, 14);
      startY += 20.0;
      this.gameView.drawPlayer(visualX, (startY - tileSize / 3.0) / tileSize, Color.CYAN, 0.7);
      this.gameView.drawText("QVC Delivery Driver", leftCol + 60.0, startY + 3.0, Color.CYAN, 12);
      startY += 30.0;
      this.gameView.drawText("═ CUSTOMERS ═", leftCol, startY, HIGHLIGHT_COLOR, 14);
      startY += 20.0;
      this.gameView.drawEnemy(visualX, (startY - tileSize / 3.0) / tileSize, Color.ORANGE, 0.5, false);
      this.gameView.drawText("Regular (common)", leftCol + 60.0, startY + 3.0, Color.ORANGE, 12);
      startY += 20.0;
      this.gameView.drawEnemy(visualX, (startY - tileSize / 3.0) / tileSize, Color.RED, 0.9, true);
      this.gameView.drawText("VIP (rare, fast, crown)", leftCol + 60.0, startY + 3.0, Color.RED, 12);
      startY = 220.0;
      this.gameView.drawText("═ WEAPONS ═", rightCol, startY, HIGHLIGHT_COLOR, 14);
      startY += 20.0;
      this.gameView.drawPackage((rightCol - 50.0) / tileSize, (startY - tileSize / 3.0) / tileSize, Color.LIGHTBLUE, 0.6);
      this.gameView.drawText("Packages (auto-fire)", rightCol + 10.0, startY + 3.0, TEXT_COLOR, 12);
      startY += 20.0;
      this.gameView.drawDrone((rightCol - 50.0) / tileSize, (startY - tileSize / 3.0) / tileSize, Color.LIGHTGREEN, 0.7);
      this.gameView.drawText("Drones (orbit you)", rightCol + 10.0, startY + 3.0, TEXT_COLOR, 12);
      startY += 30.0;
      this.gameView.drawText("═ COLLECTIBLES ═", rightCol, startY, HIGHLIGHT_COLOR, 14);
      startY += 20.0;
      this.gameView.drawCollectible((rightCol - 50.0) / tileSize, (startY - 5.0) / tileSize, Color.YELLOW, 0.8, false, false);
      this.gameView.drawText("Tips (XP)", rightCol + 10.0, startY + 3.0, Color.GOLD, 12);
      startY += 20.0;
      this.gameView.drawCollectible((rightCol - 50.0) / tileSize, (startY - 5.0) / tileSize, Color.GOLD, 0.8, false, true);
      this.gameView.drawText("Bonus (VIP drops)", rightCol + 10.0, startY + 3.0, Color.GOLD, 12);
      startY += 20.0;
      this.gameView.drawCollectible((rightCol - 50.0) / tileSize, (startY - 5.0) / tileSize, Color.LIGHTGREEN, 0.8, true, false);
      this.gameView.drawText("Energy Drink (HP)", rightCol + 10.0, startY + 3.0, Color.LIGHTGREEN, 12);
      double bottomBoxY = height - 140.0;
      double boxWidth = width - 160.0;
      double boxX = 80.0;
      this.gameView.drawBox(boxX + 2.0, bottomBoxY + 2.0, boxWidth, 90.0, Color.TRANSPARENT, BORDER_GLOW);
      this.gameView.drawBox(boxX, bottomBoxY, boxWidth, 90.0, BORDER_COLOR, BOX_COLOR);
      this.gameView.drawText("PROGRESSION", boxX + 20.0, bottomBoxY + 25.0, HIGHLIGHT_COLOR, 14);
      this.gameView
         .drawText("• Level up during runs to choose temporary upgrades (fire rate, damage, drones, etc.)", boxX + 20.0, bottomBoxY + 45.0, TEXT_COLOR, 12);
      this.gameView.drawText("• After game over, spend earned money in the Meta Shop for permanent bonuses!", boxX + 20.0, bottomBoxY + 62.0, TEXT_COLOR, 12);
      this.gameView.drawText("• Build your ultimate delivery empire through multiple runs!", boxX + 20.0, bottomBoxY + 79.0, TEXT_COLOR, 12);
      double footerY = height - 35.0;
      Color footerPulse = Color.hsb(System.currentTimeMillis() * 0.15 % 360.0, 0.7, 1.0);
      this.gameView.drawText("Press SPACE to begin your delivery journey!", centerX - 180.0, footerY, footerPulse, 16);
      this.gameView.drawText("Made entirely by AI • Prompt: Mr. AI • Code: Claude Sonnet 4", centerX - 200.0, height - 10.0, Color.rgb(150, 160, 200), 10);
   }

   private void renderSection(String title, double x, double y, String[] items) {
      this.gameView.drawText("═ " + title + " ═", x, y, HIGHLIGHT_COLOR, 14);
      y += 20.0;

      for (String item : items) {
         this.gameView.drawText("• " + item, x, y, TEXT_COLOR, 12);
         y += 18.0;
      }
   }
}
