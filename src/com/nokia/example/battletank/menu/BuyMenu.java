/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.menu;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nokia.example.battletank.BattleTankCanvas;

public class BuyMenu
    extends Menu
{
    public static final int BUY = 0;
    public static final int BACK = 1;
    private static final String TAG = "BuyMenu: ";
    private static final int MENU_ITEM_COUNT =
            BattleTankCanvas.HAS_ONE_KEY_BACK ? 1 : 2;

    private static String appPrice = null;
    private final Image background;
    private int width;
    private int height;
    private int menuWidth;
    private int menuHeight;
    private int leftOffset;
    private int topOffset;
    private final Font font = Font.getFont(
        Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

    public BuyMenu(int width, int height, Listener listener) {
        super(MENU_ITEM_COUNT, width, height, listener);
        System.out.println(TAG + "Constructor: Menu item count is " + MENU_ITEM_COUNT);
        background = loadImage("ad_background.gif");
        setItem(BUY, new MenuItem(loadSprite("buy.png", 2)));
        
        if (MENU_ITEM_COUNT > 1) {
            setItem(BACK, new MenuItem(loadSprite("back.png", 2)));
        }
        
        setSize(width, height);
    }

    public final void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        final int backgroundWidth = background.getWidth();
        final int backgroundHeight = background.getHeight();
        leftOffset = (this.width - backgroundWidth) / 2;
        topOffset = (this.height - backgroundHeight) / 2;
        menuWidth = Math.min(this.width, backgroundWidth);
        menuHeight = Math.min(this.height, backgroundHeight);
        
        final int x = leftOffset + menuWidth / 2;
        int y = topOffset + menuHeight / 4;
        
        if (getSize() > 1) {
            for (int i = 0; i < getSize(); i++) {
                MenuItem item = getItem(i);
                item.setCenter(x, y);
                y += item.getHeight();
            }
        }
        else {
            MenuItem item = getItem(0);
            final int itemHeight = item.getHeight();
            item.setCenter(x, y + itemHeight / 2);
        }
    }

    protected void paint(Graphics g) {
        g.setColor(0x00000000);
        g.fillRect(0, 0, width, height);
        g.drawImage(background, width / 2, height / 2, Graphics.HCENTER
            | Graphics.VCENTER);
        
        if (appPrice != null) {
            g.setColor(0x00ffffff);
            g.setFont(font);
            g.drawString("now for only " + appPrice, width / 2,
                topOffset + menuHeight / 9, Graphics.BASELINE | Graphics.HCENTER);
        }
        
        super.paint(g);
    }

    public static void setPrice(String price) {
        appPrice = price;
    }

    public void showWaitIndicator() {
        this.getItem(BUY).setVisible(false);
    }

    public void hideWaitIndicator() {
        this.getItem(BUY).setVisible(true);
    }
}