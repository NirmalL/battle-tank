/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/*
 * Class to hold x and y coordinates of different objects.
 */
public class Point {

    public volatile int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Writes point to DataOutputStream.
     *
     * @param dout DataOutputStream where point is written
     * @throws IOException
     */
    public void writeTo(DataOutputStream dout)
        throws IOException {
        dout.writeInt(x);
        dout.writeInt(y);
    }

    /**
     * Reads point from DataInputStream
     *
     * @param din DataInputStream where point is read
     * @return Point that has been read
     * @throws IOException
     */
    public static Point readFrom(DataInputStream din)
        throws IOException {
        return new Point(din.readInt(), din.readInt());
    }
}
