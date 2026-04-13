package com.qvc.survivors.model.entity;

public class BoomerangProjectile extends Projectile {
    private static final double SIZE = 1.0;
    private final double originX;
    private final double originY;
    private final double maxRange;
    private int pierceCount;
    private final int maxPierce;
    private boolean returning;
    private double playerX;
    private double playerY;
    private final double speed;
    private double rotation;

    public BoomerangProjectile(double x, double y, double velocityX, double velocityY,
                                double damage, double maxRange, int maxPierce) {
        super(x, y, SIZE, SIZE, velocityX, velocityY, damage);
        this.originX = x;
        this.originY = y;
        this.maxRange = maxRange;
        this.maxPierce = maxPierce;
        this.pierceCount = 0;
        this.returning = false;
        this.playerX = x;
        this.playerY = y;
        this.speed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        this.rotation = 0.0;
    }

    @Override
    public void update(double deltaTime) {
        rotation += deltaTime * 15.0;

        if (!returning) {
            super.update(deltaTime);
            double dx = this.x - originX;
            double dy = this.y - originY;
            double distFromOrigin = Math.sqrt(dx * dx + dy * dy);
            if (distFromOrigin >= maxRange) {
                returning = true;
            }
        } else {
            double dx = playerX - this.x;
            double dy = playerY - this.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < 1.5) {
                this.active = false;
                return;
            }
            double vx = (dx / dist) * speed;
            double vy = (dy / dist) * speed;
            this.movementComponent.setVelocityX(vx);
            this.movementComponent.setVelocityY(vy);
            super.update(deltaTime);
        }
    }

    public boolean canPierce() {
        return pierceCount < maxPierce;
    }

    public void incrementPierce() {
        pierceCount++;
        if (pierceCount >= maxPierce && !returning) {
            returning = true;
        }
    }

    public void setPlayerPosition(double px, double py) {
        this.playerX = px;
        this.playerY = py;
    }

    public boolean isReturning() { return returning; }
    public double getRotation() { return rotation; }
    public int getPierceCount() { return pierceCount; }
    public int getMaxPierce() { return maxPierce; }

    public void reset(double x, double y, double vx, double vy, double damage, double maxRange, int maxPierce) {
        super.reset(x, y);
        this.movementComponent.setVelocityX(vx);
        this.movementComponent.setVelocityY(vy);
        this.damageComponent.setDamage(damage);
    }
}
