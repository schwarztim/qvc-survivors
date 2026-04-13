package com.qvc.survivors.service;

import com.qvc.survivors.model.upgrade.Upgrade;
import com.qvc.survivors.model.weapon.PassiveItem;
import com.qvc.survivors.model.weapon.PassiveItemType;
import com.qvc.survivors.model.weapon.PlayerInventory;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.impl.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class UpgradeManager {
    private static final int UPGRADE_OPTIONS_COUNT = 3;
    private static final Random RANDOM = new Random();

    private static final List<Supplier<Weapon>> WEAPON_POOL = List.of(
            PackageLauncher::new,
            PriceSlash::new,
            HostMicrophone::new,
            ShoppingCartStampede::new,
            CreditCardToss::new,
            DeliveryDroneSwarm::new,
            FlashSaleStrike::new,
            SpilledCoffeeZone::new,
            ReceiptRoller::new,
            CountdownClock::new,
            ThirtyDayReturnShield::new,
            QVCPerfumeAura::new
    );

    public List<Upgrade> generateUpgradeOptions(PlayerInventory inventory) {
        List<Upgrade> candidates = new ArrayList<>();

        // Weapon level-ups (70% weight -- add multiple entries)
        for (Weapon w : inventory.getWeapons()) {
            if (!w.isMaxLevel()) {
                Upgrade u = new Upgrade(
                        w.getName() + " Lv" + (w.getLevel() + 1),
                        w.getDescription() + " [Lv " + w.getLevel() + " -> " + (w.getLevel() + 1) + "]",
                        Upgrade.ChoiceType.WEAPON_LEVELUP,
                        w.getId()
                );
                candidates.add(u);
                candidates.add(u); // double weight
            }
        }

        // Passive level-ups (70% weight)
        for (PassiveItem p : inventory.getPassives()) {
            if (!p.isMaxLevel()) {
                Upgrade u = new Upgrade(
                        p.getName() + " Lv" + (p.getLevel() + 1),
                        p.getDescription() + " [Lv " + p.getLevel() + " -> " + (p.getLevel() + 1) + "]",
                        Upgrade.ChoiceType.PASSIVE_LEVELUP,
                        p.getId()
                );
                candidates.add(u);
                candidates.add(u); // double weight
            }
        }

        // New weapons (30% weight)
        if (!inventory.isWeaponsFull()) {
            for (Supplier<Weapon> factory : WEAPON_POOL) {
                Weapon sample = factory.get();
                if (!inventory.hasWeapon(sample.getId())) {
                    candidates.add(new Upgrade(
                            "NEW: " + sample.getName(),
                            sample.getDescription(),
                            Upgrade.ChoiceType.NEW_WEAPON,
                            sample.getId()
                    ));
                }
            }
        }

        // New passives (30% weight)
        if (!inventory.isPassivesFull()) {
            for (PassiveItemType type : PassiveItemType.values()) {
                if (!inventory.hasPassive(type.getId())) {
                    candidates.add(new Upgrade(
                            "NEW: " + type.getItemName(),
                            type.getDescription(),
                            Upgrade.ChoiceType.NEW_PASSIVE,
                            type.getId()
                    ));
                }
            }
        }

        Collections.shuffle(candidates, RANDOM);

        // Deduplicate by targetId + choiceType
        List<Upgrade> selected = new ArrayList<>();
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (Upgrade u : candidates) {
            String key = u.getChoiceType() + ":" + u.getTargetId();
            if (seen.add(key)) {
                selected.add(u);
            }
            if (selected.size() >= UPGRADE_OPTIONS_COUNT) break;
        }

        return selected;
    }

    public Weapon createWeaponById(String id) {
        // Check evolved weapons first
        Weapon evolved = createEvolvedWeaponById(id);
        if (evolved != null) return evolved;

        for (Supplier<Weapon> factory : WEAPON_POOL) {
            Weapon w = factory.get();
            if (w.getId().equals(id)) return w;
        }
        return null;
    }

    private Weapon createEvolvedWeaponById(String id) {
        return switch (id) {
            case "evo_mega_package" -> new PrimeDeliveryCannon();
            case "evo_clearance_storm" -> new ClearanceAnnihilation();
            case "evo_broadcast_nova" -> new LiveBroadcastShockwave();
            case "evo_express_cart" -> new BlackFridayStampede();
            case "evo_platinum_barrage" -> new PlatinumCardStorm();
            case "evo_prime_fleet" -> new DroneArmada();
            default -> null;
        };
    }

    public PassiveItem createPassiveById(String id) {
        for (PassiveItemType type : PassiveItemType.values()) {
            if (type.getId().equals(id)) return type.createInstance();
        }
        return null;
    }

    public Weapon createNewWeaponForPlayer(PlayerInventory inventory) {
        List<Supplier<Weapon>> available = new ArrayList<>();
        for (Supplier<Weapon> factory : WEAPON_POOL) {
            Weapon sample = factory.get();
            if (!inventory.hasWeapon(sample.getId())) {
                available.add(factory);
            }
        }
        if (available.isEmpty()) return null;
        return available.get(RANDOM.nextInt(available.size())).get();
    }

    // Legacy method for backward compatibility
    public List<Upgrade> generateUpgradeOptions() {
        return List.of(
                new Upgrade("Package Launcher +1", "Upgrade your launcher", Upgrade.ChoiceType.WEAPON_LEVELUP, "package_launcher"),
                new Upgrade("New Weapon", "Get a random weapon", Upgrade.ChoiceType.NEW_WEAPON, "price_slash"),
                new Upgrade("New Passive", "Get a random passive", Upgrade.ChoiceType.NEW_PASSIVE, "easy_pay")
        );
    }
}
