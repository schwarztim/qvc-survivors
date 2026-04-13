package com.qvc.survivors.view;

import com.qvc.survivors.model.upgrade.Upgrade;
import java.util.List;
import javafx.scene.paint.Color;

public class LevelUpView {
   private static final Color OVERLAY_COLOR = Color.rgb(0, 0, 0, 0.8);
   private static final Color BOX_COLOR = Color.rgb(25, 25, 35);
   private static final Color BORDER_COLOR = Color.rgb(100, 200, 255);
   private static final Color BORDER_GLOW = Color.rgb(100, 200, 255, 0.3);
   private static final Color TEXT_COLOR = Color.rgb(220, 230, 255);
   private static final Color HIGHLIGHT_COLOR = Color.rgb(100, 255, 200);
   private static final Color OPTION_COLOR = Color.YELLOW;
   private final GameView gameView;

   public LevelUpView(GameView gameView) {
      this.gameView = gameView;
   }

   public void render(List<Upgrade> upgrades) {
      double canvasWidth = this.gameView.getWidth();
      double canvasHeight = this.gameView.getHeight();
      this.gameView.drawBox(0.0, 0.0, canvasWidth, canvasHeight, Color.TRANSPARENT, OVERLAY_COLOR);
      double boxWidth = 550.0;
      double boxHeight = 400.0;
      double boxX = (canvasWidth - boxWidth) / 2.0;
      double boxY = (canvasHeight - boxHeight) / 2.0;
      double pulse = 0.5 + 0.5 * Math.sin(System.currentTimeMillis() * 0.003);
      Color glowColor = Color.rgb(100, 200, 255, 0.2 + pulse * 0.3);
      this.gameView.drawBox(boxX + 6.0, boxY + 6.0, boxWidth, boxHeight, Color.TRANSPARENT, glowColor);
      this.gameView.drawBox(boxX + 4.0, boxY + 4.0, boxWidth, boxHeight, Color.TRANSPARENT, BORDER_GLOW);
      this.gameView.drawBox(boxX + 2.0, boxY + 2.0, boxWidth, boxHeight, Color.TRANSPARENT, BORDER_GLOW);
      this.gameView.drawBox(boxX, boxY, boxWidth, boxHeight, BORDER_COLOR, BOX_COLOR);
      double textX = boxX + 40.0;
      double textY = boxY + 60.0;
      Color titleColor = Color.hsb(System.currentTimeMillis() * 0.1 % 360.0, 0.6, 1.0);
      this.gameView.drawText("╔═══ LEVEL UP! ═══╗", textX + 100.0, textY, titleColor, 24);
      textY += 50.0;
      this.gameView.drawText("Choose Your Upgrade:", textX, textY, TEXT_COLOR, 18);
      textY += 50.0;

      for (int i = 0; i < upgrades.size(); i++) {
         Upgrade upgrade = upgrades.get(i);
         double optionY = boxY + 160.0 + i * 90;
         double optionX = boxX + 30.0;
         double hoverPulse = 0.3 + 0.2 * Math.sin(System.currentTimeMillis() * 0.005 + i);
         Color optionGlow = Color.rgb(100, 200, 255, hoverPulse);
         this.gameView.drawBox(optionX + 2.0, optionY - 23.0, boxWidth - 64.0, 66.0, Color.TRANSPARENT, optionGlow);
         this.gameView.drawBox(optionX, optionY - 25.0, boxWidth - 60.0, 70.0, BORDER_COLOR, Color.rgb(30, 35, 45));
         Color numberColor = Color.hsb((System.currentTimeMillis() * 0.2 + i * 60) % 360.0, 0.8, 1.0);
         this.gameView.drawText(String.format("[%d]", i + 1), optionX + 15.0, optionY, numberColor, 20);
         this.gameView.drawText(upgrade.getName(), optionX + 55.0, optionY, HIGHLIGHT_COLOR, 18);
         this.gameView.drawText(upgrade.getDescription(), optionX + 55.0, optionY + 25.0, TEXT_COLOR, 14);
      }

      double footerY = boxY + boxHeight - 20.0;
      this.gameView.drawText("Press 1, 2, or 3 to select", boxX + 160.0, footerY, Color.rgb(150, 160, 200), 14);
   }
}
