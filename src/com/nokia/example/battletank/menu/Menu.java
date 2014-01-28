/**
* Copyright (c) 2012-2014 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.menu;

import com.nokia.example.battletank.game.Resources;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/*
 * Generic class for menu.
 */
public abstract class Menu {
    public static final int POINTER_PRESSED = 0;
    public static final int POINTER_DRAGGED = 1;
    public static final int POINTER_RELEASED = 2;
    private String resourcePath;
    private final MenuItem[] items;
    private final Listener listener;

    /**
     * Creates a menu.
     *
     * @param capacity Number of menu items this menu is holding
     * @param w Width of screen
     * @param h Height of screen
     * @param listener Menu item click listener
     */
    protected Menu(int capacity, int w, int h, Listener listener) {
        items = new MenuItem[capacity];
        if (Math.max(w, h) < Resources.MEDIUM_THRESHOLD) {
            resourcePath = "/menu/low/";
        }
        else {
            resourcePath = "/menu/medium/";
        }
        this.listener = listener;
    }

    protected final int getSize() {
        return items.length;
    }

    protected final void setItem(int i, MenuItem item) {
        items[i] = item;
    }

    protected final MenuItem getItem(int i) {
        return items[i];
    }

    public final void render(Graphics g) {
        paint(g);
    }

    protected void paint(Graphics g) {
        for (int i = 0; i < items.length; i++) {
            items[i].paint(g);
        }
    }

    /**
     * Sets the given item selected.
     *
     * @param selected Sequential number related to the item
     */
    public void selectItem(int selected) {
        for (int i = 0; i < items.length; i++) {
            items[i].setSelected(i == selected);
        }
    }

    /**
     * Returns the selected item.
     *
     * @return Sequential number related to the item that is selected. If none
     * is selected, -1 is returned.
     */
    private int getSelected() {
        for (int i = 0; i < items.length; i++) {
            if (items[i].isSelected()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Sets the next item selected.
     */
    public void selectNext() {
        selectItem((getSelected() + 1) % items.length);
    }

    /**
     * Sets the previous item selected.
     */
    public void selectPrev() {
        selectItem((Math.max(getSelected(), 0) - 1 + items.length)
            % items.length);
    }

    /**
     * Sends a click event to the item that is selected.
     */
    public void clickSelected() {
        clickItem(getSelected());
    }

    /**
     * Sends a click event to the item given.
     *
     * @param index Index of the item to whom the click event will be sent
     */
    private void clickItem(int index) {
        if (index > -1 && index < items.length) {
            listener.itemClicked(index);
            items[index].setSelected(false);
        }
    }

    /**
     * Sends a pointer event to the menu. If the event coordinates are inside a
     * menu item, it is clicked/selected.
     *
     * @param type Type of pointer event
     * @param x X coordinate of the event
     * @param y Y coordinate of the event
     */
    public void pointerEvent(int type, int x, int y) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].hits(x, y)) {
                if (type == POINTER_RELEASED) {
                    clickItem(i);
                }
                else {
                    selectItem(i);
                }
                return;
            }
        }
        selectItem(-1);
    }

    /**
     * Loads a sprite in the given file name.
     *
     * @param fileName File name of the sprite image
     * @param lines Number of images the sprite contains
     * @return Sprite
     */
    protected Sprite loadSprite(String fileName, int lines) {
        Image i = loadImage(fileName);
        return new Sprite(i, i.getWidth(), i.getHeight() / lines);
    }

    protected Image loadImage(String fileName) {
        try {
            return Image.createImage(resourcePath + fileName);
        }
        catch (IOException e) {
            return null;
        }
    }

    public interface Listener {

        void itemClicked(int item);
    }
}
