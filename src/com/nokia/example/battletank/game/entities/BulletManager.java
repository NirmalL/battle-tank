/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game.entities;

import com.nokia.example.battletank.game.Resources;

/*
 * Class for managing bullet entities.
 */
public class BulletManager
    extends EntityManager {

    public BulletManager(int capacity, Resources r, Bullet.Listener c) {
        super(capacity);
        for (int i = 0; i < entities.length; i++) {
            entities[i] = new Bullet(r, c);
        }
    }

    public void fire(Tank tank) {
        ((Bullet) nextEntity()).spawn(tank.getCenterX(), tank.getCenterY(),
            tank.getBulletDirection(), tank.getBulletRange(), tank.
            getBulletSpeed(), tank);
    }
}
