/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game.entities;

import com.nokia.example.battletank.game.Level;
import com.nokia.example.battletank.game.Resources;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import javax.microedition.lcdui.game.Sprite;

/*
 * Class for implementing the tree entity.
 */
public class Tree
    extends Entity {

    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private volatile int type = 0;
    private volatile boolean updateSpriteType = true;

    public Tree(int index, Level level, Resources r) {
        super(r.gridSizeInPixels * 4, r.gridSizeInPixels * 4, r);
        type = RANDOM.nextInt(3);
        direction = RANDOM.nextInt(4);
        position.x = level.getTreeX(index) - width / 2;
        position.y = level.getTreeY(index) - height / 2;
    }

    protected Sprite createSprite() {
        return new Sprite(resources.trees, width, height);
    }

    public void refresh() {
        if (updateSpriteType) {
            updateSpriteType = false;
            sprite.setFrame(type % sprite.getRawFrameCount());
        }
        super.refresh();
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
