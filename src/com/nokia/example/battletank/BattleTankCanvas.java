/**
* Copyright (c) 2012-2014 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/

package com.nokia.example.battletank;

import com.nokia.example.battletank.game.Game;
import com.nokia.example.battletank.game.ProtectedContentException;
import com.nokia.example.battletank.game.audio.AudioManager;
import com.nokia.example.battletank.menu.HelpMenu;
import com.nokia.example.battletank.menu.AboutMenu;
import com.nokia.example.battletank.menu.BattleTankMenu;
import com.nokia.example.battletank.menu.BuyMenu;
import com.nokia.example.battletank.menu.Menu;
import java.io.IOException;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * Game canvas class for the user interface.
 */
public class BattleTankCanvas
    extends GameCanvas
    implements GameThread.Listener, CommandListener
{
    public static final boolean HW_BACK_KEY_EXISTS;
    private static final int MAX_RENDERING_FPS = 12;
    private static final int LEFT_SOFTKEY = -6;
    private static final int RIGHT_SOFTKEY = -7;

    private volatile int pointerKeyState = 0;
    private Main main;

    // Menus
    private BattleTankMenu menu;
    private BuyMenu buyMenu;
    private HelpMenu helpMenu;
    private AboutMenu aboutMenu;

    private Menu visibleMenu; // Currently visible menu
    private Game game;
    private PointerEventHandler pointerEventHandler; // Touch handler
    private GameThread gameLoop; // The game loop that is run MAX_RENDERING_FPS timer per second
    private Graphics graphics;
    private Command backCommand;

    static {
        HW_BACK_KEY_EXISTS =
            System.getProperty("com.nokia.keyboard.type")
                .equalsIgnoreCase("OnekeyBack");
    }

    /**
     * Initializes the canvas
     *
     * @param main Main midlet
     */
    public BattleTankCanvas(Main main) {
        super(false);
        setFullScreenMode(true);
        this.main = main;

        // create menus
        createMenu();
        createGame();
        createBuyMenu();
        createHelpMenu();
        createAboutMenu();

        createPointerEventHandler();

        if (HW_BACK_KEY_EXISTS) {
            backCommand = new Command("Back", Command.BACK, 0);
            this.addCommand(backCommand);
            this.setCommandListener(this);
        }
    }

    /**
     * Gets the states of the physical game keys.
     *
     * @return An integer containing the key state information (one bit per
     * key), or 0 if the GameCanvas is not currently shown.
     */
    public int getKeyStates() {
        int keyStates = super.getKeyStates();
        if (keyStates != 0) {
            pointerKeyState = 0;
        }
        else {
            keyStates = pointerKeyState;
            if (pointerKeyState == FIRE_PRESSED) {
                pointerKeyState = 0;
            }
        }
        return keyStates;
    }

    /**
     * Shows menu view.
     */
    public void showMenu() {
        if (visibleMenu == menu) {
            return;
        }
        visibleMenu = menu;
        menu.setSounds(game.soundsEnabled);
        menu.selectItem(hasPointerEvents() ? -1 : 0);
    }

    /**
     * Hides current menu view.
     */
    public void hideCurrentMenu() {
        if (visibleMenu == menu && AudioManager.areSoundsEnabled()
            != game.soundsEnabled) {
            AudioManager.setSoundsEnabled(game.soundsEnabled);
        }
        visibleMenu = null;
    }

    /**
     * Shows buy menu.
     */
    public void showBuyMenu() {
        showMenu();
        buyMenu.selectItem(hasPointerEvents() ? -1 : 0);
        visibleMenu = buyMenu;
    }

    /**
     * Hides the wait indicator in the buy menu.
     */
    public void hideBuyMenuWaitIndicator() {
        buyMenu.hideWaitIndicator();
    }

    /**
     * Saves the current state of the game to RecordStore
     */
    public void saveGame() {
        if (game == null) {
            return;
        }
        try {
            RecordStore gameState = RecordStore.openRecordStore("GameState",
                true);
            if (gameState.getNumRecords() == 0) {
                gameState.addRecord(null, 0, 0);
            }
            byte[] data = game.getState();
            gameState.setRecord(getRecordId(gameState), data, 0, data.length);
            gameState.closeRecordStore();
        }
        catch (Exception e) {
            try {
                RecordStore.deleteRecordStore("GameState");
            }
            catch (RecordStoreException rse) {
                // Nothing to do here.
            }
        }
    }

    /**
     * Hides buy option from the main menu.
     */
    public void hideBuyOption() {
        menu.setBuy(false);
    }

    /**
     * Shows Help view.
     */
    public void showHelpMenu() {
        showMenu();
        helpMenu.selectItem(hasPointerEvents() ? -1 : 0);
        visibleMenu = helpMenu;
    }

    /**
     * Shows About view.
     */
    public void showAboutMenu() {
        showMenu();
        aboutMenu.selectItem(hasPointerEvents() ? -1 : 0);
        visibleMenu = aboutMenu;
    }

    /**
     * Handle key press events.
     * @see javax.microedition.lcdui.Canvas#keyPressed(int)
     * @param key the pressed key
     */
    protected void keyPressed(int key) {
        // delegate key event to proper menu instance
        if (visibleMenu != null) {
            switch (getGameAction(key)) {
                case UP:
                    visibleMenu.selectPrev();
                    break;
                case DOWN:
                    visibleMenu.selectNext();
                    break;
                case FIRE:
                    visibleMenu.clickSelected();
                    break;
            }
        }
        switch (key) {
            case LEFT_SOFTKEY:
                leftSoftkey();
                break;
            case RIGHT_SOFTKEY:
                rightSoftkey();
                break;
        }
    }

    /**
     * delegate pointer events to proper menu
     * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
     * @param x coordinate of press
     * @param y coordinate of press
     */
    protected void pointerPressed(int x, int y) {
        if (visibleMenu != null) {
            visibleMenu.pointerEvent(Menu.POINTER_PRESSED, x, y);
        }
        else {
            pointerEventHandler.pointerPressed(x, y);
        }
    }

    /**
     * delegate pointer drag events to proper menu
     * @see javax.microedition.lcdui.Canvas#pointerDragged(int, int)
     * @param x coordinate
     * @param y coordinate
     */
    protected void pointerDragged(int x, int y) {
        if (visibleMenu != null) {
            visibleMenu.pointerEvent(Menu.POINTER_DRAGGED, x, y);
        }
        else {
            pointerEventHandler.pointerDragged(x, y);
        }
    }

    /**
     * delegate pointer release events to proper menu
     * @see javax.microedition.lcdui.Canvas#pointerReleased(int, int)
     * @param x coordinate
     * @param y coordinate
     */
    protected void pointerReleased(int x, int y) {
        if (visibleMenu != null) {
            visibleMenu.pointerEvent(Menu.POINTER_RELEASED, x, y);
        }
        else {
            pointerEventHandler.pointerReleased(x, y);
        }
    }

    /**
     * Called when this canvas is shown.
     * @see javax.microedition.lcdui.Canvas#showNotify()
     */
    protected void showNotify() {
        if (!Main.isTrial()) {
            hideBuyOption();
        }
        graphics = getGraphics();
        startGameLoop();
        // show menu view first
        showMenu();
    }

    /**
     * Called when this canvas is hidden.
     * @see javax.microedition.lcdui.Canvas#hideNotify()
     */
    protected void hideNotify() {
        AudioManager.disableSounds();
        stopGameLoop();
        graphics = null;
    }

    /**
     * Handles command events.
     * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
     * @param c Command received
     * @param d Displayable that sent the command
     */
    public void commandAction(Command c, Displayable d) {
        if (c == backCommand) {
            if (visibleMenu == menu) {
                main.close();
            } else {
                hideCurrentMenu();
                showMenu();
            }
        }
    }

    /**
     * Called when the drawable area of the Canvas has been changed.
     * @see javax.microedition.lcdui.Canvas#sizeChanged(int, int)
     * @param w the new width in pixels of the drawable area of the Canvas
     * @param h the new height in pixels of the drawable area of the Canvas
     */
    protected void sizeChanged(int w, int h) {
        if (menu != null) {
            menu.setSize(w, h);
        }
        if (buyMenu != null) {
            buyMenu.setSize(w, h);
        }
        if (helpMenu != null) {
            helpMenu.setSize(w, h);
        }
        if (aboutMenu != null) {
            aboutMenu.setSize(w, h);
        }
        if (game != null) {
            game.setViewportSize(w, h);
        }
        if (pointerEventHandler != null) {
            pointerEventHandler.setSize(w, h);
        }
    }

    private void leftSoftkey() {
        if (visibleMenu != menu && game != null) {
            stopGameLoop();
            try {
                game.leftSoftkeyPressed();
            }
            catch (ProtectedContentException e) {
                newGame();
                showBuyMenu();
            }
            catch (IOException ex) {
                Main.showAlertMessage("Level loading failed",
                    "Loading of level file failed unexpectedly.", AlertType.ERROR);
            }

            startGameLoop();
        }
    }

    private void rightSoftkey() {
        if (visibleMenu != null) {
            hideCurrentMenu();
        }
        else {
            saveGame();
            showMenu();
        }
    }

    private void startGameLoop() {
        stopGameLoop();
        gameLoop = new GameThread(this, MAX_RENDERING_FPS);
        gameLoop.start();
    }

    private void createHelpMenu() {
        helpMenu = new HelpMenu(getWidth(), getHeight(), hasPointerEvents(),
            new Menu.Listener() {

                public void itemClicked(int item) {
                    switch (item) {
                        case HelpMenu.BACK:
                            hideCurrentMenu();
                            showMenu();
                            break;
                    }
                }
            });
    }

    private void createAboutMenu() {
        aboutMenu = new AboutMenu(getWidth(), getHeight(), new Menu.Listener() {
            public void itemClicked(int item) {
                switch (item) {
                    case AboutMenu.BACK:
                        hideCurrentMenu();
                        showMenu();
                        break;
                    case BuyMenu.BACK:
                        hideCurrentMenu();
                        showMenu();
                        break;
                }
            }
        }, main);
    }

    /**
     * Create game. If there is a saved game in the RecordStore,
     * load the previous game state. Otherwise create a new game.
     */
    private void createGame() {
        game = new Game(getWidth(), getHeight());
        try {
            RecordStore gameState = RecordStore.openRecordStore("GameState",
                true);
            if (gameState.getNumRecords() == 0
                || !game.load(gameState.getRecord(getRecordId(gameState)))) {
                newGame();
            }
            gameState.closeRecordStore();
        }
        catch (RecordStoreException e) {
            newGame();
        }
    }

    private void newGame() {
        try {
            game.newGame();
        }
        catch (ProtectedContentException e) {
            // should not happen as there should be at least one trial level
            throw new RuntimeException("No levels.");
        }
        catch (IOException e) {
            // should not happen as there should be at least one trial level
            throw new RuntimeException("No levels.");
        }
    }

    private int getRecordId(RecordStore store)
        throws RecordStoreException {
        RecordEnumeration e = store.enumerateRecords(null, null, false);
        try {
            return e.nextRecordId();
        }
        finally {
            e.destroy();
        }
    }

    private void createPointerEventHandler() {
        pointerEventHandler = new PointerEventHandler(getWidth(), getHeight(),
            new PointerEventHandler.Listener() {

                public void onMoveLeft() {
                    pointerKeyState = LEFT_PRESSED;
                }

                public void onMoveRight() {
                    pointerKeyState = RIGHT_PRESSED;
                }

                public void onMoveUp() {
                    pointerKeyState = UP_PRESSED;
                }

                public void onMoveDown() {
                    pointerKeyState = DOWN_PRESSED;
                }

                public void onFire() {
                    pointerKeyState = FIRE_PRESSED;
                }

                public void onLeftSoftKey() {
                    leftSoftkey();
                }

                public void onRightSoftKey() {
                    rightSoftkey();
                }
            });
    }

    private void createBuyMenu() {
        buyMenu = new BuyMenu(getWidth(), getHeight(), new Menu.Listener() {
            public void itemClicked(int item) {
                switch (item) {
                    case BuyMenu.BUY:
                        if (main.purchaseFullVersion()) {
                            buyMenu.showWaitIndicator();
                        }
                        
                        break;
                }
            }
        });
    }

    private void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.cancel();
        }
    }

    private void createMenu() {
        menu = new BattleTankMenu(getWidth(), getHeight(), new Menu.Listener() {

            public void itemClicked(int item) {
                switch (item) {
                    case BattleTankMenu.RESUME:
                        hideCurrentMenu();
                        break;
                    case BattleTankMenu.NEWGAME:
                        newGame();
                        hideCurrentMenu();
                        break;
                    case BattleTankMenu.FULL_VERSION:
                        showBuyMenu();
                        break;
                    case BattleTankMenu.SOUNDS:
                        game.soundsEnabled = menu.toggleSounds();
                        break;
                    case BattleTankMenu.HELP:
                        showHelpMenu();
                        break;
                    case BattleTankMenu.ABOUT:
                        showAboutMenu();
                        break;
                }
            }
        });
        menu.setBuy(Main.isTrial());
    }

    public void runGameLoop() {
        if (visibleMenu != null) {
            visibleMenu.render(graphics);
        }
        else {
            game.update(getKeyStates());
            game.render(graphics);
        }

        flushGraphics();
    }
}
