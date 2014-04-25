/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.game;

import com.nokia.example.battletank.Main;
import com.nokia.example.battletank.game.entities.Enemy;
import java.io.IOException;
import javax.microedition.lcdui.Image;

/*
 * Class to manage game levels.
 */
public class Levels {

    private static final String PATH = "/levels/";
    private static final String[] fileNames = {
        "first.png",
        "classic.png",
        "tight.png",
        "easy.png",
        "mario.png",
        "mega.png",
        "bob.png"
    };

    /**
     * Loads image related to the given level. Throws ProtectedContentException
     * if a full version level is tried to be loaded when in trial mode.
     *
     * @param level Level number
     * @return Image containing level structure
     * @throws ProtectedContentException
     * @throws IOException
     */
    public static Image getImage(int level)
        throws ProtectedContentException, IOException {
        int levelIndex = (level - 1) % fileNames.length;
        if (levelIndex < 2 || !Main.isTrial()) {
            String fileName = fileNames[levelIndex];
            return Image.createImage(PATH + fileName);
        }
        else {
            throw new ProtectedContentException();
        }
    }

    /**
     * Returns number of enemies total in the given level.
     *
     * @param level Sequence number of the level
     * @return Number of enemies total
     */
    public static int getTotalEnemies(int level) {
        return Math.min(2 + level / 2 * 2, 20);
    }

    /**
     * Return number of max enemies concurrently in the given level.
     *
     * @param level Sequence number of the level
     * @return Number of max enemies concurrently
     */
    public static int getConcurrentEnemies(int level) {
        return Math.min(2 + level / 4, 6);
    }

    /**
     * Returns type of enemy to be spawned.
     *
     * @param level Sequence number of the level
     * @param remainingEnemies Enemies remaining in the level
     * @return Type of enemy to be spawned
     */
    public static int getEnemyType(int level, int remainingEnemies) {
        int enemy = getTotalEnemies(level) - remainingEnemies;
        if (enemy < 3) {
            return Enemy.BASIC;
        }
        else if (enemy % 3 == 0) {
            return Enemy.FAST;
        }
        else if (enemy % 3 == 1) {
            return Enemy.BASIC;
        }
        else {
            return Enemy.HEAVY;
        }
    }
}
