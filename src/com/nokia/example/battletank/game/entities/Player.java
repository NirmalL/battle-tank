/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
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
import javax.microedition.lcdui.game.GameCanvas;

/*
 * Class for player that controls the tank.
 */
public class Player
    extends Tank {

    private final static int SUPER_AMMO_DURATION = 400;
    private final static int SUPER_TANK_DURATION = 400;
    private int superAmmoCounter = 0;
    private int superTankCounter = 0;

    public Player(Level l, Resources r, Listener listener) {
        super(new Point(l.getPlayerSpawnX(), l.getPlayerSpawnY()), r, listener);
    }

    protected TankImage getImage() {
        return resources.tank;
    }

    protected int getSpeed() {
        return superTankCounter > 0 ? 2 : 1;
    }

    public int getBulletRange() {
        int range = super.getBulletRange();
        return superAmmoCounter > 0 ? 2 * range : range;
    }

    public int getBulletSpeed() {
        int speed = super.getBulletSpeed();
        return superAmmoCounter > 0 ? 2 * speed : speed;
    }

    protected int getStateDuration(int state) {
        if (state == PROTECTED && superTankCounter > 0) {
            return superTankCounter;
        }
        return super.getStateDuration(state);
    }

    public void spawn() {
        superAmmoCounter = 0;
        superTankCounter = 0;
        super.spawn();
    }

    public void superAmmo() {
        superAmmoCounter = SUPER_AMMO_DURATION;
    }

    public void superTank() {
        superTankCounter = SUPER_TANK_DURATION;
        setState(PROTECTED);
    }

    public void update(int keyStates) {
        if (superAmmoCounter > 0) {
            superAmmoCounter--;
        }
        if (superTankCounter > 0) {
            superTankCounter--;
        }
        super.update();
        if (canMove()) {
            handleInput(keyStates);
        }
    }

    private void handleInput(int keyStates) {
        boolean moving = false;

        if ((keyStates & GameCanvas.LEFT_PRESSED) != 0 && !changeDirection(
            Tank.DIRECTION_LEFT)) {
            moving = true;
        }
        if ((keyStates & GameCanvas.RIGHT_PRESSED) != 0
            && !changeDirection(Tank.DIRECTION_RIGHT)) {
            moving = true;
        }
        if ((keyStates & GameCanvas.UP_PRESSED) != 0 && !changeDirection(
            Tank.DIRECTION_UP)) {
            moving = true;
        }
        if ((keyStates & GameCanvas.DOWN_PRESSED) != 0 && !changeDirection(
            Tank.DIRECTION_DOWN)) {
            moving = true;
        }
        this.moving = moving;
        if (moving) {
            move();
        }
        if ((keyStates & GameCanvas.FIRE_PRESSED) != 0) {
            fire();
        }
    }

    public void writeTo(DataOutputStream dout)
        throws IOException {
        super.writeTo(dout);
        dout.writeInt(superAmmoCounter);
        dout.writeInt(superTankCounter);
    }

    public void readFrom(DataInputStream din)
        throws IOException {
        super.readFrom(din);
        superAmmoCounter = din.readInt();
        superTankCounter = din.readInt();
    }
}
