/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game.audio;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;

/*
 * Class to play samples.
 */
public class SoundEffect {

    private final static String PATH = "/sounds/";
    private final String file;
    private Player player = null;
    private VolumeControl volumeControl;
    public int volume = 0;

    public SoundEffect(String fileName) {
        file = PATH + fileName;
    }

    /**
     * Load correct sample based on filename.
     */
    public void load() {
        if (player != null) {
            return;
        }
        try {
            InputStream is = this.getClass().getResourceAsStream(file);
            player = Manager.createPlayer(is, "audio/mp3");
            player.prefetch();
            volumeControl = (VolumeControl) player.getControl("VolumeControl");
        }
        catch (IOException e) {
            // Nothing to do here.
        }
        catch (MediaException e) {
            // Nothing to do here.
        }
    }

    public void close() {
        if (player == null) {
            return;
        }
        player.close();
        player = null;
        volumeControl = null;
    }

    public void play() {
        if (volume > 0) {
            try {
                player.prefetch();
                player.stop();
                player.setMediaTime(0);
                volumeControl.setLevel(volume);
                player.start();
            }
            catch (MediaException ex) {
                // Nothing to do here.
            }
        }
        volume = 0;
    }

    public void deallocate() {
        player.deallocate();
        volume = 0;
    }
}
