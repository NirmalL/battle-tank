/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.game.entities;

import com.nokia.example.battletank.game.Level;
import com.nokia.example.battletank.game.Resources;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

/*
 * Class for managing bonuses.
 */
public class BonusManager
    extends EntityManager {

    private static final int MAX_CONCURRENT_BONUSES = 3;
    private static final int MIN_SPAWN_DURATION = 300;
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private final Level level;
    private boolean spawning = true;
    private int spawnCounter = MIN_SPAWN_DURATION;

    public BonusManager(Level level, Resources r, Bonus.Listener l) {
        super(MAX_CONCURRENT_BONUSES);
        this.level = level;
        for (int i = 0; i < entities.length; i++) {
            entities[i] = new Bonus(r, l);
        }
    }

    public void update() {
        if (spawning) {
            spawnCounter--;
            if (spawnCounter < 0) {
                spawnABonus();
                spawnCounter = MIN_SPAWN_DURATION + RANDOM.nextInt(
                    MIN_SPAWN_DURATION);
            }
        }
        super.update();
    }

    private void spawnABonus() {
        int type = RANDOM.nextInt(6);
        int x = RANDOM.nextInt(level.widthInPixels - entities[0].width);
        int y = RANDOM.nextInt(level.heightInPixels - entities[0].height);
        ((Bonus) nextEntity()).spawn(type, x, y);
    }

    public void stopSpawning() {
        spawning = false;
    }

    public void writeTo(DataOutputStream dout)
        throws IOException {
        super.writeTo(dout);
        dout.writeBoolean(spawning);
        dout.writeInt(spawnCounter);
    }

    public void readFrom(DataInputStream din)
        throws IOException {
        super.readFrom(din);
        spawning = din.readBoolean();
        spawnCounter = din.readInt();
    }
}
