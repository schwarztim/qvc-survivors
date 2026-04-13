package com.qvc.survivors.model.weapon;

import com.qvc.survivors.model.upgrade.StatModifier;

import java.util.Map;

public enum PassiveItemType {
    EASY_PAY_PLAN("easy_pay", "Easy Pay Plan", "+10% Fire Rate per level",
            Map.of(StatModifier.FIRE_RATE, 0.10)),

    LOYALTY_REWARDS("loyalty_rewards", "Loyalty Rewards Card", "+8% Package Damage per level",
            Map.of(StatModifier.PACKAGE_DAMAGE, 0.08)),

    EXTENDED_WARRANTY("extended_warranty", "Extended Warranty", "+15 Max Health per level",
            Map.of(StatModifier.MAX_HEALTH, 15.0)),

    SAME_DAY_SHIPPING("same_day_shipping", "Same-Day Shipping", "+12% Movement Speed per level",
            Map.of(StatModifier.MOVEMENT_SPEED, 0.12)),

    STUDIO_LIGHTING("studio_lighting", "Studio Lighting Rig", "+15% AoE Radius per level",
            Map.of(StatModifier.AOE_RADIUS, 0.15)),

    REWARDS_POINTS("rewards_points", "Rewards Points", "+10% XP, +5% Money per level",
            Map.of(StatModifier.XP_BONUS, 0.10, StatModifier.MONEY_BONUS, 0.05)),

    FREE_RETURNS("free_returns", "Free Returns Policy", "+6% Lifesteal per level",
            Map.of(StatModifier.LIFESTEAL, 0.06)),

    SATISFACTION_GUARANTEE("satisfaction_guarantee", "Satisfaction Guarantee", "+5% Crit, +20% Crit Dmg per level",
            Map.of(StatModifier.CRITICAL_CHANCE, 0.05, StatModifier.CRIT_DAMAGE, 0.20)),

    BULK_PURCHASE("bulk_purchase", "Bulk Purchase Discount", "+1 Projectile Count per level",
            Map.of(StatModifier.PROJECTILE_COUNT, 1.0)),

    VIP_MEMBERSHIP("vip_membership", "VIP Membership Lanyard", "+20% Pickup Range, -4% Damage Taken per level",
            Map.of(StatModifier.PICKUP_RANGE, 0.60, StatModifier.DAMAGE_REDUCTION, 0.04)),

    SPINACH_DIP("spinach_dip", "Spinach Dip", "+10% Damage per level",
            Map.of(StatModifier.PACKAGE_DAMAGE, 0.10)),

    BUBBLE_WRAP("bubble_wrap", "Bubble Wrap", "+1 Armor per level",
            Map.of(StatModifier.ARMOR, 1.0)),

    LOYALTY_POINTS("loyalty_points", "Loyalty Points", "+8% XP Gain per level",
            Map.of(StatModifier.XP_BONUS, 0.08)),

    EXPRESS_SHIPPING("express_shipping", "Express Shipping", "+10% Projectile Speed per level",
            Map.of(StatModifier.PROJECTILE_SPEED, 0.10)),

    GIFT_WRAPPING("gift_wrapping", "Gift Wrapping", "+10% Effect Duration per level",
            Map.of(StatModifier.EFFECT_DURATION, 0.10)),

    CUSTOMER_REVIEWS("customer_reviews", "Customer Reviews", "+10% Luck per level",
            Map.of(StatModifier.LUCK, 0.10)),

    FRAGRANCE_SAMPLE("fragrance_sample", "Fragrance Sample", "+10% Area per level",
            Map.of(StatModifier.AOE_RADIUS, 0.10));

    private final String id;
    private final String name;
    private final String description;
    private final Map<StatModifier, Double> baseBoosts;

    PassiveItemType(String id, String name, String description, Map<StatModifier, Double> baseBoosts) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.baseBoosts = baseBoosts;
    }

    public PassiveItem createInstance() {
        return new PassiveItem(id, name, description, baseBoosts);
    }

    public String getId() { return id; }
    public String getItemName() { return name; }
    public String getDescription() { return description; }
    public Map<StatModifier, Double> getBaseBoosts() { return baseBoosts; }
}
