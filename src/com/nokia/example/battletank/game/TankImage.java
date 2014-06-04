/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game;

import javax.microedition.lcdui.Image;

/*
 * Class for tank image.
 */
public class TankImage {

    public final Image image;
    public final int[] protectedSeq; // images for the tank in protected mode
    public final int[] normalSeq; // images for the tank in normal mode

    public TankImage(Image image, int[] protectedSeq, int[] normalSeq) {
        this.image = image;
        this.protectedSeq = protectedSeq;
        this.normalSeq = normalSeq;
    }
}
