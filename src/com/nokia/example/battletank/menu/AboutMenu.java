/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.battletank.menu;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

/**
 * Class for implementing about menu.
 */
public class AboutMenu
    extends Menu
{
    public static final boolean HW_BACK_KEY_EXISTS;
    public static final int ITEM_COUNT;
    public static final int BACK = 0;

    private final Font fontBold =
        Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
    private final Font font =
        Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);    
    private String versionNumberString; 
    private int width;
    private int height;

    static {
        HW_BACK_KEY_EXISTS =
            System.getProperty("com.nokia.keyboard.type")
                .equalsIgnoreCase("OnekeyBack");
        ITEM_COUNT = HW_BACK_KEY_EXISTS ? 0 : 1;
    }

    public AboutMenu(int w, int h, Listener l, MIDlet midlet) {
        super(ITEM_COUNT, w, h, l);
        
        if (midlet != null) {
            versionNumberString = midlet.getAppProperty("MIDlet-Version");
        }
        
        if (!HW_BACK_KEY_EXISTS) {
            setItem(BACK, new MenuItem(loadSprite("back.png", 2)));
            setSize(w, h);
        }
    }

    public final void setSize(int w, int h) {
        width = w;
        height = h;
        
        int x = width / 2;
        int y = 11 * height / 13;
        
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
        int y = height / 9;
        int a = Graphics.BASELINE | Graphics.HCENTER;
        g.setFont(fontBold);
        g.drawString("JME Battle Tank", x, y, a);
        y += font.getHeight();
        g.drawString("by Nokia", x, y, a);
        y += 3 * font.getHeight() / 2;
        g.setFont(font);
        g.drawString("Version " + versionNumberString, x, y, a);
        super.paint(g);
    }
}
