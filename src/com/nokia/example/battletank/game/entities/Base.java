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
import com.nokia.example.battletank.game.Resources;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.game.Sprite;

/*
 * Class for implementing Base entity.
 */
public class Base
    extends Entity {

    private static final int PROTECT_DURATION = 500;
    private volatile boolean destroyed = false;
    private volatile int protectedCounter = 0;
    private volatile boolean updateSprite = true;

    public Base(Level l, Resources r) {
        super(r.gridSizeInPixels * 4, r.gridSizeInPixels * 4, r);
        setPosition(l.getBaseX(), l.getBaseY());
    }

    protected Sprite createSprite() {
        return new Sprite(resources.base, width, height);
    }

    public boolean destroy() {
        if (protectedCounter > 0) {
            return false;
        }
        destroyed = true;
        updateSprite = true;
        return true;
    }

    public void protect() {
        protectedCounter = PROTECT_DURATION;
        updateSprite = true;
    }

    public void refresh() {
        super.refresh();
        if (updateSprite) {
            updateSprite = false;
            if (destroyed) {
                sprite.setFrameSequence(Resources.BASE_DESTROYED_SEQ);
            }
            else if (protectedCounter > 0) {
                sprite.setFrameSequence(Resources.BASE_PROTECTED_SEQ);
            }
            else {
                sprite.setFrameSequence(Resources.BASE_NORMAL_SEQ);
            }
        }
        sprite.nextFrame();
    }

    public void update() {
        if (protectedCounter > 0) {
            protectedCounter--;
            if (protectedCounter == 0) {
                updateSprite = true;
            }
        }
    }

    public void writeTo(DataOutputStream dout)
        throws IOException {
        dout.writeBoolean(destroyed);
        dout.writeInt(protectedCounter);
    }

    public void readFrom(DataInputStream din)
        throws IOException {
        destroyed = din.readBoolean();
        protectedCounter = din.readInt();
    }
}
