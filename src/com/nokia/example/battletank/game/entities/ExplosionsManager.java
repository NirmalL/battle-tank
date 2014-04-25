/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
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
