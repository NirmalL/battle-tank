/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.game.entities;

import com.nokia.example.battletank.game.Resources;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.game.Sprite;

/*
 * Class for implementing Bonus entity.
 */
public class Bonus
    extends Entity {

    public static final int AMMO = 0;
    public static final int CLOCK = 1;
    public static final int SHOVEL = 2;
    public static final int LIFE = 3;
    public static final int GRENADE = 4;
    public static final int STAR = 5;
    private volatile int type = AMMO;
    private volatile boolean updateSpriteType = true;
    private volatile boolean visible = false;
    private volatile boolean updateSpriteVisibility = true;
    private final Listener listener;

    public Bonus(Resources r, Listener listener) {
        super(r.gridSizeInPixels * 4, r.gridSizeInPixels * 4, r);
        this.listener = listener;
    }

    protected Sprite createSprite() {
        return new Sprite(resources.ammo, width, height);
    }

    public void refresh() {
        if (updateSpriteType) {
            updateSpriteType = false;
            switch (type) {
                case AMMO:
                    sprite.setImage(resources.ammo, width, height);
                    break;
                case CLOCK:
                    sprite.setImage(resources.clock, width, height);
                    break;
                case SHOVEL:
                    sprite.setImage(resources.shovel, width, height);
                    break;
                case LIFE:
                    sprite.setImage(resources.life, width, height);
                    break;
                case GRENADE:
                    sprite.setImage(resources.grenade, width, height);
                    break;
                case STAR:
                    sprite.setImage(resources.star, width, height);
                    break;
            }
        }
        if (updateSpriteVisibility) {
            updateSpriteVisibility = false;
            sprite.setVisible(visible);
        }
        super.refresh();
    }

    public void update() {
        if (!visible) {
            return;
        }
        if (listener.collidesWithPlayer(this)) {
            visible = false;
            updateSpriteVisibility = true;
        }
    }

    public int getType() {
        return type;
    }

    public void spawn(int type, int x, int y) {
        this.type = type;
        updateSpriteType = true;
        visible = true;
        updateSpriteVisibility = true;
        setPosition(x, y);
    }

    public interface Listener {

        boolean collidesWithPlayer(Bonus bonus);
    }

    public void writeTo(DataOutputStream dout)
        throws IOException {
        super.writeTo(dout);
        dout.writeInt(type);
        dout.writeBoolean(visible);
    }

    public void readFrom(DataInputStream din)
        throws IOException {
        super.readFrom(din);
        type = din.readInt();
        visible = din.readBoolean();
    }
}
