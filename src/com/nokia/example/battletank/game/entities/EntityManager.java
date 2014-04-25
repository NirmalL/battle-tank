/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.game.entities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.game.LayerManager;

/*
 * Generic class for managing entities.
 */
public abstract class EntityManager {

    protected Entity[] entities;
    private int nextIndex;

    /**
     * Creates an EntityManager holding the given number of entities.
     *
     * @param capacity Number of entities the manager holds
     */
    protected EntityManager(int capacity) {
        entities = new Entity[capacity];
        nextIndex = 0;
    }

    /**
     * Appends the entity manager to a layer manager for entities to be drawn.
     *
     * @param lm LayerManager where the entity manager is appended
     */
    public void appendTo(LayerManager lm) {
        for (int i = 0; i < entities.length; i++) {
            lm.append(entities[i].getSprite());
        }
    }

    /**
     * Refreshes all entities the entity manager contains.
     */
    public void refresh() {
        for (int i = 0; i < entities.length; i++) {
            entities[i].refresh();
        }
    }

    /**
     * Updates all entities the entity manager contains.
     */
    public void update() {
        for (int i = 0; i < entities.length; i++) {
            entities[i].update();
        }
    }

    /**
     * Writes all entities the entity manager contains to DataOutputStream
     *
     * @param dout DataOutputStream where entities are written
     * @throws IOException
     */
    public void writeTo(DataOutputStream dout)
        throws IOException {
        dout.writeInt(entities.length);
        for (int i = 0; i < entities.length; i++) {
            entities[i].writeTo(dout);
        }
    }

    /**
     * Reads all entities the entity manager contains from DataInputStream
     *
     * @param din DataInputStream where entities are read from
     * @throws IOException
     */
    public void readFrom(DataInputStream din)
        throws IOException {
        int length = din.readInt();
        for (int i = 0; i < length; i++) {
            if (i < entities.length) {
                entities[i].readFrom(din);
            }
        }
    }

    protected Entity nextEntity() {
        Entity e = entities[nextIndex];
        nextIndex = (nextIndex + 1) % entities.length;
        return e;
    }
}
