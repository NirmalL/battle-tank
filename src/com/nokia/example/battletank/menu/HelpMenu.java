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
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/*
 * Class for implementing help menu.
 */
public class HelpMenu
    extends Menu {

    public static final boolean HW_BACK_KEY_EXISTS;
    public static final int ITEM_COUNT;
    public static final int BACK = 0;
    private final boolean hasPointerEvents;
    private int width;
    private int height;
    private final Font fontBold = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,
        Font.SIZE_SMALL);
    private final Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,
        Font.SIZE_SMALL);

    static {
        HW_BACK_KEY_EXISTS = System.getProperty("com.nokia.keyboard.type").equalsIgnoreCase("OnekeyBack");
        ITEM_COUNT = HW_BACK_KEY_EXISTS ? 0 : 1;
    }

    public HelpMenu(int w, int h, boolean hasPointerEvents, Listener l) {
        super(ITEM_COUNT, w, h, l);
        this.hasPointerEvents = hasPointerEvents;
        if (!HW_BACK_KEY_EXISTS) {
            setItem(BACK, new MenuItem(loadSprite("back.png", 2)));
            setSize(w, h);
        }
    }

    public final void setSize(int w, int h) {
        width = w;
        height = h;

        int x = width / 2;
        int y = 11 * height / 12;
        for (int i = 0; i < getSize(); i++) {
            MenuItem item = getItem(i);
            item.setCenter(x, y);
            y += item.getHeight();
        }
    }

    protected void paint(Graphics g) {
        g.setColor(0x00000000);
        g.fillRect(0, 0, width, height);
        g.setColor(0x00ffffff);
        int x = width / 2;
        int y = height / 12;
        int a = Graphics.BASELINE | Graphics.HCENTER;
        g.setFont(fontBold);
        g.drawString("Goal", x, y, a);
        y += font.getHeight();
        g.setFont(font);
        g.drawString("Defend the base", x, y, a);
        y += font.getHeight();
        g.drawString("Destroy all enemy tanks", x, y, a);
        y += 3 * font.getHeight() / 2;
        g.setFont(fontBold);
        g.drawString("Keyboard controls", x, y, a);
        y += font.getHeight();
        g.setFont(font);
        g.drawString("Move: direction keys", x, y, a);
        y += font.getHeight();
        g.drawString("Fire: fire key", x, y, a);
        if (hasPointerEvents) {
            y += 3 * font.getHeight() / 2;
            g.setFont(fontBold);
            g.drawString("Touch controls", x, y, a);
            y += font.getHeight();
            g.setFont(font);
            g.drawString("Move: flick", x, y, a);
            y += font.getHeight();
            g.drawString("Fire: tap", x, y, a);
        }
        y += 3 * font.getHeight() / 2;
        g.setFont(fontBold);
        if (Main.isTrial()) {
            g.drawString("Purchasing Full Version", x, y, a);
            y += font.getHeight();
            g.setFont(font);
            g.drawString("Select Buy to purchase.", x, y, a);
        }
        super.paint(g);
    }
}
