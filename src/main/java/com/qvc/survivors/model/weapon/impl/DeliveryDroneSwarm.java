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

public class DeliveryDroneSwarm extends Weapon {
    private double damage = 1.0;
    private int droneCount = 1;
    private final List<Drone> drones = new ArrayList<>();

    public DeliveryDroneSwarm() {
        super("drone_swarm", "Delivery Drone Swarm",
                "Orbital drones that damage enemies on contact",
                WeaponType.ORBITAL, 0.0);
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
            double orbitRadius = 5.0;
            double orbitOffset = drones.size() * 2.0 * Math.PI / droneCount;
            drones.add(new Drone(playerX, playerY, damage, orbitRadius, orbitOffset));
        }
        // Update damage on existing drones
        for (Drone drone : drones) {
            drone.getDamageComponent().setDamage(damage);
        }
    }

    @Override
    public void onLevelUp() {
        switch (level) {
            case 2 -> { damage = 1.3; droneCount = 2; }
            case 3 -> { damage = 1.6; droneCount = 2; }
            case 4 -> { damage = 1.9; droneCount = 3; }
            case 5 -> { damage = 2.2; droneCount = 4; }
        }
    }

    public List<Drone> getDrones() {
        return Collections.unmodifiableList(drones);
    }

    public double getDamage() { return damage; }
    public int getDroneCount() { return droneCount; }
}
