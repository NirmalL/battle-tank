/**
* Copyright (c) 2012-2014 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.battletank.menu;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

/**
 * Generic class for menu items.
 */
public class MenuItem {

    protected final Sprite sprite;
    private volatile boolean selected = false;

    public MenuItem(Sprite sprite) {
        this.sprite = sprite;
    }

    public void setVisible(boolean v) {
        sprite.setVisible(v);
    }

    public boolean isVisible() {
        return sprite.isVisible();
    }

    public void setSelected(boolean s) {
        selected = s;
        sprite.setFrame(getFrame());
    }

    public boolean isSelected() {
        return selected;
    }

    public int getHeight() {
        return sprite.isVisible() ? sprite.getHeight() : 0;
    }

    public void setCenter(int x, int y) {
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
    }

    /**
     * Tells whether the given point is inside this menu item.
     * 
     * @param x X coordinate of the point
     * @param y Y coordinate of the point
     * @return true if the given point is inside this menu item, false otherwise
     */
    public boolean hits(int x, int y) {
        if (!sprite.isVisible()) {
            return false;
        }
        int left = sprite.getX();
        int right = left + sprite.getWidth();
        int top = sprite.getY();
        int bottom = top + sprite.getHeight();
        return x > left && x < right && y > top && y < bottom;
    }

    public void paint(Graphics g) {
        sprite.paint(g);
    }

    protected int getFrame() {
        return selected ? 1 : 0;
    }
}
