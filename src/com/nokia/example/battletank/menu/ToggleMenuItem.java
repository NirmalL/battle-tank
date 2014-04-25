/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.menu;

import javax.microedition.lcdui.game.Sprite;

/**
 * Class for a menu item that can be toggled, e.g. sound on and off.
 */
public class ToggleMenuItem
    extends MenuItem {

    private volatile boolean on = false;

    public ToggleMenuItem(Sprite sprite) {
        super(sprite);
    }

    public void setOn(boolean on) {
        this.on = on;
        sprite.setFrame(getFrame());
    }

    protected int getFrame() {
        return super.getFrame() + (on ? 0 : 2);
    }

    public boolean isOn() {
        return on;
    }
}
