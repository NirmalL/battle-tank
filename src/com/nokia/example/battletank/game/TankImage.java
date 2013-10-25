/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
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
