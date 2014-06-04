/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game.entities;

import com.nokia.example.battletank.game.Level;
import com.nokia.example.battletank.game.Resources;

/*
 * Class for managing tree entities.
 */
public class TreeManager
    extends EntityManager {

    public TreeManager(Level l, Resources r) {
        super(l.getTreesLength());
        for (int i = 0; i < entities.length; i++) {
            entities[i] = new Tree(i, l, r);
        }
    }
}
