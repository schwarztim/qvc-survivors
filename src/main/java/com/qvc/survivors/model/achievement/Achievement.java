package com.qvc.survivors.model.achievement;

public enum Achievement {
    FIRST_KILL("First Sale", "Satisfy your first customer"),
    HUNDRED_KILLS("Century Club", "Satisfy 100 customers in one run"),
    THOUSAND_KILLS("Black Friday Survivor", "Satisfy 1000 customers in one run"),
    EVOLVE_WEAPON("Product Upgrade", "Evolve your first weapon"),
    REACH_WAVE_10("Overtime", "Survive to wave 10"),
    REACH_WAVE_20("Double Shift", "Survive to wave 20"),
    BEAT_FIRST_BOSS("Management Material", "Defeat your first boss"),
    BEAT_ALL_BOSSES("Board Approved", "Defeat all zone bosses"),
    MAX_WEAPONS("Fully Stocked", "Have 6 weapons at once"),
    SURVIVE_15_MIN("Quarter Hour Hero", "Survive 15 minutes"),
    VISIT_ALL_ZONES("World Tour", "Visit all 5 zones");

    private final String displayName;
    private final String description;

    Achievement(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
