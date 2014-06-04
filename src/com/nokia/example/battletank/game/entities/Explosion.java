/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game.entities;

import com.nokia.example.battletank.game.Resources;
import javax.microedition.lcdui.game.Sprite;

/*
 * Class for implementing the Explosion entity.
 */
public class Explosion
    extends Entity {

    private volatile boolean start = false;

    public Explosion(Resources r) {
        super(r.gridSizeInPixels * 4, r.gridSizeInPixels * 4, r);
    }

    protected Sprite createSprite() {
        Sprite s = new Sprite(resources.explosion, width, height);
        s.setVisible(false);
        return s;
    }

    public void refresh() {
        super.refresh();
        if (sprite.isVisible()) {
            if (sprite.getFrame() == sprite.getFrameSequenceLength() - 1) {
                sprite.setVisible(false);
            }
            else {
                sprite.nextFrame();
            }
        }
        if (start) {
            start = false;
            sprite.setVisible(true);
            sprite.setFrame(0);
        }
    }

    public void show(int centerX, int centerY) {
        setPosition(centerX - width / 2, centerY - height / 2);
        start = true;
    }
}
