package com.qvc.survivors.engine;

public class Camera {
    private double x;
    private double y;
    private double viewportWidth;
    private double viewportHeight;
    private double tileSize = 15.0;
    private double shakeOffsetX;
    private double shakeOffsetY;
    private double shakeDuration;
    private double shakeIntensity;
    private boolean enabled = true;

    public Camera(double x, double y, double viewportWidth, double viewportHeight) {
        this.x = x;
        this.y = y;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    public double worldToScreenX(double worldX) {
        if (!enabled) {
            return worldX * tileSize;
        }
        return (worldX - x) * tileSize + viewportWidth / 2.0 + shakeOffsetX;
    }

    public double worldToScreenY(double worldY) {
        if (!enabled) {
            return worldY * tileSize;
        }
        return (worldY - y) * tileSize + viewportHeight / 2.0 + shakeOffsetY;
    }

    public double screenToWorldX(double screenX) {
        if (!enabled) {
            return screenX / tileSize;
        }
        return (screenX - viewportWidth / 2.0 - shakeOffsetX) / tileSize + x;
    }

    public double screenToWorldY(double screenY) {
        if (!enabled) {
            return screenY / tileSize;
        }
        return (screenY - viewportHeight / 2.0 - shakeOffsetY) / tileSize + y;
    }

    public boolean isInView(double worldX, double worldY, double margin) {
        double screenX = worldToScreenX(worldX);
        double screenY = worldToScreenY(worldY);
        double marginPx = margin * tileSize;
        return screenX >= -marginPx && screenX <= viewportWidth + marginPx
            && screenY >= -marginPx && screenY <= viewportHeight + marginPx;
    }

    public void follow(double targetX, double targetY, double smoothing, double deltaTime) {
        x += (targetX - x) * smoothing * deltaTime;
        y += (targetY - y) * smoothing * deltaTime;
    }

    public void setViewportSize(double w, double h) {
        this.viewportWidth = w;
        this.viewportHeight = h;
    }

    public void update(double deltaTime) {
        if (shakeDuration > 0) {
            shakeDuration -= deltaTime;
            shakeOffsetX = (Math.random() - 0.5) * 2.0 * shakeIntensity;
            shakeOffsetY = (Math.random() - 0.5) * 2.0 * shakeIntensity;
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }
    }

    public void shake(double intensity, double duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getViewportWidth() {
        return viewportWidth;
    }

    public double getViewportHeight() {
        return viewportHeight;
    }

    public double getTileSize() {
        return tileSize;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
