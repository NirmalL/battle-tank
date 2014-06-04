/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game.entities;

import com.nokia.example.battletank.game.Resources;

/*
 * Class for managing explosions.
 */
public class ExplosionsManager
    extends EntityManager {

    public ExplosionsManager(int capacity, Resources resources) {
        super(capacity);
        for (int i = 0; i < entities.length; i++) {
            entities[i] = new Explosion(resources);
        }
    }

    public void show(Entity e) {
        ((Explosion) nextEntity()).show(e.getCenterX(), e.getCenterY());
    }
}
