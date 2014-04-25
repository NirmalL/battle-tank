/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.menu;

import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/*
 * Class that implements the wait indicator.
 */
public class WaitIndicator
    extends Sprite {

    private final static long INTERVAL = 60;
    private Timer animator;
    private TimerTask rotate;

    public WaitIndicator(Image animation) {
        super(animation, animation.getHeight(), animation.getHeight());
        defineReferencePixel(animation.getHeight() / 2, animation.getHeight()
            / 2);
        setVisible(false);
    }

    /**
     * Shows the wait indicator and animates it.
     */
    public void show() {
        setVisible(true);
        animator = new Timer();
        rotate = new TimerTask() {

            public void run() {
                nextFrame();
            }
        };
        animator.schedule(rotate, 0, INTERVAL);
    }

    /**
     * Hides the wait indicator.
     */
    public void hide() {
        setVisible(false);
        animator.cancel();
        setFrame(0);
    }

    public void setCenter(int x, int y) {
        setRefPixelPosition(x, y);
    }
}
