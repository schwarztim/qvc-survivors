package com.qvc.survivors.view;

import com.qvc.survivors.model.upgrade.Upgrade;
import com.qvc.survivors.model.upgrade.Upgrade.ChoiceType;
import java.util.List;
import javafx.scene.paint.Color;

public class LevelUpView {
   private static final Color OVERLAY_COLOR = Color.rgb(0, 0, 0, 0.85);
   private static final Color CARD_BG = Color.rgb(25, 28, 40);
   private static final Color CARD_BORDER = Color.rgb(80, 100, 140);
   private static final Color SELECTED_BORDER = Color.rgb(100, 255, 200);
   private static final Color SELECTED_GLOW = Color.rgb(100, 255, 200, 0.25);
   private static final Color TEXT_COLOR = Color.rgb(220, 230, 255);
   private static final Color DIM_TEXT = Color.rgb(120, 130, 160);
   private final GameView gameView;

   public LevelUpView(GameView gameView) {
      this.gameView = gameView;
   }

   public void render(List<Upgrade> upgrades) {
      render(upgrades, 0, 0, 0, 0);
   }

   public void render(List<Upgrade> upgrades, int selectedIndex, int rerolls, int skips, int banishes) {
      double cw = this.gameView.getWidth();
      double ch = this.gameView.getHeight();

      // Full-screen overlay
      this.gameView.drawBox(0, 0, cw, ch, Color.TRANSPARENT, OVERLAY_COLOR);

      // Title
      double titleY = ch * 0.12;
      Color titleColor = Color.hsb(System.currentTimeMillis() * 0.1 % 360.0, 0.5, 1.0);
      this.gameView.drawText("LEVEL UP!", cw / 2.0 - 70, titleY, titleColor, 32);
      this.gameView.drawText("Choose Your Upgrade", cw / 2.0 - 100, titleY + 40, TEXT_COLOR, 16);

      // Cards layout
      int count = Math.min(upgrades.size(), 4);
      double cardW = 180;
      double cardH = 220;
      double gap = 20;
      double totalW = count * cardW + (count - 1) * gap;
      double startX = (cw - totalW) / 2.0;
      double cardY = ch * 0.28;

      for (int i = 0; i < count; i++) {
         Upgrade upgrade = upgrades.get(i);
         double cardX = startX + i * (cardW + gap);
         boolean selected = (i == selectedIndex);

         // Selection glow
         if (selected) {
            double pulse = 0.7 + 0.3 * Math.sin(System.currentTimeMillis() * 0.006);
            Color glow = Color.rgb(100, 255, 200, 0.15 + pulse * 0.15);
            this.gameView.drawBox(cardX - 4, cardY - 4, cardW + 8, cardH + 8, Color.TRANSPARENT, glow);
            this.gameView.drawBox(cardX - 2, cardY - 2, cardW + 4, cardH + 4, Color.TRANSPARENT, SELECTED_GLOW);
         }

         // Card background
         Color border = selected ? SELECTED_BORDER : CARD_BORDER;
         Color bg = selected ? Color.rgb(30, 40, 50) : CARD_BG;
         this.gameView.drawBox(cardX, cardY, cardW, cardH, border, bg);

         // Type badge at top
         Color badgeColor = getBadgeColor(upgrade.getChoiceType());
         String badgeText = getBadgeText(upgrade.getChoiceType());
         this.gameView.drawBox(cardX + 10, cardY + 10, cardW - 20, 22, Color.TRANSPARENT, badgeColor.deriveColor(0, 1, 1, 0.3));
         this.gameView.drawText(badgeText, cardX + 15, cardY + 26, badgeColor, 11);

         // Number indicator
         Color numColor = selected ? Color.WHITE : DIM_TEXT;
         this.gameView.drawText("[" + (i + 1) + "]", cardX + cardW - 35, cardY + 27, numColor, 12);

         // Weapon/item name
         Color nameColor = selected ? Color.WHITE : TEXT_COLOR;
         String name = upgrade.getName();
         if (name.length() > 20) name = name.substring(0, 18) + "..";
         this.gameView.drawText(name, cardX + 12, cardY + 60, nameColor, 14);

         // Description (word-wrap manually, max 3 lines)
         String desc = upgrade.getDescription();
         int charsPerLine = 22;
         double descY = cardY + 85;
         for (int line = 0; line < 3 && !desc.isEmpty(); line++) {
            int end = Math.min(desc.length(), charsPerLine);
            // Try to break at space
            if (end < desc.length()) {
               int space = desc.lastIndexOf(' ', end);
               if (space > end / 2) end = space;
            }
            this.gameView.drawText(desc.substring(0, end).trim(), cardX + 12, descY, DIM_TEXT, 11);
            desc = desc.substring(end).trim();
            descY += 16;
         }

         // Selection arrow indicator
         if (selected) {
            double arrowY = cardY + cardH + 8;
            this.gameView.drawText("\u25B2", cardX + cardW / 2 - 5, arrowY + 12, SELECTED_BORDER, 14);
         }
      }

      // Footer: controls + meta actions
      double footerY = cardY + cardH + 35;
      this.gameView.drawText("1-" + count + " or D-pad + A to select", cw / 2.0 - 120, footerY, DIM_TEXT, 13);

      // Reroll/Skip/Banish buttons
      double metaY = footerY + 25;
      if (rerolls > 0) {
         this.gameView.drawText("[R] Reroll (" + rerolls + ")", cw / 2.0 - 160, metaY, Color.rgb(100, 200, 255), 12);
      }
      if (skips > 0) {
         this.gameView.drawText("[S] Skip (" + skips + ")", cw / 2.0 - 30, metaY, Color.rgb(255, 200, 100), 12);
      }
      if (banishes > 0) {
         this.gameView.drawText("[B] Banish (" + banishes + ")", cw / 2.0 + 80, metaY, Color.rgb(255, 100, 100), 12);
      }
   }

   private Color getBadgeColor(ChoiceType type) {
      return switch (type) {
         case NEW_WEAPON -> Color.rgb(100, 200, 255);
         case NEW_PASSIVE -> Color.rgb(200, 150, 255);
         case WEAPON_LEVELUP -> Color.rgb(100, 255, 200);
         case PASSIVE_LEVELUP -> Color.rgb(255, 200, 100);
         default -> Color.rgb(180, 180, 200);
      };
   }

   private String getBadgeText(ChoiceType type) {
      return switch (type) {
         case NEW_WEAPON -> "NEW WEAPON";
         case NEW_PASSIVE -> "NEW PASSIVE";
         case WEAPON_LEVELUP -> "WEAPON UPGRADE";
         case PASSIVE_LEVELUP -> "PASSIVE UPGRADE";
         default -> "UPGRADE";
      };
   }
}
