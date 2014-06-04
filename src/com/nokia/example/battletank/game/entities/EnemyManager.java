/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game.entities;

import com.nokia.example.battletank.game.Level;
import com.nokia.example.battletank.game.Resources;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/*
 * Class for managing enemies.
 */
public class EnemyManager
    extends EntityManager {

    private static final int FREEZE_DURATION = 500;
    private int frozenCounter = 0;

    public EnemyManager(Level l, int concurrentEnemies, Resources r,
        Tank.Listener tl) {
        super(Math.min(concurrentEnemies, l.getEnemySpawnPointsLength()));
        for (int i = 0; i < entities.length; i++) {
            entities[i] = new Enemy(i, l, r, tl);
        }
    }

    public int numberOfAlive() {
        int ret = 0;
        for (int i = 0; i < entities.length; i++) {
            if (get(i).isAlive()) {
                ret++;
            }
        }
        return ret;
    }

    private Enemy get(int index) {
        return (Enemy) entities[index];
    }

    public boolean collidesWith(Tank tank, int x, int y, int w, int h) {
        Enemy enemy;
        for (int i = 0; i < entities.length; i++) {
            enemy = get(i);
            if (tank != enemy && enemy.collidesWith(x, y, w, h)) {
                return true;
            }
        }
        return false;
    }

    public Enemy collidesWith(Bullet bullet) {
        Enemy enemy;
        for (int i = 0; i < entities.length; i++) {
            enemy = get(i);
            if (enemy.collidesWith(bullet)) {
                return enemy;
            }
        }
        return null;
    }

    public Enemy[] all() {
        Enemy[] enemies = new Enemy[entities.length];
        for (int i = 0; i < entities.length; i++) {
            enemies[i] = get(i);
        }
        return enemies;
    }

    public void update() {
        if (frozenCounter > 0) {
            frozenCounter--;
            return;
        }
        super.update();
    }

    public void freezeEnemies() {
        frozenCounter = FREEZE_DURATION;
    }

    public void writeTo(DataOutputStream dout)
        throws IOException {
        super.writeTo(dout);
        dout.writeInt(frozenCounter);
    }

    public void readFrom(DataInputStream din)
        throws IOException {
        super.readFrom(din);
        frozenCounter = din.readInt();
    }
}
