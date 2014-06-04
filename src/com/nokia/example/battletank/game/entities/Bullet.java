/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game.entities;

import com.nokia.example.battletank.game.Resources;
import javax.microedition.lcdui.game.Sprite;

/*
 * Class for implementing bullet entity.
 */
public class Bullet
    extends Entity {

    private volatile boolean visible = false;
    private volatile boolean updateSpriteVisibility = true;
    private Listener listener;
    private int range = 0;
    private int speed = 0;
    public Tank firedBy;

    public Bullet(Resources r, Listener listener) {
        super(r.gridSizeInPixels, r.gridSizeInPixels, r);
        this.listener = listener;
    }

    protected Sprite createSprite() {
        return new Sprite(resources.bullet);
    }

    public void spawn(int centerX, int centerY, int direction, int range,
        int speed, Tank firedBy) {
        if (visible) {
            return;
        }
        setPosition(centerX - width / 2, centerY - height / 2);
        changeDirection(direction);
        setVisible(true);
        this.range = range;
        this.speed = speed;
        this.firedBy = firedBy;
        update();
    }

    public void update() {
        for (int i = 0; i < speed; i++) {
            updateOnce();
        }
    }

    private void updateOnce() {
        if (!visible) {
            return;
        }
        range--;
        if (listener.collide(this) || range < 0) {
            setVisible(false);
            return;
        }
        move(resources.gridSizeInPixels);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        updateSpriteVisibility = true;
    }

    public boolean isVisible() {
        return visible;
    }

    public void refresh() {
        super.refresh();
        if (updateSpriteVisibility) {
            updateSpriteVisibility = false;
            sprite.setVisible(visible);
        }
    }

    public static interface Listener {

        boolean collide(Bullet bullet);
    }
}
