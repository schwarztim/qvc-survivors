package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.entity.ShockwaveEffect;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.List;

public class FlashSaleStrike extends Weapon {
    private double damage = 1.5;
    private int strikeCount = 2;
    private double aoeRadius = 3.0;
    private final List<ShockwaveEffect> activeStrikes = new ArrayList<>();

    public FlashSaleStrike() {
        super("flash_sale_strike", "Flash Sale Strike",
                "Random lightning bolts strike enemies on screen",
                WeaponType.AREA, 2.5);
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        activeStrikes.clear();
        List<Enemy> activeEnemies = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isActive()) activeEnemies.add(e);
        }
        if (activeEnemies.isEmpty()) {
            resetCooldown();
            return new ArrayList<>();
        }
        for (int i = 0; i < strikeCount && !activeEnemies.isEmpty(); i++) {
            Enemy target = activeEnemies.get((int)(Math.random() * activeEnemies.size()));
            ShockwaveEffect strike = new ShockwaveEffect(target.getX(), target.getY(),
                    aoeRadius * 15.0, damage, javafx.scene.paint.Color.rgb(255, 240, 50));
            activeStrikes.add(strike);
        }
        resetCooldown();
        return new ArrayList<>();
    }

    @Override
    public void onLevelUp() {
        switch (level) {
            case 2 -> { damage = 2.0; strikeCount = 3; baseCooldown = 2.2; }
            case 3 -> { damage = 2.5; strikeCount = 4; aoeRadius = 3.5; baseCooldown = 2.0; }
            case 4 -> { damage = 3.0; strikeCount = 5; baseCooldown = 1.8; }
            case 5 -> { damage = 3.5; strikeCount = 6; aoeRadius = 4.0; baseCooldown = 1.5; }
        }
    }

    public List<ShockwaveEffect> getActiveStrikes() { return activeStrikes; }
    public void clearStrikes() { activeStrikes.clear(); }
    public double getDamage() { return damage; }
    public int getStrikeCount() { return strikeCount; }
}
