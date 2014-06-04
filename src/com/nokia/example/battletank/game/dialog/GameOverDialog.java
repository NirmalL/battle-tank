/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game.dialog;

import com.nokia.example.battletank.game.Resources;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/*
 * Class for implementing game over -dialog.
 */
public class GameOverDialog
    extends Dialog {

    public volatile int highScore = 0;

    public GameOverDialog(Resources r) {
        super(r);
    }

    /**
     * Paints the dialog.
     * @param g Graphics object
     * @param w Width of view port
     * @param h Height of view port
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
        g.drawString("GAME OVER", w / 2, h / 2 - lineHeight, Graphics.HCENTER
            | Graphics.BASELINE);
        g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN,
            Font.SIZE_MEDIUM));
        g.drawString("level: " + level, w / 2, h / 2, Graphics.HCENTER
            | Graphics.BASELINE);
        g.drawString("score: " + score, w / 2, h / 2 + lineHeight,
            Graphics.HCENTER | Graphics.BASELINE);
        g.drawString("high score: " + highScore, w / 2, h / 2 + 2 * lineHeight,
            Graphics.HCENTER | Graphics.BASELINE);
    }
}
