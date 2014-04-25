/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.game;

import java.io.IOException;
import javax.microedition.lcdui.Image;

/*
 * Class for handling game resources.
 */
public class Resources {

    public static final int MEDIUM_THRESHOLD = 320;
    public static final int HIGH_THRESHOLD = 640;
    private String resourcePath;
    public final int gridSizeInPixels;
    public final int tankMovementInPixels;
    public final Image tiles;
    public final Image ground;
    public final Image spawn;
    public final TankImage tank;
    public final TankImage enemyTank;
    public final TankImage fastEnemyTank;
    public final TankImage heavyEnemyTank;
    public static final int[] SPAWNING_ANIM_SEQ = {0, 1, 2};
    public final Image bullet;
    public final Image base;
    public static final int[] BASE_NORMAL_SEQ = {0};
    public static final int[] BASE_PROTECTED_SEQ = {0, 1, 2, 3, 2, 1};
    public static final int[] BASE_DESTROYED_SEQ = {4};
    public final Image trees;
    public final Image explosion;
    public final Image ammo;
    public final Image clock;
    public final Image grenade;
    public final Image life;
    public final Image shovel;
    public final Image star;
    public final Image lifeIcon;
    public final Image enemyIcon;
    public final Image hudBackground;

    public Resources(int w, int h) {
        final int max = Math.max(w, h);
        /*
         * Check what is the size of the resources to be loaded
         */
        if (max < MEDIUM_THRESHOLD) {
            resourcePath = "/low/";
            gridSizeInPixels = 4;
        }
        else if (max < HIGH_THRESHOLD) {
            resourcePath = "/medium/";
            gridSizeInPixels = 8;
        }
        else {
            resourcePath = "/high/";
            gridSizeInPixels = 16;
        }
        tankMovementInPixels = gridSizeInPixels / 4;
        tiles = loadImage("tiles.png");
        ground = loadImage("ground.png");
        spawn = loadImage("spawn.png");
        tank = new TankImage(loadImage("tank.png"), new int[]{0, 1, 2},
            new int[]{3, 4, 5});
        enemyTank = new TankImage(loadImage("FT-17_argonne.png"), new int[]{0, 1,
                2}, new int[]{3, 4, 5});
        fastEnemyTank = new TankImage(loadImage("m1_abrams.png"), new int[]{1, 2,
                3}, new int[]{0});
        heavyEnemyTank = new TankImage(loadImage("jytky_39.png"), new int[]{1, 2,
                3}, new int[]{0});
        bullet = loadImage("bullet.png");
        base = loadImage("base.png");
        explosion = loadImage("explosion.png");
        ammo = loadImage("ammo.png");
        clock = loadImage("clock.png");
        grenade = loadImage("grenade.png");
        life = loadImage("life.png");
        shovel = loadImage("shovel.png");
        star = loadImage("star.png");
        lifeIcon = loadImage("life_icon.png");
        enemyIcon = loadImage("enemy_icon.png");
        hudBackground = loadImage("hud_bg.png");
        trees = loadImage("trees.png");
    }

    /**
     * Changes a pixel coordinate to a grid coordinate.
     *
     * @param x Pixel coordinate
     * @return Grid coordinate of the give pixel coordinate
     */
    public int toGrid(int x) {
        return x / gridSizeInPixels;
    }

    /**
     * Changes a grid coordinate to a pixel coordinate.
     *
     * @param i Grid coordinate
     * @return Pixel coordinate of the given grid coordinate
     */
    public int toPixels(int i) {
        return i * gridSizeInPixels;
    }

    /**
     * Changes a pixel coordinate to the nearest grid coordinate.
     *
     * @param x Pixel coordinate
     * @return The nearest grid coordinate of the given pixel coordinate
     */
    public int roundToGrid(int x) {
        return toPixels(toGrid(x + gridSizeInPixels / 2));
    }

    private Image loadImage(String fileName) {
        try {
            return Image.createImage(resourcePath + fileName);
        }
        catch (IOException e) {
            return null;
        }
    }
}
