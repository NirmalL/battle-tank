/*
 * Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
 * Please see the license file delivered with this project for more details.
 */

package com.nokia.example.battletank.game.audio;

import java.util.Timer;
import java.util.TimerTask;

/*
 * Class to manage audio of the game.
 */
public class AudioManager {

    private static final SoundEffect[] PLAY_BUFFER;

    static {
        String s = System.getProperty("supports.mixing");
        PLAY_BUFFER = s != null && s.equalsIgnoreCase("true")
            ? new SoundEffect[3] : new SoundEffect[1];
    }
    public static final int EXPLOSION_LARGE = 0;
    public static final int EXPLOSION_SMALL = 1;
    public static final int CANNON = 2;
    private static final SoundEffect[] EFFECTS = new SoundEffect[3];

    static {
        EFFECTS[EXPLOSION_LARGE] = new SoundEffect("explosion.mp3");
        EFFECTS[EXPLOSION_SMALL] = new SoundEffect("explosion2.mp3");
        EFFECTS[CANNON] = new SoundEffect("firing.mp3");
    }
    private static boolean soundsEnabled = false;
    private static Timer timer = new Timer();

    /**
     * Enables/disables sounds.
     *
     * @param enable Enables sounds if true, else disables
     */
    public static void setSoundsEnabled(boolean enable) {
        if (enable) {
            enableSounds();
        }
        else {
            disableSounds();
        }
    }

    public static boolean areSoundsEnabled() {
        return soundsEnabled;
    }

    /**
     * Enables sounds and loads effects.
     */
    public static void enableSounds() {
        if (soundsEnabled) {
            return;
        }
        soundsEnabled = true;
        for (int i = 0; i < EFFECTS.length; i++) {
            EFFECTS[i].load();
        }
    }

    /**
     * Disables sounds and closes players of the loaded effects.
     */
    public static void disableSounds() {
        if (!soundsEnabled) {
            return;
        }
        soundsEnabled = false;
        for (int i = 0; i < EFFECTS.length; i++) {
            EFFECTS[i].close();
        }
    }

    /**
     * Increases volume of the effect to the given volume
     *
     * @param effect Effect in question
     * @param volume New volume
     */
    public static void bufferEffect(int effect, int volume) {
        EFFECTS[effect].volume = Math.max(EFFECTS[effect].volume, volume);
    }

    /**
     * Plays effects in every channel.
     */
    public static void playEffects() {
        if (!soundsEnabled) {
            return;
        }
        timer.schedule(new TimerTask() {

            public void run() {
                int channels = PLAY_BUFFER.length;
                for (int effect = 0; effect < EFFECTS.length; effect++) {
                    for (int channel = 0; channel < channels; channel++) {
                        int t = (channel + effect) % channels;
                        if (PLAY_BUFFER[t] == null || PLAY_BUFFER[t].volume
                            < EFFECTS[effect].volume) {
                            PLAY_BUFFER[t] = EFFECTS[effect];
                        }
                    }
                }
                for (int effect = 0; effect < EFFECTS.length; effect++) {
                    boolean contains = false;
                    for (int channel = 0; channel < channels; channel++) {
                        contains = PLAY_BUFFER[channel] == EFFECTS[effect];
                        if (contains) {
                            break;
                        }
                    }
                    if (!contains) {
                        EFFECTS[effect].deallocate();
                    }
                }
                for (int channel = 0; channel < channels; channel++) {
                    PLAY_BUFFER[channel].play();
                }
            }
        }, 0);
    }
}
