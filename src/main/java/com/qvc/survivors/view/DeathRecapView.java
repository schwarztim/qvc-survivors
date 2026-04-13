package com.qvc.survivors.view;

import com.qvc.survivors.model.entity.Player;
import com.qvc.survivors.model.weapon.PassiveItem;
import com.qvc.survivors.model.weapon.PlayerInventory;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.world.ZoneType;
import javafx.scene.paint.Color;

public class DeathRecapView {
    private final GameView gameView;

    public DeathRecapView(GameView gameView) {
        this.gameView = gameView;
    }

    public void render(Player player, int wave, ZoneType zone, PlayerInventory inventory) {
        double w = gameView.getWidth();
        double h = gameView.getHeight();
        double centerX = w / 2.0;

        // Dark overlay
        gameView.drawBox(0, 0, w, h, Color.TRANSPARENT, Color.rgb(0, 0, 0, 0.88));

        // Title
        gameView.drawText("SHIFT ENDED", centerX - 85, h * 0.12, Color.rgb(255, 100, 100), 32);

        // Divider
        double lineY = h * 0.17;
        gameView.drawBox(centerX - 180, lineY, 360, 2, Color.TRANSPARENT, Color.rgb(255, 100, 100, 0.5));

        // Stats
        double col1X = centerX - 170;
        double col2X = centerX + 20;
        double y = h * 0.22;
        double lineH = 28;
        Color label = Color.rgb(150, 180, 220);
        Color value = Color.rgb(220, 240, 255);

        int minutes = (int) player.getSurvivalTime() / 60;
        int seconds = (int) player.getSurvivalTime() % 60;

        gameView.drawText("Shift Duration:", col1X, y, label, 16);
        gameView.drawText(String.format("%02d:%02d", minutes, seconds), col2X, y, value, 16);
        y += lineH;

        gameView.drawText("Wave Reached:", col1X, y, label, 16);
        gameView.drawText(String.valueOf(wave), col2X, y, value, 16);
        y += lineH;

        gameView.drawText("Customers Satisfied:", col1X, y, label, 16);
        gameView.drawText(String.valueOf(player.getCustomersSatisfied()), col2X, y, value, 16);
        y += lineH;

        gameView.drawText("Total Tips:", col1X, y, label, 16);
        gameView.drawText("$" + player.getMoney(), col2X, y, Color.YELLOW, 16);
        y += lineH;

        gameView.drawText("Level Reached:", col1X, y, label, 16);
        gameView.drawText(String.valueOf(player.getLevel()), col2X, y, value, 16);
        y += lineH;

        String zoneName = zone != null ? zone.getDisplayName() : "The Void";
        gameView.drawText("Zone:", col1X, y, label, 16);
        gameView.drawText(zoneName, col2X, y, value, 16);
        y += lineH + 10;

        // Weapons at death
        gameView.drawText("-- Weapons --", centerX - 70, y, Color.rgb(100, 200, 255), 16);
        y += lineH;
        for (Weapon weapon : inventory.getWeapons()) {
            String weaponText = weapon.getName() + " Lv." + weapon.getLevel() + "/" + weapon.getMaxLevel();
            gameView.drawText(weaponText, col1X, y, Color.rgb(180, 220, 255), 14);
            y += 22;
        }

        // Passive items at death
        if (!inventory.getPassives().isEmpty()) {
            y += 5;
            gameView.drawText("-- Passives --", centerX - 75, y, Color.rgb(100, 200, 255), 16);
            y += lineH;
            for (PassiveItem passive : inventory.getPassives()) {
                String passiveText = passive.getName() + " Lv." + passive.getLevel() + "/" + passive.getMaxLevel();
                gameView.drawText(passiveText, col1X, y, Color.rgb(180, 220, 200), 14);
                y += 22;
            }
        }

        // Actions
        double actionsY = Math.max(y + 20, h * 0.85);
        gameView.drawText("Press SPACE for Meta Shop", centerX - 130, actionsY, Color.rgb(255, 255, 100), 16);
        gameView.drawText("ESC to quit", centerX - 50, actionsY + 25, Color.rgb(150, 150, 150), 14);
    }
}
