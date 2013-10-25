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
