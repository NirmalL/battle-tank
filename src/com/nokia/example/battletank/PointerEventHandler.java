/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank;

/*
 * Class for handling pointer events in the game.
 */
public class PointerEventHandler {

    // threshold for pointer event to be handled as a drag
    private static final int MOVE_THRESHOLD = 10;
    private final int moveThreshold;
    private final Listener listener;
    // coordinates representing drag start point
    private int x0 = 0;
    private int y0 = 0;
    private int softKeyTop;
    private int leftSoftKeyRight;
    private int rightSoftKeyLeft;

    public PointerEventHandler(int w, int h, Listener l) {
        listener = l;
        moveThreshold = Math.min(w, h) * MOVE_THRESHOLD / 100;
        setSize(w, h);
    }

    /**
     * Sets regions for soft keys
     *
     * @param w Width of screen
     * @param h Height of screen
     */
    public final void setSize(int w, int h) {
        softKeyTop = h - h / 10;
        leftSoftKeyRight = w / 3;
        rightSoftKeyLeft = w - w / 3;
    }

    /**
     * Handles pointer press event.
     * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
     * @param x coordinate of press
     * @param y coordinate of press
     */
    public void pointerPressed(int x, int y) {
        // coordinates representing drag start point
        x0 = x;
        y0 = y;
    }

    /**
     * Handles pointer drag event.
     * @see javax.microedition.lcdui.Canvas#pointerDragged(int, int)
     * @param x coordinate of drag
     * @param y coordinate of drag
     */
    public void pointerDragged(int x, int y) {
        // Nothing to do here.
    }

    /**
     * Handles pointer release event.
     * @see javax.microedition.lcdui.Canvas#pointerReleased(int, int)
     * @param x coordinate of release
     * @param y coordinate of release
     */
    public void pointerReleased(int x, int y) {
        // handle taps
        if (handleSoftKeys(x, y)) {
            return;
        }
        // handle touch movements
        if (handleMovement(x, y)) {
            return;
        }
        handleFire();
    }

    private boolean handleSoftKeys(int x, int y) {
        if (x < leftSoftKeyRight && y > softKeyTop) {
            listener.onLeftSoftKey();
            return true;
        }
        if (x > rightSoftKeyLeft && y > softKeyTop) {
            listener.onRightSoftKey();
            return true;
        }
        return false;
    }

    private boolean handleMovement(int x, int y) {
        // check drag length
        int dx = x - x0;
        int adx = Math.abs(dx);
        int dy = y - y0;
        int ady = Math.abs(dy);
        // if movement is long enough, handle as a drag
        if (Math.max(adx, ady) < moveThreshold) {
            return false;
        }

        if (adx > ady) {
            if (dx > 0) {
                listener.onMoveRight();
            }
            else {
                listener.onMoveLeft();
            }
        }
        else {
            if (dy > 0) {
                listener.onMoveDown();
            }
            else {
                listener.onMoveUp();
            }
        }
        return true;
    }

    private void handleFire() {
        listener.onFire();
    }

    public static interface Listener {

        void onMoveLeft();

        void onMoveRight();

        void onMoveUp();

        void onMoveDown();

        void onFire();

        void onLeftSoftKey();

        void onRightSoftKey();
    }
}
