/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.game;

import com.nokia.mid.ui.DirectUtils;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.TiledLayer;

/*
 * Class to handle hud related operations.
 */
public class Hud {

    public static final int NONE = 0;
    public static final int CONTINUE = 1;
    public static final int NEWGAME = 2;
    private final Resources resources;
    private int width = 0;
    private TiledLayer background;
    private Image hudImgAbove, hudImgBelow;
    private volatile int lives;
    private volatile int score;
    private volatile int enemies;
    private volatile int leftButton = NONE;
    private final int padding;
    private Graphics hudG;

    /**
     * Constructor of Hud. Represents the panels at the top and bottom of the
     * screen.
     *
     * @param r Resources object
     * @param w Width of the screen
     * @param h Height of the screen
     */
    public Hud(Resources r, int w, int h) {
        resources = r;
        padding = r.gridSizeInPixels / 2;
    }

    /**
     * Paints the hud. To gain better performance, the hud is cached by drawing
     * it to an image that is redrawn only when something changes in the hud.
     * drawString methods, for example, are quite heavy operations to perform in
     * every frame.
     *
     * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
     * @param g Graphics object
     * @param w Width of screen
     * @param h Height of screen
     */
    public void paint(Graphics g, int w, int h) {
        if (hudImgAbove == null) {
            hudImgAbove = initHudImage(w, h);
            int textOffset = (background.getHeight()
                - hudG.getFont().getHeight()) / 2 - 1;
            hudG.drawImage(resources.lifeIcon, padding, 0, Graphics.LEFT
                | Graphics.TOP);
            int x = padding + resources.lifeIcon.getWidth();
            hudG.drawString(" x " + lives, x, textOffset, Graphics.LEFT
                | Graphics.TOP);
            hudG.drawString("SCORE " + score, w / 2, textOffset, Graphics.HCENTER
                | Graphics.TOP);
            hudG.drawString(" x " + enemies, w - padding, textOffset, Graphics.RIGHT
                | Graphics.TOP);
            x = w - hudG.getFont().stringWidth(" x " + enemies) - padding;
            hudG.drawImage(resources.enemyIcon, x, 0, Graphics.RIGHT
                | Graphics.TOP);
        }

        if (hudImgBelow == null) {
            hudImgBelow = initHudImage(w, h);
            hudG.drawString("MENU", w - padding, -2, Graphics.RIGHT
                | Graphics.TOP);
            if (leftButton == CONTINUE) {
                hudG.drawString("CONTINUE", padding, -2, Graphics.LEFT
                    | Graphics.TOP);
            }
            else if (leftButton == NEWGAME) {
                hudG.drawString("NEW GAME", padding, -2, Graphics.LEFT
                    | Graphics.TOP);
            }
        }

        g.drawImage(hudImgAbove, 0, 0, Graphics.LEFT | Graphics.TOP);
        g.drawImage(hudImgBelow, 0, h - hudImgBelow.getHeight(), Graphics.LEFT
            | Graphics.TOP);
    }

    private Image initHudImage(int w, int h) {
        updateBackground(w, h);
        Image img = DirectUtils.createImage(w, background.getHeight(),
            0x00000000);
        hudG = img.getGraphics();
        background.setPosition(0, 0);
        background.paint(hudG);
        hudG.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,
            Font.SIZE_SMALL));
        hudG.setColor(0x00ffffff);
        return img;
    }

    /**
     * Updates the number lives drawn in the hud.
     *
     * @param lives Number of lives remaining
     */
    public void updateLives(int lives) {
        this.lives = lives;
        updateHudAbove();
    }

    /**
     * Updates the score drawn in the hud.
     *
     * @param score Score to be updated
     */
    public void updateScore(int score) {
        this.score = score;
        updateHudAbove();
    }

    /**
     * Updates the number of enemies drawn in the hud.
     *
     * @param enemies Number of enemies remaining
     */
    public void updateEnemies(int enemies) {
        this.enemies = enemies;
        updateHudAbove();
    }

    public int getEnemies() {
        return enemies;
    }

    /**
     * Updates the functionality of the left button
     *
     * @param leftButton Button that left button should represent
     */
    public void updateLeftButton(int leftButton) {
        this.leftButton = leftButton;
        updateHudBelow();
    }

    /**
     * Forces the top part of the hud to be redrawn.
     */
    private void updateHudAbove() {
        hudImgAbove = null;
    }

    /**
     * Forces the bottom part of the hud to be redrawn.
     */
    private void updateHudBelow() {
        hudImgBelow = null;
    }

    private void updateBackground(int w, int h) {
        if (background == null || w != width) {
            width = w;
            Image bg = resources.hudBackground;
            background = new TiledLayer(1, (w - 1) / bg.getWidth() + 1, bg, bg.
                getWidth(), bg.getHeight());
            background.fillCells(0, 0, background.getColumns(), 1, 1);
        }
    }
}
