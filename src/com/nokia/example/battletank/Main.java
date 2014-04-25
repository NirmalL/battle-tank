/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.battletank;

import com.nokia.example.battletank.menu.BuyMenu;

/* Make sure that you have Nokia In-App Payment API library added to the
 * project. Otherwise the following classes to import cannot be found. The
 * library must be exported too or - even though there are no compilation
 * erros - the application will crash.
 */
import com.nokia.payment.NPayException;
import com.nokia.payment.NPayListener;
import com.nokia.payment.NPayManager;
import com.nokia.payment.ProductData;
import com.nokia.payment.PurchaseData;

import com.nokia.mid.ui.VirtualKeyboard;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

/*
 * Main class to handle starting the application, pausing it and exiting it.
 */
public class Main
    extends MIDlet
    implements NPayListener
{
    /* Nokia In-App Payment product IDs for offline testing.
     * Uncomment the ID matching the desired UI flow to test.
     */
    public static final String PURCHASE_ID = "success-battle_tank"; // Successful payment flow
    //public static final String PURCHASE_ID = "restore-battle_tank"; // Successful restore flow
    //public static final String PURCHASE_ID = "fail-battle_tank"; // Failed purchase flow

    /* Nokia In-App payment product IDs for online testing.
     * Note that the online IDs do not work on emulator.
     */
    //public static final String PURCHASE_ID = "1023608"; // success
    //public static final String PURCHASE_ID = "1023626"; // fail

    // Trial version as default
    private static boolean trial = true;
    public static NPayManager manager;
    protected static Display display;
    private BattleTankCanvas battleTankCanvas = null;

    /**
     * Initializes display and in-app payment manager.
     * @see javax.microedition.midlet.MIDlet#startApp()
     */
    public void startApp() {
        if (battleTankCanvas == null) {
            battleTankCanvas = new BattleTankCanvas(this);
            display = Display.getDisplay(this);
            display.setCurrent(battleTankCanvas);
            
            // Hide virtual keyboard if one exists
            if (hasOnekeyBack()) {
                VirtualKeyboard.hideOpenKeypadCommand(true);
                VirtualKeyboard.suppressSizeChanged(true);
            }

            // Initialize Nokia In-App Payment
            initPaymentAPI();
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

    public static boolean isTrial() {
        return trial;
    }

    public static void setTrial(boolean t) {
        trial = t;
    }

    /**
     * Purchases the full versions of Battle Tank.
     *
     * @return true if purchdase was successful
     */
    public boolean purchaseFullVersion() {
        try {
            manager.purchaseProduct(PURCHASE_ID);
        }
        catch (Exception e) {
            showAlertMessage(display, "Purchase failure",
                "Purchase process failed.", AlertType.ERROR);
            return false;
        }
        
        return true;
    }

    /**
     * @see com.nokia.payment.NPayListener#productDataReceived(com.nokia.payment.ProductData[])
     */
    public void productDataReceived(ProductData[] productDataItems) {
        for (int i = 0; i < productDataItems.length; i++) {
            ProductData productData = productDataItems[i];
            
            if (productData.isValid()
                    && productData.getProductId() == PURCHASE_ID)
            {
                BuyMenu.setPrice(productData.getLocalizedPrice()
                    + " " + productData.getCurrency());
            }
        }
    }

    /**
     * @see com.nokia.payment.NPayListener#restorableProductsReceived(com.nokia.payment.ProductData[])
     */
    public void restorableProductsReceived(ProductData[] productDataItems) {
        // Not implemented
    }

    public void purchaseCompleted(PurchaseData pd) {
        battleTankCanvas.hideBuyMenuWaitIndicator();
        
        if (pd.getStatus() == PurchaseData.PURCHASE_SUCCESS
                || pd.getStatus() == PurchaseData.PURCHASE_RESTORE_SUCCESS)
        {
            setTrial(false);
            battleTankCanvas.hideBuyOption();
            battleTankCanvas.hideCurrentMenu();
            battleTankCanvas.showMenu();
        }
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
     * Initialize payment manager.
     * 
     * @throws NPayException
     */
    private void initPaymentAPI() {
        try {
            manager = new NPayManager(this);
            manager.setNPayListener(this);
    
            // Check is Nokia In-App Payment Enabler library installed on device.
            if (!manager.isNPayAvailable()) {
                // If not installed, launch installer. 
                // You should do nothing after launchNPaySetup is called,
                // as the app will exit after it.
                manager.launchNPaySetup();
                return;
            }

            // Query application price data. 
            if (trial) {
                String[] productIds = { PURCHASE_ID };
                manager.getProductData(productIds);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
