/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.game.entities;

import com.nokia.example.battletank.game.Point;
import com.nokia.example.battletank.game.Resources;
import com.nokia.example.battletank.game.TankImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.game.Sprite;

/*
 * Class for implementing Tank entity
 */
public abstract class Tank
    extends Entity {

    private static final int SPAWN_DURATION = 50;
    private static final int PROTECTED_DURATION = 100;
    private static final int DESTOYED_DURATION = 10;
    private static final int BULLET_RANGE = 20;
    private static final int BULLET_SPEED = 1;
    private static final int LOADING_DURATION = 15;
    public static final int SPAWNING = 0;
    public static final int PROTECTED = 1;
    public static final int NORMAL = 2;
    public static final int DESTROYED = 3;
    private volatile int state = DESTROYED;
    private volatile boolean updateSpriteState = true;
    private int stateCounter = 0;
    public volatile boolean moving = false;
    private boolean movedLastRound = false;
    protected Point spawnPoint;
    private Listener listener;
    private int loadingCounter = 0;

    public Tank(Point spawnPoint, Resources r, Listener li) {
        super(r.gridSizeInPixels * 4, r.gridSizeInPixels * 4, r);
        this.spawnPoint = spawnPoint;
        this.listener = li;
    }

    protected void setState(int newState) {
        state = newState;
        stateCounter = getStateDuration(newState);
        updateSpriteState = true;
    }

    protected int getStateDuration(int state) {
        switch (state) {
            case SPAWNING:
                return SPAWN_DURATION;
            case PROTECTED:
                return PROTECTED_DURATION;
            case DESTROYED:
                return DESTOYED_DURATION;
        }
        return 0;
    }

    protected int getState() {
        return state;
    }

    public void update() {
        if (loadingCounter > 0) {
            loadingCounter--;
        }
        updateState();
    }

    private void updateState() {
        if (stateCounter > 0) {
            stateCounter--;
            if (stateCounter == 0) {
                switch (state) {
                    case SPAWNING:
                        setState(PROTECTED);
                        break;
                    case PROTECTED:
                        setState(NORMAL);
                        break;
                    case DESTROYED:
                        listener.destroyed(this);
                }
            }
        }
    }

    protected void fire() {
        if (loadingCounter > 0) {
            return;
        }
        loadingCounter = getLoadingDuration();
        listener.fireBullet(this);
    }

    protected int getLoadingDuration() {
        return LOADING_DURATION;
    }

    public int getBulletDirection() {
        return direction;
    }

    public int getBulletRange() {
        return BULLET_RANGE;
    }

    public int getBulletSpeed() {
        return BULLET_SPEED;
    }

    protected boolean setPosition(int x, int y) {
        if (!(listener.collidesWithAnything(this, x, y, width, height))) {
            return super.setPosition(x, y);
        }
        return false;
    }

    public boolean canMove() {
        return state != SPAWNING && state != DESTROYED;
    }

    protected boolean move() {
        final int speed = getSpeed();
        for (int i = 0; i < speed; i++) {
            if (listener.isInWater(this) && movedLastRound) {
                movedLastRound = false;
            }
            else if (!move(resources.tankMovementInPixels)) {
                return false;
            }
        }
        return true;
    }

    protected abstract int getSpeed();

    protected boolean move(int distance) {
        movedLastRound = true;
        return super.move(distance);
    }

    protected boolean changeDirection(int newDirection) {
        if (direction != newDirection) {
            int x, y;
            if (newDirection == DIRECTION_UP || newDirection == DIRECTION_DOWN) {
                x = resources.roundToGrid(position.x);
                y = position.y;
            }
            else {
                x = position.x;
                y = resources.roundToGrid(position.y);
            }
            setPosition(x, y);
        }
        return super.changeDirection(newDirection);
    }

    public void spawn() {
        setPosition(spawnPoint.x, spawnPoint.y);
        changeDirection(DIRECTION_UP);
        setState(SPAWNING);
    }

    public boolean destroy() {
        if (state == NORMAL) {
            setState(DESTROYED);
            return true;
        }
        return false;
    }

    protected Sprite createSprite() {
        return new Sprite(resources.spawn, width, height);
    }

    public void refresh() {
        super.refresh();
        if (updateSpriteState) {
            updateSpriteState = false;
            switch (state) {
                case SPAWNING:
                    sprite.setImage(resources.spawn, width, height);
                    sprite.setFrameSequence(Resources.SPAWNING_ANIM_SEQ);
                    sprite.setVisible(true);
                    break;
                case PROTECTED:
                    sprite.setImage(getImage().image, width, height);
                    sprite.setFrameSequence(getImage().protectedSeq);
                    sprite.setVisible(true);
                    break;
                case NORMAL:
                    sprite.setImage(getImage().image, width, height);
                    sprite.setFrameSequence(getImage().normalSeq);
                    sprite.setVisible(true);
                    break;
                case DESTROYED:
                    sprite.setVisible(false);
                    break;
            }
        }
        if (moving || state == SPAWNING) {
            sprite.nextFrame();
        }
    }

    protected abstract TankImage getImage();

    public boolean collidesWith(Bullet bullet) {
        if (bullet.firedBy == this) {
            return false;
        }
        return super.collidesWith(bullet);
    }

    protected boolean isSolid() {
        return state != SPAWNING && state != DESTROYED;
    }

    public boolean isAlive() {
        return state != DESTROYED;
    }

    public static interface Listener {

        void destroyed(Tank tank);

        boolean isInWater(Tank tank);

        boolean collidesWithAnything(Tank tank, int x, int y, int w, int h);

        void fireBullet(Tank tank);
    }

    public void writeTo(DataOutputStream dout)
        throws IOException {
        super.writeTo(dout);
        dout.writeInt(state);
        dout.writeInt(stateCounter);
    }

    public void readFrom(DataInputStream din)
        throws IOException {
        super.readFrom(din);
        state = din.readInt();
        stateCounter = din.readInt();
    }
}
