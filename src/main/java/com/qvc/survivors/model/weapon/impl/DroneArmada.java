package com.qvc.survivors.model.weapon.impl;

import com.qvc.survivors.model.entity.Drone;
import com.qvc.survivors.model.entity.Enemy;
import com.qvc.survivors.model.entity.Projectile;
import com.qvc.survivors.model.weapon.Weapon;
import com.qvc.survivors.model.weapon.WeaponType;
import com.qvc.survivors.service.EntityPoolManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DroneArmada extends Weapon {
    private double damage = 4.5;
    private int droneCount = 8;
    private final List<Drone> drones = new ArrayList<>();

    public DroneArmada() {
        super("evo_prime_fleet", "Drone Armada",
                "8 orbital drones with larger orbit and mini-projectile fire",
                WeaponType.ORBITAL, 0.0);
        this.maxLevel = 1;
        this.level = 1;
    }

    @Override
    public List<Projectile> fire(double playerX, double playerY, double facingAngle,
                                  List<Enemy> enemies, EntityPoolManager pool) {
        return Collections.emptyList();
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        for (Drone drone : drones) {
            drone.update(deltaTime);
        }
    }

    public void updateDronePositions(double playerX, double playerY) {
        ensureDroneCount(playerX, playerY);
        for (Drone drone : drones) {
            drone.updatePosition(playerX, playerY);
        }
    }

    private void ensureDroneCount(double playerX, double playerY) {
        while (drones.size() < droneCount) {
            double orbitRadius = 7.0;
            double orbitOffset = drones.size() * 2.0 * Math.PI / droneCount;
            drones.add(new Drone(playerX, playerY, damage, orbitRadius, orbitOffset));
        }
        for (Drone drone : drones) {
            drone.getDamageComponent().setDamage(damage);
        }
    }

    @Override
    public void onLevelUp() {
        // Evolved weapons don't level up further
    }

    public List<Drone> getDrones() {
        return Collections.unmodifiableList(drones);
    }

    public double getDamage() { return damage; }
    public int getDroneCount() { return droneCount; }
}
