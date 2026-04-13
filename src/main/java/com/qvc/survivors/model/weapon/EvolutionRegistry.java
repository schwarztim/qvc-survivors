package com.qvc.survivors.model.weapon;

import java.util.ArrayList;
import java.util.List;

public class EvolutionRegistry {
    private static final List<EvolutionRecipe> RECIPES = new ArrayList<>();

    static {
        RECIPES.add(new EvolutionRecipe("package_launcher", "loyalty_rewards", "evo_mega_package"));
        RECIPES.add(new EvolutionRecipe("price_slash", "satisfaction_guarantee", "evo_clearance_storm"));
        RECIPES.add(new EvolutionRecipe("host_microphone", "studio_lighting", "evo_broadcast_nova"));
        RECIPES.add(new EvolutionRecipe("shopping_cart", "same_day_shipping", "evo_express_cart"));
        RECIPES.add(new EvolutionRecipe("credit_card_toss", "bulk_purchase", "evo_platinum_barrage"));
        RECIPES.add(new EvolutionRecipe("drone_swarm", "easy_pay", "evo_prime_fleet"));

        // New weapon evolution recipes
        RECIPES.add(new EvolutionRecipe("flash_sale_strike", "spinach_dip", "evo_mega_flash"));
        RECIPES.add(new EvolutionRecipe("receipt_roller", "bubble_wrap", "evo_endless_receipt"));
        RECIPES.add(new EvolutionRecipe("countdown_clock", "express_shipping", "evo_time_stop"));
        RECIPES.add(new EvolutionRecipe("spilled_coffee", "gift_wrapping", "evo_lava_zone"));
        RECIPES.add(new EvolutionRecipe("return_shield", "customer_reviews", "evo_platinum_shield"));
        RECIPES.add(new EvolutionRecipe("perfume_aura", "fragrance_sample", "evo_toxic_cloud"));
    }

    public static String getEvolution(String weaponId, String passiveId) {
        for (EvolutionRecipe recipe : RECIPES) {
            if (recipe.weaponId().equals(weaponId) && recipe.passiveId().equals(passiveId)) {
                return recipe.evolvedWeaponId();
            }
        }
        return null;
    }

    public static EvolutionRecipe checkEvolutions(PlayerInventory inventory) {
        for (EvolutionRecipe recipe : RECIPES) {
            Weapon weapon = inventory.getWeapon(recipe.weaponId());
            if (weapon != null && weapon.isMaxLevel() && inventory.hasPassive(recipe.passiveId())) {
                return recipe;
            }
        }
        return null;
    }

    public static List<EvolutionRecipe> getRecipes() {
        return RECIPES;
    }
}
