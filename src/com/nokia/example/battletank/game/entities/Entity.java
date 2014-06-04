/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game.entities;

import com.nokia.example.battletank.game.Point;
import com.nokia.example.battletank.game.Resources;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.game.Sprite;

/*
 * Generic class for entities.
 */
public abstract class Entity {

    public static final int DIRECTION_UP = 0;
    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_LEFT = 3;
    protected Sprite sprite = null;
    protected volatile int direction = DIRECTION_UP;
    private volatile boolean updateSpriteDirection = true;
    protected final Point position = new Point(0, 0);
    private volatile boolean updateSpritePosition = true;
    public final int width;
    public final int height;
    protected final Resources resources;

    /**
     * Creates a game entity.
     *
     * @param width Width of entity
     * @param height Height of entity
     * @param resources Resources object
     */
    protected Entity(int width, int height, Resources resources) {
        this.width = width;
        this.height = height;
        this.resources = resources;
    }

    public int getDirection() {
        return direction;
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }

    public int getCenterX() {
        return position.x + width / 2;
    }

    public int getCenterY() {
        return position.y + height / 2;
    }

    /**
     * Returns true if the entity collides with the given entity
     *
     * @param e Entity that maybe collides
     * @return True if the entities collide, false otherwise
     */
    public boolean collidesWith(Entity e) {
        return collidesWith(e.getX(), e.getY(), e.width, e.height);
    }

    /**
     * Returns true if the entity collides with the given area. Colliding
     * requires the area to be solid.
     *
     * @param x X coordinate of the area
     * @param y Y coordinate of the area
     * @param w Width of the area
     * @param h Height of the area
     * @return True if the entity collides with the area.
     */
    public boolean collidesWith(int x, int y, int w, int h) {
        if (!isSolid()) {
            return false;
        }
        return overlaps(x, x + w, position.x, position.x + width) && overlaps(y,
            y + h, position.y, position.y + height);
    }

    public Sprite getSprite() {
        if (sprite == null) {
            sprite = createSprite();
        }
        return sprite;
    }

    /**
     * Refreshes the direction and position of the entity.
     */
    public void refresh() {
        if (updateSpriteDirection) {
            updateSpriteDirection = false;
            switch (direction) {
                case DIRECTION_UP:
                    sprite.setTransform(Sprite.TRANS_NONE);
                    break;
                case DIRECTION_DOWN:
                    sprite.setTransform(Sprite.TRANS_ROT180);
                    break;
                case DIRECTION_LEFT:
                    sprite.setTransform(Sprite.TRANS_ROT270);
                    break;
                case DIRECTION_RIGHT:
                    sprite.setTransform(Sprite.TRANS_ROT90);
                    break;
            }
        }
        if (updateSpritePosition) {
            updateSpritePosition = false;
            sprite.setPosition(position.x, position.y);
        }
    }

    public void update() {
        // Nothing to do here.
    }

    /**
     * Writes the entity to a DataOutputStream
     *
     * @param dout DataOutputStream where the entity is written
     * @throws IOException
     */
    public void writeTo(DataOutputStream dout)
        throws IOException {
        dout.writeInt(direction);
        position.writeTo(dout);
    }

    protected void readFrom(DataInputStream din)
        throws IOException {
        direction = din.readInt();
        Point p = Point.readFrom(din);
        position.x = p.x;
        position.y = p.y;
    }

    protected boolean changeDirection(int newDirection) {
        boolean changed = direction != newDirection;
        direction = newDirection;
        if (changed) {
            updateSpriteDirection = true;
            updateSpritePosition = true;
        }
        return changed;
    }

    protected abstract Sprite createSprite();

    protected boolean move(int distance) {
        int x = position.x;
        int y = position.y;

        switch (direction) {
            case DIRECTION_UP:
                y -= distance;
                break;
            case DIRECTION_DOWN:
                y += distance;
                break;
            case DIRECTION_LEFT:
                x -= distance;
                break;
            case DIRECTION_RIGHT:
                x += distance;
                break;
        }
        return setPosition(x, y);
    }

    protected boolean setPosition(int x, int y) {
        position.x = x;
        position.y = y;
        updateSpritePosition = true;
        return true;
    }

    protected boolean isSolid() {
        return true;
    }

    private boolean overlaps(int startA, int endA, int startB, int endB) {
        return startA < startB ? endA > startB : endB > startA;
    }
}
