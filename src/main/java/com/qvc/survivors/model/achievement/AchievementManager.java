package com.qvc.survivors.model.achievement;

import com.qvc.survivors.model.entity.Player;
import com.qvc.survivors.model.meta.MetaProgression;
import com.qvc.survivors.world.ZoneType;

import java.util.EnumSet;
import java.util.Set;

public class AchievementManager {
    private final Set<Achievement> sessionUnlocked = EnumSet.noneOf(Achievement.class);
    private final Set<ZoneType> visitedZones = EnumSet.noneOf(ZoneType.class);
    private int bossesDefeated;
    private Achievement pendingBanner;
    private double bannerTimer;

    public void check(Player player, int wave, ZoneType currentZone, MetaProgression meta) {
        int kills = player.getCustomersSatisfied();
        double time = player.getSurvivalTime();
        int weaponCount = player.getInventory().getWeapons().size();

        if (currentZone != null) {
            visitedZones.add(currentZone);
        }

        Set<String> persisted = meta.getUnlockedAchievements();

        tryUnlock(Achievement.FIRST_KILL, kills >= 1, persisted);
        tryUnlock(Achievement.HUNDRED_KILLS, kills >= 100, persisted);
        tryUnlock(Achievement.THOUSAND_KILLS, kills >= 1000, persisted);
        tryUnlock(Achievement.REACH_WAVE_10, wave >= 10, persisted);
        tryUnlock(Achievement.REACH_WAVE_20, wave >= 20, persisted);
        tryUnlock(Achievement.MAX_WEAPONS, weaponCount >= 6, persisted);
        tryUnlock(Achievement.SURVIVE_15_MIN, time >= 900.0, persisted);
        tryUnlock(Achievement.VISIT_ALL_ZONES, visitedZones.size() >= 5, persisted);
        tryUnlock(Achievement.BEAT_ALL_BOSSES, bossesDefeated >= 5, persisted);
    }

    public void onBossDefeated(MetaProgression meta) {
        bossesDefeated++;
        Set<String> persisted = meta.getUnlockedAchievements();
        tryUnlock(Achievement.BEAT_FIRST_BOSS, true, persisted);
    }

    public void onWeaponEvolved(MetaProgression meta) {
        Set<String> persisted = meta.getUnlockedAchievements();
        tryUnlock(Achievement.EVOLVE_WEAPON, true, persisted);
    }

    private void tryUnlock(Achievement achievement, boolean condition, Set<String> persisted) {
        if (!condition) return;
        if (persisted.contains(achievement.name())) return;
        if (sessionUnlocked.contains(achievement)) return;

        sessionUnlocked.add(achievement);
        persisted.add(achievement.name());
        pendingBanner = achievement;
        bannerTimer = 3.0;
    }

    public void updateBanner(double deltaTime) {
        if (bannerTimer > 0) {
            bannerTimer -= deltaTime;
            if (bannerTimer <= 0) {
                pendingBanner = null;
            }
        }
    }

    public Achievement getPendingBanner() { return pendingBanner; }
    public double getBannerTimer() { return bannerTimer; }
    public Set<Achievement> getSessionUnlocked() { return sessionUnlocked; }
}
