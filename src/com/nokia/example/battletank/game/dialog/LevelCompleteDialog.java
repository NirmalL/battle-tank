/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.game.dialog;

import com.nokia.example.battletank.game.Resources;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/*
 * Class for implementing the level complete dialog.
 */
public class LevelCompleteDialog
    extends Dialog {

    public LevelCompleteDialog(Resources r) {
        super(r);
    }

    /**
     * Paints the dialog.
     * @param g graphics object
     * @param w width of view port
     * @param h height of view port
     */
    public void paint(Graphics g, int w, int h) {
        if (!visible) {
            return;
        }
        super.paint(g, w, h);
        // TODO to improve performance use images not strings
        g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN,
            Font.SIZE_LARGE));
        final int lineHeight = g.getFont().getHeight();
        g.setColor(0x00ffffff);
        g.drawString("LEVEL " + level, w / 2, h / 2 - lineHeight,
            Graphics.HCENTER | Graphics.BASELINE);
        g.drawString("COMPLETE", w / 2, h / 2, Graphics.HCENTER
            | Graphics.BASELINE);
        g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN,
            Font.SIZE_MEDIUM));
        g.drawString("score: " + score, w / 2, h / 2 + lineHeight,
            Graphics.HCENTER | Graphics.BASELINE);
    }
}
