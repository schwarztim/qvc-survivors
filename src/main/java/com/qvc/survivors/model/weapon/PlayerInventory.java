package com.qvc.survivors.model.weapon;

import com.qvc.survivors.model.upgrade.StatModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerInventory {
    private static final int MAX_WEAPONS = 6;
    private static final int MAX_PASSIVES = 6;

    private final List<Weapon> weapons = new ArrayList<>();
    private final List<PassiveItem> passives = new ArrayList<>();

    public boolean addWeapon(Weapon weapon) {
        if (weapons.size() >= MAX_WEAPONS) return false;
        weapons.add(weapon);
        return true;
    }

    public boolean addPassive(PassiveItem passive) {
        if (passives.size() >= MAX_PASSIVES) return false;
        passives.add(passive);
        return true;
    }

    public boolean hasWeapon(String id) {
        return weapons.stream().anyMatch(w -> w.getId().equals(id));
    }

    public boolean hasPassive(String id) {
        return passives.stream().anyMatch(p -> p.getId().equals(id));
    }

    public Weapon getWeapon(String id) {
        return weapons.stream().filter(w -> w.getId().equals(id)).findFirst().orElse(null);
    }

    public PassiveItem getPassive(String id) {
        return passives.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    public boolean isWeaponsFull() {
        return weapons.size() >= MAX_WEAPONS;
    }

    public boolean isPassivesFull() {
        return passives.size() >= MAX_PASSIVES;
    }

    public void clearWeapons() {
        weapons.clear();
    }

    public List<Weapon> getWeapons() {
        return Collections.unmodifiableList(weapons);
    }

    public List<PassiveItem> getPassives() {
        return Collections.unmodifiableList(passives);
    }

    public double getTotalStatBoost(StatModifier modifier) {
        double total = 0.0;
        for (PassiveItem passive : passives) {
            Double boost = passive.getEffectiveBoosts().get(modifier);
            if (boost != null) {
                total += boost;
            }
        }
        return total;
    }
}
