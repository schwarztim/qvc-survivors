package com.qvc.survivors.view;

import com.qvc.survivors.model.meta.MetaProgression;
import com.qvc.survivors.model.meta.MetaUpgradeType;
import javafx.scene.paint.Color;

public class MetaShopView {
   private static final Color OVERLAY_COLOR = Color.rgb(0, 0, 0, 0.85);
   private static final Color BOX_COLOR = Color.rgb(25, 25, 35);
   private static final Color BORDER_COLOR = Color.rgb(100, 200, 255);
   private static final Color BORDER_GLOW = Color.rgb(100, 200, 255, 0.3);
   private static final Color TEXT_COLOR = Color.rgb(220, 230, 255);
   private static final Color HIGHLIGHT_COLOR = Color.rgb(100, 255, 200);
   private static final Color GOLD_COLOR = Color.rgb(255, 215, 0);
   private static final Color SELECTED_GLOW = Color.rgb(255, 255, 100, 0.3);
   private final GameView gameView;

   public MetaShopView(GameView gameView) {
      this.gameView = gameView;
   }

   public void render(MetaProgression metaProgression, int selectedIndex) {
      double width = this.gameView.getWidth();
      double height = this.gameView.getHeight();
      this.gameView.drawBox(0.0, 0.0, width, height, Color.TRANSPARENT, OVERLAY_COLOR);
      Color titleColor = Color.hsb(System.currentTimeMillis() * 0.1 % 360.0, 0.6, 1.0);
      this.gameView.drawText("╔═══════════════════════╗", width / 2.0 - 200.0, 60.0, titleColor, 32);
      this.gameView.drawText("   PERMANENT UPGRADES   ", width / 2.0 - 200.0, 95.0, HIGHLIGHT_COLOR, 32);
      this.gameView.drawText("╚═══════════════════════╝", width / 2.0 - 200.0, 130.0, titleColor, 32);
      double moneyBoxX = width / 2.0 - 150.0;
      double moneyBoxY = 150.0;
      this.gameView.drawBox(moneyBoxX + 2.0, moneyBoxY + 2.0, 300.0, 40.0, Color.TRANSPARENT, BORDER_GLOW);
      this.gameView.drawBox(moneyBoxX, moneyBoxY, 300.0, 40.0, BORDER_COLOR, BOX_COLOR);
      this.gameView.drawText("Total Money: $" + metaProgression.getTotalMoney(), moneyBoxX + 70.0, moneyBoxY + 27.0, GOLD_COLOR, 20);
      double startY = 220.0;
      double itemHeight = 90.0;
      int itemsPerRow = 2;
      double columnWidth = (width - 100.0) / itemsPerRow;
      MetaUpgradeType[] upgrades = MetaUpgradeType.values();

      for (int i = 0; i < upgrades.length; i++) {
         int row = i / itemsPerRow;
         int col = i % itemsPerRow;
         double x = 50.0 + col * columnWidth;
         double y = startY + row * itemHeight;
         this.renderUpgradeItem(metaProgression, upgrades[i], x, y, i == selectedIndex);
      }

      double footerY = height - 70.0;
      double footerBoxX = 50.0;
      this.gameView.drawBox(footerBoxX + 2.0, footerY + 2.0, width - 104.0, 50.0, Color.TRANSPARENT, BORDER_GLOW);
      this.gameView.drawBox(footerBoxX, footerY, width - 100.0, 50.0, BORDER_COLOR, BOX_COLOR);
      this.gameView.drawText("WASD/Arrows: Navigate  |  ENTER: Purchase  |  ESC: Start Game", footerBoxX + 80.0, footerY + 22.0, TEXT_COLOR, 16);
      this.gameView
         .drawText(
            String.format(
               "Stats - Games: %d | Total Kills: %d | Best Wave: %d | Best Level: %d",
               metaProgression.getGamesPlayed(),
               metaProgression.getTotalKills(),
               metaProgression.getHighestWave(),
               metaProgression.getHighestLevel()
            ),
            footerBoxX + 40.0,
            footerY + 42.0,
            Color.rgb(150, 160, 200),
            12
         );
   }

   private void renderUpgradeItem(MetaProgression metaProgression, MetaUpgradeType upgrade, double x, double y, boolean selected) {
      int currentLevel = metaProgression.getUpgradeLevel(upgrade);
      int cost = upgrade.getCost(currentLevel);
      boolean canAfford = metaProgression.canAfford(upgrade);
      boolean maxed = currentLevel >= upgrade.getMaxLevel();
      double boxWidth = 380.0;
      double boxHeight = 75.0;
      if (selected) {
         double selectPulse = 0.3 + 0.3 * Math.sin(System.currentTimeMillis() * 0.008);
         Color selectGlow = Color.rgb(255, 255, 100, selectPulse);
         this.gameView.drawBox(x - 4.0, y - 4.0, boxWidth + 8.0, boxHeight + 8.0, Color.TRANSPARENT, selectGlow);
         this.gameView.drawBox(x - 2.0, y - 2.0, boxWidth + 4.0, boxHeight + 4.0, Color.TRANSPARENT, SELECTED_GLOW);
      }

      this.gameView.drawBox(x + 2.0, y + 2.0, boxWidth, boxHeight, Color.TRANSPARENT, BORDER_GLOW);
      this.gameView.drawBox(x, y, boxWidth, boxHeight, selected ? Color.rgb(255, 255, 100) : BORDER_COLOR, maxed ? Color.rgb(40, 40, 30) : BOX_COLOR);
      double textX = x + 15.0;
      double textY = y + 25.0;
      Color nameColor = maxed ? GOLD_COLOR : HIGHLIGHT_COLOR;
      this.gameView.drawText(upgrade.getDisplayName(), textX, textY, nameColor, 18);
      this.gameView.drawText(upgrade.getDescription(), textX, textY + 22.0, TEXT_COLOR, 12);
      String levelText = currentLevel + "/" + upgrade.getMaxLevel();
      Color levelColor = Color.rgb(100, 200, 255);
      double levelBarY = textY + 35.0;
      double levelBarWidth = 100.0;
      double levelBarHeight = 8.0;
      this.gameView.drawBox(textX, levelBarY, levelBarWidth, levelBarHeight, Color.rgb(60, 60, 80), Color.rgb(20, 20, 30));
      double fillWidth = levelBarWidth * currentLevel / upgrade.getMaxLevel();
      if (fillWidth > 0.0) {
         this.gameView.drawBox(textX, levelBarY, fillWidth, levelBarHeight, Color.TRANSPARENT, maxed ? GOLD_COLOR : levelColor);
      }

      this.gameView.drawText(levelText, textX + levelBarWidth + 10.0, levelBarY + 7.0, levelColor, 12);
      if (maxed) {
         this.gameView.drawText("✓ MAXED", textX + 260.0, textY + 42.0, GOLD_COLOR, 14);
      } else {
         Color costColor = canAfford ? HIGHLIGHT_COLOR : Color.rgb(255, 100, 100);
         this.gameView.drawText("Cost: $" + cost, textX + 240.0, textY + 42.0, costColor, 14);
      }
   }
}
