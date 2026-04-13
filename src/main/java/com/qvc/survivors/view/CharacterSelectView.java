package com.qvc.survivors.view;

import com.qvc.survivors.model.character.CharacterType;
import javafx.scene.paint.Color;

public class CharacterSelectView {
    private final GameView gameView;

    public CharacterSelectView(GameView gameView) {
        this.gameView = gameView;
    }

    public void render(int selectedIndex) {
        double w = gameView.getWidth();
        double h = gameView.getHeight();
        double centerX = w / 2.0;

        // Background overlay
        gameView.drawBox(0, 0, w, h, Color.TRANSPARENT, Color.rgb(10, 10, 20, 0.95));

        // Title
        gameView.drawText("SELECT YOUR HOST", centerX - 130, h * 0.10, Color.rgb(100, 200, 255), 28);
        gameView.drawText("A/D or Arrow Keys to browse, SPACE to confirm",
                centerX - 230, h * 0.16, Color.rgb(150, 170, 200), 14);

        CharacterType[] chars = CharacterType.values();
        double cardWidth = 200;
        double cardHeight = 320;
        double gap = 40;
        double totalWidth = chars.length * cardWidth + (chars.length - 1) * gap;
        double startX = centerX - totalWidth / 2.0;
        double cardY = h * 0.25;

        for (int i = 0; i < chars.length; i++) {
            CharacterType ch = chars[i];
            double cx = startX + i * (cardWidth + gap);
            boolean selected = (i == selectedIndex);

            // Card background
            Color borderColor = selected ? ch.getColor() : Color.rgb(60, 70, 90);
            Color bgColor = selected ? Color.rgb(30, 35, 50, 0.95) : Color.rgb(20, 22, 30, 0.8);
            gameView.drawBox(cx, cardY, cardWidth, cardHeight, borderColor, bgColor);

            if (selected) {
                // Selection glow
                gameView.drawBox(cx - 2, cardY - 2, cardWidth + 4, cardHeight + 4,
                        ch.getColor(), Color.TRANSPARENT);
            }

            // Character avatar (colored circle)
            double avatarCX = cx + cardWidth / 2.0;
            double avatarCY = cardY + 60;
            gameView.getGraphicsContext().setFill(ch.getColor().darker());
            gameView.getGraphicsContext().fillOval(avatarCX - 30, avatarCY - 30, 60, 60);
            gameView.getGraphicsContext().setStroke(ch.getColor());
            gameView.getGraphicsContext().setLineWidth(2.5);
            gameView.getGraphicsContext().strokeOval(avatarCX - 30, avatarCY - 30, 60, 60);
            // "Q" label
            gameView.drawText("Q", avatarCX - 6, avatarCY + 5, ch.getColor().brighter(), 18);

            // Name
            double nameX = cx + cardWidth / 2.0 - ch.getName().length() * 4.5;
            gameView.drawText(ch.getName(), nameX, cardY + 110, ch.getColor(), 16);

            // Description
            double descX = cx + 10;
            gameView.drawText(ch.getDescription(), descX, cardY + 140, Color.rgb(180, 190, 210), 11);

            // Stats
            double statY = cardY + 175;
            Color statLabel = Color.rgb(140, 160, 190);
            Color statVal = Color.rgb(200, 220, 255);

            gameView.drawText("HP:", descX, statY, statLabel, 12);
            gameView.drawText(formatMult(ch.getHealthMult()), descX + 90, statY, statColor(ch.getHealthMult()), 12);
            statY += 22;

            gameView.drawText("Speed:", descX, statY, statLabel, 12);
            gameView.drawText(formatMult(ch.getSpeedMult()), descX + 90, statY, statColor(ch.getSpeedMult()), 12);
            statY += 22;

            gameView.drawText("Damage:", descX, statY, statLabel, 12);
            gameView.drawText(formatMult(ch.getDamageMult()), descX + 90, statY, statColor(ch.getDamageMult()), 12);
            statY += 30;

            // Starting weapon
            gameView.drawText("Weapon:", descX, statY, Color.rgb(100, 200, 255), 12);
            gameView.drawText(weaponDisplayName(ch.getStartingWeaponId()), descX, statY + 18, statVal, 11);
        }
    }

    private String formatMult(double mult) {
        if (mult == 1.0) return "100%";
        return String.format("%.0f%%", mult * 100);
    }

    private Color statColor(double mult) {
        if (mult > 1.0) return Color.LIGHTGREEN;
        if (mult < 1.0) return Color.rgb(255, 150, 100);
        return Color.rgb(200, 220, 255);
    }

    private String weaponDisplayName(String id) {
        return switch (id) {
            case "package_launcher" -> "Package Launcher";
            case "shopping_cart_stampede" -> "Shopping Cart";
            case "credit_card_toss" -> "Credit Card Toss";
            default -> id;
        };
    }
}
