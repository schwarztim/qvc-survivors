package com.qvc.survivors.model.weapon;

import com.qvc.survivors.model.upgrade.StatModifier;

import java.util.HashMap;
import java.util.Map;

public class PassiveItem {
    private final String id;
    private final String name;
    private final String description;
    private int level;
    private final int maxLevel = 5;
    private final Map<StatModifier, Double> baseBoosts;

    public PassiveItem(String id, String name, String description, Map<StatModifier, Double> baseBoosts) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = 1;
        this.baseBoosts = new HashMap<>(baseBoosts);
    }

    public Map<StatModifier, Double> getEffectiveBoosts() {
        Map<StatModifier, Double> effective = new HashMap<>();
        for (Map.Entry<StatModifier, Double> entry : baseBoosts.entrySet()) {
            effective.put(entry.getKey(), entry.getValue() * level);
        }
        return effective;
    }

    public void levelUp() {
        if (level < maxLevel) {
            level++;
        }
    }

    public boolean isMaxLevel() {
        return level >= maxLevel;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getLevel() { return level; }
    public int getMaxLevel() { return maxLevel; }
    public Map<StatModifier, Double> getBaseBoosts() { return baseBoosts; }
}
