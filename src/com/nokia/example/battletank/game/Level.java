/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.game;

import com.nokia.example.battletank.game.entities.Entity;
import com.nokia.example.battletank.game.entities.Bullet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;
import javax.microedition.lcdui.game.TiledLayer;

/*
 * Class for implementing a level.
 */
public class Level {

    private static final int BRICK_COLOR = 0x00f00000;
    private static final int STEEL_COLOR = 0x00d0d0d0;
    private static final int WATER_COLOR = 0x000000f0;
    private static final int TREE_COLOR = 0x00009000;
    private static final int ENEMY_SPAWN_POINT_COLOR = 0x0000f000;
    private static final int PLAYER_SPAWN_POINT_COLOR = 0x00f0f000;
    private static final int BASE_COLOR = 0x00707070;
    private static final byte BRICK_WALL = 1;
    private static final byte STEEL_WALL = 2;
    private static final byte WATER = 3;
    private static final int[] WATER_SEQ = {9, 13, 17, 21, 17, 13};
    private final byte[][] level;
    public final int width;
    public final int height;
    public final int widthInPixels;
    public final int heightInPixels;
    private final Point[] enemies;
    private final Point player;
    private final Point base;
    private final Point[] trees;
    private final Resources resources;
    private TiledLayer ground = null;
    private TiledLayer walls = null;
    private volatile boolean refreshWalls = true;
    private final int[] waterTiles = new int[4];
    private int currentwaterFrame = 0;

    private Level(byte[][] level, Point[] enemies, Point player, Point base,
        Point[] trees, Resources resources) {
        this.level = level;
        this.width = level.length;
        this.height = level[0].length;
        this.enemies = enemies;
        this.player = player;
        this.base = base;
        this.trees = trees;
        this.widthInPixels = width * resources.gridSizeInPixels;
        this.heightInPixels = height * resources.gridSizeInPixels;
        this.resources = resources;
    }

    /**
     * Loads a level.
     *
     * @param levelNumber Number of the wanted level.
     * @param resources Resource object for images
     * @return Level object
     * @throws ProtectedContentException
     * @throws IOException
     */
    public static Level load(int levelNumber, Resources resources)
        throws IOException {
        final Image image = Levels.getImage(levelNumber);
        final int w = image.getWidth();
        final int h = image.getHeight();
        final int[] raw = new int[w * h];
        image.getRGB(raw, 0, w, 0, 0, w, h);
        byte[][] level = new byte[w][h];
        Vector enemySpawnPointsVector = new Vector();
        Point playerSpawnPoint = null;
        Point base = null;
        Vector treePointsVector = new Vector();
        byte value;
        int x, y;
        Hashtable unknownColors = new Hashtable();

        // add the level elements set in level image
        for (int i = 0; i < raw.length; i++) {
            value = 0;
            x = i % w;
            y = i / w;
            switch (raw[i] & 0x00F0F0F0) {
                case BRICK_COLOR:
                    value = BRICK_WALL;
                    break;
                case STEEL_COLOR:
                    value = STEEL_WALL;
                    break;
                case WATER_COLOR:
                    value = WATER;
                    break;
                case TREE_COLOR:
                    treePointsVector.addElement(new Point(x, y));
                    break;
                case ENEMY_SPAWN_POINT_COLOR:
                    enemySpawnPointsVector.addElement(new Point(x, y));
                    break;
                case PLAYER_SPAWN_POINT_COLOR:
                    playerSpawnPoint = new Point(x, y);
                    break;
                case BASE_COLOR:
                    base = new Point(x, y);
                    break;
                default:
                    if ((raw[i] & 0x00FFFFFF) != 0x00FFFFFF) {
                        Integer key = new Integer(raw[i] & 0x00FFFFFF);
                        unknownColors.put(key, key);
                    }
            }
            level[x][y] = value;
        }
        Point[] enemySpawnPoints = new Point[enemySpawnPointsVector.size()];
        enemySpawnPointsVector.copyInto(enemySpawnPoints);
        Point[] trees = new Point[treePointsVector.size()];
        treePointsVector.copyInto(trees);
        StringBuffer debug = new StringBuffer();
        Enumeration e = unknownColors.keys();
        while (e.hasMoreElements()) {
            debug.append(Integer.toHexString(((Integer) e.nextElement()).
                intValue()));
            debug.append(", ");
        }
        if (enemySpawnPoints.length == 0) {
            throw new IllegalArgumentException("no enemy spawn points " + debug.
                toString());
        }
        if (playerSpawnPoint == null) {
            throw new IllegalArgumentException("no player spawn point " + debug.
                toString());
        }
        if (base == null) {
            throw new IllegalArgumentException("no base " + debug.toString());
        }
        return new Level(level, enemySpawnPoints, playerSpawnPoint, base, trees,
            resources);
    }

    public Layer getGroundLayer() {
        if (ground == null) {
            Image image = resources.ground;
            int iw = image.getWidth();
            int ih = image.getHeight();
            int w = widthInPixels / iw + 1;
            int h = heightInPixels / ih + 1;
            ground = new TiledLayer(w, h, image, iw, ih);
            ground.fillCells(0, 0, w, h, 1);
        }
        return ground;
    }

    /**
     * Creates a tiled layer for walls if not already created.
     * 
     * @return Tiled layer for walls
     */
    public Layer getWallLayer() {
        if (walls == null) {
            walls = new TiledLayer(width, height, resources.tiles,
                resources.gridSizeInPixels, resources.gridSizeInPixels);
            for (int i = 0; i < 4; i++) {
                waterTiles[i] = walls.createAnimatedTile(getNextWaterTile(i));
            }
        }
        return walls;
    }

    /**
     * Refreshes level tiles.
     */
    public void refresh() {
        if (refreshWalls) {
            refreshWalls = false;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    walls.setCell(x, y, getWallTileIndex(level[x][y], x, y));
                }
            }
        }
        currentwaterFrame++;
        for (int i = 0; i < waterTiles.length; i++) {
            walls.setAnimatedTile(waterTiles[i], getNextWaterTile(i));
        }
    }

    private int getNextWaterTile(int tileIndex) {
        if (currentwaterFrame / 4 == WATER_SEQ.length) {
            currentwaterFrame = 0;
        }
        return WATER_SEQ[currentwaterFrame / 4] + tileIndex;
    }

    private int getWallTileIndex(int type, int x, int y) {
        switch (type) {
            case BRICK_WALL:
                return x % 2 + y % 2 * 2 + 1;
            case STEEL_WALL:
                return x % 2 + y % 2 * 2 + 5;
            case WATER:
                return waterTiles[x % 2 + y % 2 * 2];
        }
        return 0;
    }

    private boolean collidesBorder(int left, int top, int width, int height) {
        return left < 0 || top < 0 || left + width > widthInPixels || top
            + height > heightInPixels;
    }

    private boolean collidesWalls(int left, int top, int width, int height) {
        int leftGrid = toGrid(left, this.width);
        int topGrid = toGrid(top, this.height);
        int rightGrid = toGrid(left + width - 1, this.width);
        int bottomGrid = toGrid(top + height - 1, this.height);
        for (int i = leftGrid; i <= rightGrid; i++) {
            for (int j = topGrid; j <= bottomGrid; j++) {
                if (isWall(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWall(int i, int j) {
        return level[i][j] == BRICK_WALL || level[i][j] == STEEL_WALL;
    }

    private int toGrid(int x, int max) {
        return checkBounds(resources.toGrid(x), max);
    }

    private int checkBounds(int i, int length) {
        return Math.min(Math.max(i, 0), length - 1);
    }

    public boolean collideAndDestroy(Bullet b) {
        int left = b.getX();
        int top = b.getY();
        if (!collides(left, top, b.width, b.height)) {
            return false;
        }
        int leftGrid = toGrid(left, width);
        int topGrid = toGrid(top, height);
        int rightGrid = toGrid(left + b.width - 1, width);
        int bottomGrid = toGrid(top + b.height - 1, height);
        switch (b.getDirection()) {
            case Entity.DIRECTION_UP:
                bottomGrid = checkBounds(topGrid + 1, height);
                leftGrid = checkBounds(leftGrid - 1, width);
                rightGrid = checkBounds(rightGrid + 1, width);
                break;
            case Entity.DIRECTION_DOWN:
                topGrid = checkBounds(bottomGrid - 1, height);
                leftGrid = checkBounds(leftGrid - 1, width);
                rightGrid = checkBounds(rightGrid + 1, width);
                break;
            case Entity.DIRECTION_LEFT:
                rightGrid = checkBounds(leftGrid + 1, width);
                topGrid = checkBounds(topGrid - 1, height);
                bottomGrid = checkBounds(bottomGrid + 1, height);
                break;
            case Entity.DIRECTION_RIGHT:
                leftGrid = checkBounds(rightGrid - 1, width);
                topGrid = checkBounds(topGrid - 1, height);
                bottomGrid = checkBounds(bottomGrid + 1, height);
                break;
        }
        for (int i = leftGrid; i <= rightGrid; i++) {
            for (int j = topGrid; j <= bottomGrid; j++) {
                destroyWall(i, j);
            }
        }
        return true;
    }

    public void destroyWall(int i, int j) {
        if (level[i][j] == BRICK_WALL) {
            level[i][j] = 0;
            refreshWalls = true;
        }
    }

    public boolean collides(int left, int top, int width, int height) {
        return collidesBorder(left, top, width, height) || collidesWalls(left,
            top, width, height);
    }

    public boolean isInWater(Entity e) {
        return isInWater(e.getX(), e.getY(), e.width, e.height);
    }

    public boolean isInWater(int left, int top, int width, int height) {
        int leftGrid = toGrid(left, this.width);
        int topGrid = toGrid(top, this.height);
        int rightGrid = toGrid(left + width - 1, this.width);
        int bottomGrid = toGrid(top + height - 1, this.height);
        for (int i = leftGrid; i <= rightGrid; i++) {
            for (int j = topGrid; j <= bottomGrid; j++) {
                if (level[i][j] == WATER) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getPlayerSpawnX() {
        return resources.toPixels(player.x);
    }

    public int getPlayerSpawnY() {
        return resources.toPixels(player.y);
    }

    public int getEnemySpawnPointsLength() {
        return enemies.length;
    }

    public int getEnemySpawnX(int index) {
        return resources.toPixels(enemies[index].x);
    }

    public int getEnemySpawnY(int index) {
        return resources.toPixels(enemies[index].y);
    }

    public int getBaseX() {
        return resources.toPixels(base.x);
    }

    public int getBaseY() {
        return resources.toPixels(base.y);
    }

    public int getTreesLength() {
        return trees.length;
    }

    public int getTreeX(int index) {
        return resources.toPixels(trees[index].x) + resources.gridSizeInPixels
            / 2;
    }

    public int getTreeY(int index) {
        return resources.toPixels(trees[index].y) + resources.gridSizeInPixels
            / 2;
    }

    public void writeTo(DataOutputStream dout)
        throws IOException {
        dout.writeInt(width);
        dout.writeInt(height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                dout.writeByte(level[i][j]);
            }
        }
        dout.writeInt(enemies.length);
        for (int i = 0; i < enemies.length; i++) {
            enemies[i].writeTo(dout);
        }
        player.writeTo(dout);
        base.writeTo(dout);
        dout.writeInt(trees.length);
        for (int i = 0; i < trees.length; i++) {
            trees[i].writeTo(dout);
        }
    }

    public static Level readFrom(DataInputStream din, Resources resources)
        throws IOException {
        int width = din.readInt();
        int height = din.readInt();
        byte[][] level = new byte[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                level[i][j] = din.readByte();
            }
        }
        Point[] enemies = new Point[din.readInt()];
        for (int i = 0; i < enemies.length; i++) {
            enemies[i] = Point.readFrom(din);
        }
        Point player = Point.readFrom(din);
        Point base = Point.readFrom(din);
        Point[] trees = new Point[din.readInt()];
        for (int i = 0; i < trees.length; i++) {
            trees[i] = Point.readFrom(din);
        }
        return new Level(level, enemies, player, base, trees, resources);
    }
}
