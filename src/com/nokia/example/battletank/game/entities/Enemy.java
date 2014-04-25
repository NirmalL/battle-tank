/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.game.entities;

import com.nokia.example.battletank.game.Level;
import com.nokia.example.battletank.game.Point;
import com.nokia.example.battletank.game.Resources;
import com.nokia.example.battletank.game.TankImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

/*
 * Class for implementing enemy tanks.
 */
public class Enemy
    extends Tank {

    public static final int BASIC = 0;
    public static final int FAST = 1;
    public static final int HEAVY = 2;
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private final Point base;
    private final int threshold;
    private int type = BASIC;
    private boolean collidedOnLastUpdate = false;

    public Enemy(int index, Level l, Resources r, Listener listener) {
        super(new Point(l.getEnemySpawnX(index), l.getEnemySpawnY(index)), r,
            listener);
        base = new Point(l.getBaseX(), l.getBaseY());
        threshold = r.gridSizeInPixels * 4;
        moving = true;
    }

    protected TankImage getImage() {
        switch (type) {
            case HEAVY:
                return resources.heavyEnemyTank;
            case FAST:
                return resources.fastEnemyTank;
        }
        return resources.enemyTank;
    }

    protected int getSpeed() {
        return type == FAST ? 2 : 1;
    }

    protected int getLoadingDuration() {
        int loadingDuration = super.getLoadingDuration();
        return loadingDuration + RANDOM.nextInt(type == HEAVY ? loadingDuration
            / 2 : 2 * loadingDuration);
    }

    public int getBulletRange() {
        int range = super.getBulletRange();
        return type == HEAVY ? 3 * range / 2 : range;
    }

    public void update() {
        super.update();
        if (canMove()) {
            changeDirectionRandomly();
            collidedOnLastUpdate = !move();
            fire();
        }
    }

    private void changeDirectionRandomly() {
        if (!collidedOnLastUpdate && RANDOM.nextInt(100) < 95) {
            return;
        }
        int up = 1;
        int down = 1;
        int left = 1;
        int right = 1;

        final int dX = position.x - base.x;
        final int dY = position.y - base.y;
        final boolean fX = Math.abs(dX) > Math.abs(dY);
        if (dX > threshold) {
            left += fX ? 10 : 5;
        }
        else if (dX < -threshold) {
            right += fX ? 10 : 5;
        }
        if (dY > threshold) {
            up += fX ? 5 : 10;
        }
        else if (dY < -threshold) {
            down += fX ? 5 : 10;
        }

        switch (direction) {
            case DIRECTION_UP:
                up = 0;
                break;
            case DIRECTION_DOWN:
                down = 0;
                break;
            case DIRECTION_LEFT:
                left = 0;
                break;
            case DIRECTION_RIGHT:
                right = 0;
                break;
        }

        final int r = RANDOM.nextInt(up + down + left + right);
        if (r < up) {
            changeDirection(DIRECTION_UP);
        }
        else if (r < up + down) {
            changeDirection(DIRECTION_DOWN);
        }
        else if (r < up + down + left) {
            changeDirection(DIRECTION_LEFT);
        }
        else {
            changeDirection(DIRECTION_RIGHT);
        }
    }

    public void spawn(int type) {
        this.type = type;
        spawn();
    }

    public int getPoints() {
        switch (type) {
            case BASIC:
                return 100;
            case FAST:
                return 150;
            case HEAVY:
                return 200;
        }
        return 0;
    }

    public void writeTo(DataOutputStream dout)
        throws IOException {
        super.writeTo(dout);
        dout.writeInt(type);
    }

    public void readFrom(DataInputStream din)
        throws IOException {
        super.readFrom(din);
        type = din.readInt();
    }
}
