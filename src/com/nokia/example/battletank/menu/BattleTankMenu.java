/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.menu;

import com.nokia.example.battletank.Main;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/*
 * Class for implementing the main menu.
 */
public class BattleTankMenu
    extends Menu {

    public static final int RESUME = 0;
    public static final int NEWGAME = 1;
    public static final int FULL_VERSION = 2;
    public static final int SOUNDS = 3;
    public static final int HELP = 4;
    public static final int ABOUT = 5;
    private final Image background;
    private final Image trial;
    private final Image full;
    private final ToggleMenuItem sounds;
    private final MenuItem fullVersion;
    private int width;
    private int height;

    public BattleTankMenu(int w, int h, Listener l) {
        super(6, w, h, l);
        background = loadImage("background.gif");
        trial = loadImage("trial.png");
        full = loadImage("full.png");
        setItem(RESUME, new MenuItem(loadSprite("resume.png", 2)));
        setItem(NEWGAME, new MenuItem(loadSprite("new_game.png", 2)));
        fullVersion = new MenuItem(loadSprite("full_version.png", 2));
        setItem(FULL_VERSION, fullVersion);
        sounds = new ToggleMenuItem(loadSprite("sounds.png", 4));
        setItem(SOUNDS, sounds);
        setItem(HELP, new MenuItem(loadSprite("help.png", 2)));
        setItem(ABOUT, new MenuItem(loadSprite("about.png", 2)));
        setSize(w, h);
    }

    /**
     * Set menu size to fit the screen.
     * @param w width of the menu
     * @param h height of the menu
     */
    public final void setSize(int w, int h) {
        width = w;
        height = h;
        final int bgW = background.getWidth();
        final int bgH = background.getHeight();
        final int leftOffset = (w - bgW) / 2;
        final int topOffset = (h - bgH) / 2;
        final int menuW = Math.min(w, bgW);
        final int menuH = Math.min(h, bgH);
        int x = leftOffset + menuW / 2;
        int y = topOffset + menuH / 9;
        for (int i = 0; i < getSize(); i++) {
            MenuItem item = getItem(i);
            item.setCenter(x, y);
            y += item.getHeight();
        }
    }

    protected void paint(Graphics g) {
        g.setColor(0x00000000);
        g.fillRect(0, 0, width, height);
        g.drawImage(background, width / 2, height / 2,
            Graphics.HCENTER | Graphics.VCENTER);
        g.drawImage(Main.isTrial() ? trial : full, width * 7 / 9, height
            - trial.getHeight(), Graphics.HCENTER | Graphics.VCENTER);
        super.paint(g);
    }

    public void setSounds(boolean on) {
        sounds.setOn(on);
    }

    public void setBuy(boolean visible) {
        fullVersion.setVisible(visible);
        setSize(width, height);
    }

    public boolean toggleSounds() {
        sounds.setOn(!sounds.isOn());
        return sounds.isOn();
    }
}
