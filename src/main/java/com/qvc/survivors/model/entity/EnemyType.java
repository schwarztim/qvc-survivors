package com.qvc.survivors.model.entity;

import javafx.scene.paint.Color;

public enum EnemyType {
    REGULAR_CUSTOMER("Regular Customer", 1.0, 16.0, 5.0, 1, 1.8, "chase", Color.ORANGE),
    VIP_CUSTOMER("VIP Customer", 3.0, 22.0, 10.0, 5, 1.8, "chase", Color.RED),
    KAREN("Karen", 2.0, 14.0, 15.0, 3, 1.6, "charge", Color.rgb(255, 80, 80)),
    COUPON_CLIPPER("Coupon Clipper", 0.5, 20.0, 3.0, 1, 1.0, "swarm", Color.rgb(200, 200, 100)),
    CART_PUSHER("Cart Pusher", 8.0, 10.0, 12.0, 8, 2.5, "chase", Color.rgb(150, 120, 80)),
    SCALPER_BOT("Scalper Bot", 1.5, 8.0, 4.0, 2, 1.4, "teleport", Color.rgb(100, 200, 255)),
    INFLUENCER("Influencer", 2.5, 18.0, 0.0, 10, 1.6, "circle", Color.rgb(255, 150, 255)),
    RETURN_FRAUDSTER("Return Fraudster", 4.0, 12.0, 8.0, 6, 1.8, "chase", Color.rgb(180, 100, 60)),
    QVC_SUPERFAN("QVC Superfan", 1.5, 15.0, 1.0, 4, 1.2, "ranged", Color.rgb(255, 200, 100)),
    MYSTERY_BOX("Mystery Box", 20.0, 0.0, 0.0, 25, 3.0, "stationary", Color.rgb(200, 150, 255));

    private final String displayName;
    private final double hp;
    private final double speed;
    private final double damage;
    private final int moneyDrop;
    private final double size;
    private final String behaviorId;
    private final Color color;

    EnemyType(String displayName, double hp, double speed, double damage, int moneyDrop,
              double size, String behaviorId, Color color) {
        this.displayName = displayName;
        this.hp = hp;
        this.speed = speed;
        this.damage = damage;
        this.moneyDrop = moneyDrop;
        this.size = size;
        this.behaviorId = behaviorId;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public double getHp() { return hp; }
    public double getSpeed() { return speed; }
    public double getDamage() { return damage; }
    public int getMoneyDrop() { return moneyDrop; }
    public double getSize() { return size; }
    public String getBehaviorId() { return behaviorId; }
    public Color getColor() { return color; }
}
