/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank;

import com.nokia.mid.ui.VirtualKeyboard;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

/*
 * Main class to handle starting the application, pausing it and exiting it.
 */
public class Main
    extends MIDlet {

    protected static Display display;
    private BattleTankCanvas battleTankCanvas = null;

    /**
     * Initializes display.
     * @see javax.microedition.midlet.MIDlet#startApp()
     */
    public void startApp() {
        if (battleTankCanvas == null) {
            battleTankCanvas = new BattleTankCanvas(this);
            display = Display.getDisplay(this);
            display.setCurrent(battleTankCanvas);
        }

        // Hide virtual keyboard if one exists.
        if (hasOnekeyBack()) {
            VirtualKeyboard.hideOpenKeypadCommand(true);
            VirtualKeyboard.suppressSizeChanged(true);
        }
    }

    /**
     * Pauses the app.
     * @see javax.microedition.midlet.MIDlet#pauseApp()
     */
    public void pauseApp() {
        // Nothing to do here.
    }

    /**
     * Saves game before destroying the application.
     * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
     * @param unconditional Should the MIDlet cleanup and release all resources.
     */
    public void destroyApp(boolean unconditional) {
        if (battleTankCanvas != null) {
            battleTankCanvas.saveGame();
        }
    }

    public void close() {
        destroyApp(true);
        notifyDestroyed();
    }

    public BattleTankCanvas getBattleTankCanvas() {
        return battleTankCanvas;
    }

    /**
     * Displays an alert message.
     *
     * @param title Title of the message
     * @param alertText Text of the message
     * @param type Type of the alert
     */
    public static void showAlertMessage(String title, String alertText, AlertType type) {
        showAlertMessage(display, title, alertText, type);
    }

    public static void showAlertMessage(Display display, String title,
        String alertText, AlertType type) {
        Alert alert = new Alert(title, alertText, null, type);
        display.setCurrent(alert, display.getCurrent());
    }

    /**
     * Determine whether the device has hardware keyBack. If so, it has a
     * virtual keyboard, which isn't needed here. This method is needed to
     * preserve backwards compatibility with devices that do not have a
     * virtual keyboard.
     * @return true or false
     */
    private boolean hasOnekeyBack() {
        String keyboard = System.getProperty("com.nokia.keyboard.type");
        if (keyboard != null) {
            return (keyboard.equalsIgnoreCase("OnekeyBack"));
        }
        else {
            return false;
        }
    }
}
