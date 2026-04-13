package com.qvc.survivors.view;

import javafx.scene.paint.Color;

public class DamageNumber {
    private double x;
    private double y;
    private double damage;
    private Color color;
    private double lifetime;
    private boolean active;

    public DamageNumber() {
        this.active = false;
    }

    public void init(double x, double y, double damage, Color color) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.color = color;
        this.lifetime = 1.0;
        this.active = true;
    }

    public void update(double deltaTime) {
        if (!active) return;
        y -= 2.0 * deltaTime;
        lifetime -= deltaTime;
        if (lifetime <= 0) {
            active = false;
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDamage() { return damage; }
    public Color getColor() { return color; }
    public double getLifetime() { return lifetime; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
