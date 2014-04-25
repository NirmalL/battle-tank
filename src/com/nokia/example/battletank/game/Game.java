/**
* Copyright (c) 2012-2014 Microsoft Mobile. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.battletank.game;

import com.nokia.example.battletank.Main;
import com.nokia.example.battletank.game.dialog.GameOverDialog;
import com.nokia.example.battletank.game.dialog.LevelCompleteDialog;
import com.nokia.example.battletank.game.entities.Entity;
import com.nokia.example.battletank.game.entities.Enemy;
import com.nokia.example.battletank.game.entities.Bonus;
import com.nokia.example.battletank.game.entities.ExplosionsManager;
import com.nokia.example.battletank.game.entities.BulletManager;
import com.nokia.example.battletank.game.entities.TreeManager;
import com.nokia.example.battletank.game.entities.Bullet;
import com.nokia.example.battletank.game.entities.Base;
import com.nokia.example.battletank.game.entities.BonusManager;
import com.nokia.example.battletank.game.entities.EnemyManager;
import com.nokia.example.battletank.game.entities.Tank;
import com.nokia.example.battletank.game.entities.Player;
import com.nokia.example.battletank.game.audio.AudioManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.LayerManager;

/*
 * Class to handle game logic.
 */
public class Game {

    private static final int POINTS_PER_LIFE = 100;  // hit points of tank
    private Resources resources;  // contains game images
    private final int soundPadding;
    public boolean soundsEnabled = true;
    private int levelNumber = 0;
    private int lives;
    private int score = 0;
    private int highScore = 0;
    private int enemiesInReserve;
    private Level level;
    private Player player;
    private EnemyManager enemyManager;
    private Base base;
    private Hud hud;
    private GameOverDialog gameOverDialog;
    private LevelCompleteDialog levelCompleteDialog;
    private LayerManager layerManager;
    private int viewportLeft;
    private int viewportTop;
    private int viewportWidth;
    private int viewportHeight;
    private BulletManager bulletManager;
    private ExplosionsManager explosionManager;
    private BonusManager bonusManager;
    private TreeManager treeManager;

    public Game(int w, int h) {
        resources = new Resources(w, h);
        soundPadding = resources.gridSizeInPixels * 20;
        setViewportSize(w, h);
        hud = new Hud(resources, w, h);
        gameOverDialog = new GameOverDialog(resources);
        levelCompleteDialog = new LevelCompleteDialog(resources);
    }

    /**
     * Renders the game state.
     *
     * @param g Graphics object
     */
    public void render(Graphics g) {
        level.refresh();
        bulletManager.refresh();
        explosionManager.refresh();
        bonusManager.refresh();
        base.refresh();
        player.refresh();
        enemyManager.refresh();
        refreshViewport();
        clearScreen(g);
        layerManager.paint(g, 0, 0);
        gameOverDialog.paint(g, viewportWidth, viewportHeight);
        levelCompleteDialog.paint(g, viewportWidth, viewportHeight);
        hud.paint(g, viewportWidth, viewportHeight);
    }

    /**
     * Updates objects according to the game logic.
     *
     * @param keyStates States of device keys
     */
    public void update(int keyStates) {
        // update 2 steps
        for (int i = 0; i < 2; i++) {
            bulletManager.update();
            bonusManager.update();
            base.update();
            player.update(keyStates);
            enemyManager.update();
        }
        AudioManager.playEffects();
    }

    /**
     * Continues to next level or starts a new game depending on win/lose
     *
     * @throws ProtectedContentException
     * @throws IOException
     */
    public void leftSoftkeyPressed()
        throws ProtectedContentException, IOException {
        if (isLevelComplete()) {
            nextLevel();
        }
        else if (isGameOver()) {
            newGame();
        }
    }

    /**
     * Resets the game state and starts a new game.
     *
     * @throws ProtectedContentException
     * @throws IOException
     */
    public void newGame()
        throws ProtectedContentException, IOException {
        gameOverDialog.hide();
        levelCompleteDialog.hide();
        levelNumber = 1;
        lives = 3;
        score = 0;
        loadLevel();
    }

    /**
     * Proceeds player to next level.
     *
     * @throws ProtectedContentException
     * @throws IOException
     */
    public void nextLevel()
        throws ProtectedContentException, IOException {
        levelCompleteDialog.hide();
        levelNumber++;
        lives++;
        loadLevel();
    }

    public boolean isGameOver() {
        return gameOverDialog.isVisible();
    }

    public boolean isLevelComplete() {
        return levelCompleteDialog.isVisible();
    }

    public final void setViewportSize(int w, int h) {
        viewportWidth = w;
        viewportHeight = h;
    }

    /**
     * Returns volume of sound produced by entity depending on distance.
     *
     * @param e Game entity producing sound
     * @return volume of the sound
     */
    public int getVolume(Entity e) {
        int x = e.getCenterX();
        int dx = viewportLeft - x;
        if (dx < 0) {
            dx = x - viewportLeft - viewportWidth;
            if (dx < 0) {
                dx = 0;
            }
        }
        int y = e.getCenterY();
        int dy = viewportTop - y;
        if (dy < 0) {
            dy = y - viewportTop - viewportHeight;
            if (dy < 0) {
                dy = 0;
            }
        }
        if (dx > soundPadding || dy > soundPadding) {
            return 0;
        }
        return 100 - 100 * (dx ^ 2 + dy ^ 2) / (2 * soundPadding ^ 2);
    }

    /**
     * Gets the state of the current game (to be saved).
     *
     * @return The game state as bytes
     */
    public byte[] getState() {
        ByteArrayOutputStream bout = null;
        try {
            bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            dout.writeBoolean(Main.isTrial());
            dout.writeInt(levelNumber);
            dout.writeInt(lives);
            dout.writeInt(score);
            dout.writeInt(highScore);
            dout.writeInt(enemiesInReserve);
            level.writeTo(dout);
            player.writeTo(dout);
            enemyManager.writeTo(dout);
            base.writeTo(dout);
            bonusManager.writeTo(dout);
            treeManager.writeTo(dout);
            dout.writeBoolean(isGameOver());
            dout.writeBoolean(isLevelComplete());
            dout.writeBoolean(soundsEnabled);
            return bout.toByteArray();
        }
        catch (IOException e) {
        }
        finally {
            try {
                if (bout != null) {
                    bout.close();
                }
            }
            catch (IOException e) {
                // Nothing to do here.
            }
        }
        return new byte[0];
    }

    /**
     * Loads a previously saved game state and updates the game according to it.
     *
     * @param record Previously saved game state as bytes
     * @return true if loading was successful, false otherwise
     */
    public boolean load(byte[] record) {
        if (record == null) {
            return false;
        }
        try {
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(
                record));
            Main.setTrial(din.readBoolean());
            levelNumber = din.readInt();
            lives = din.readInt();
            score = din.readInt();
            highScore = din.readInt();
            enemiesInReserve = din.readInt();
            level = Level.readFrom(din, resources);
            createPlayer();
            player.readFrom(din);
            createEnemies();
            enemyManager.readFrom(din);
            createBase();
            base.readFrom(din);
            createManagers();
            bonusManager.readFrom(din);
            treeManager.readFrom(din);
            treeManager.refresh();
            if (din.readBoolean()) {
                showGameOverDialog();
            }
            if (din.readBoolean()) {
                showLevelCompleteDialog();
            }
            soundsEnabled = din.readBoolean();
            hud.updateLives(lives);
            hud.updateScore(score);
            hud.updateEnemies(getNumberOfEnemies());
            return true;
        }
        catch (IOException e) {
            // Nothing to do here.
        }
        return false;
    }

    private void spawnTank(Tank tank) {
        if (tank == player) {
            if (lives == 0) {
                showGameOverDialog();
                return;
            }
            player.spawn();
            lives--;
            hud.updateLives(lives);
        }
        else {
            if (enemiesInReserve == 0) {
                return;
            }
            ((Enemy) tank).spawn(Levels.getEnemyType(levelNumber,
                enemiesInReserve));
            enemiesInReserve--;
        }
    }

    private void loadLevel()
        throws ProtectedContentException, IOException {
        this.enemiesInReserve = Levels.getTotalEnemies(levelNumber);
        level = Level.load(levelNumber, resources);
        createBase();
        createPlayer();
        createEnemies();
        createManagers();
        hud.updateScore(score);
        hud.updateEnemies(getNumberOfEnemies());
        hud.updateLeftButton(Hud.NONE);
        startPlaying();
    }

    private int getNumberOfEnemies() {
        return enemiesInReserve + enemyManager.numberOfAlive();
    }

    private void createBase() {
        base = new Base(level, resources);
    }

    private void createPlayer() {
        player = new Player(level, resources, createTankListener());
    }

    private void createEnemies() {
        enemyManager = new EnemyManager(level, Levels.getConcurrentEnemies(
            levelNumber), resources, createTankListener());
    }

    private Tank.Listener createTankListener() {
        return new Tank.Listener() {

            public void destroyed(Tank tank) {
                spawnTank(tank);
            }

            public boolean collidesWithAnything(Tank tank, int x, int y, int w,
                int h) {
                if (level.collides(x, y, w, h) || base.collidesWith(x, y, w, h)) {
                    return true;
                }
                if (tank != player && player.collidesWith(x, y, w, h)) {
                    return true;
                }
                if (enemyManager.collidesWith(tank, x, y, w, h)) {
                    return true;
                }
                return false;
            }

            public boolean isInWater(Tank tank) {
                return level.isInWater(tank);
            }

            public void fireBullet(Tank tank) {
                bulletManager.fire(tank);
                AudioManager.bufferEffect(AudioManager.CANNON, getVolume(tank)
                    / 2);
            }
        };
    }

    private void showGameOverDialog() {
        levelCompleteDialog.hide();
        hud.updateLeftButton(Hud.NEWGAME);
        bonusManager.stopSpawning();
        if (!gameOverDialog.isVisible()) {
            highScore = Math.max(score, highScore);
            gameOverDialog.highScore = highScore;
            gameOverDialog.show(score, levelNumber);
        }
    }

    private Bullet.Listener createBulletListener() {
        return new Bullet.Listener() {

            public boolean collide(Bullet bullet) {
                boolean collided = false;
                if (level.collideAndDestroy(bullet)) {
                    collided = true;
                }
                else if (base.collidesWith(bullet)) {
                    destroyBase();
                    collided = true;
                }
                else if (player.collidesWith(bullet)) {
                    destroyPlayer();
                    collided = true;
                }
                else {
                    Enemy enemy = enemyManager.collidesWith(bullet);
                    if (enemy != null) {
                        collided = true;
                        if (bullet.firedBy == player) {
                            destroyEnemy(enemy);
                        }
                    }
                }
                if (collided) {
                    explosionManager.show(bullet);
                    AudioManager.bufferEffect(AudioManager.EXPLOSION_SMALL,
                        getVolume(bullet));
                }
                return collided;
            }
        };
    }

    private Bonus.Listener createBonusListener() {
        return new Bonus.Listener() {

            public boolean collidesWithPlayer(Bonus bonus) {
                if (player.isAlive() && player.collidesWith(bonus)) {
                    switch (bonus.getType()) {
                        case Bonus.AMMO:
                            player.superAmmo();
                            break;
                        case Bonus.CLOCK:
                            enemyManager.freezeEnemies();
                            break;
                        case Bonus.GRENADE:
                            Enemy[] enemies = enemyManager.all();
                            for (int i = 0; i < enemies.length; i++) {
                                destroyEnemy(enemies[i]);
                            }
                            break;
                        case Bonus.LIFE:
                            lives++;
                            hud.updateLives(lives);
                            break;
                        case Bonus.SHOVEL:
                            base.protect();
                            break;
                        case Bonus.STAR:
                            player.superTank();
                            break;
                    }
                    return true;
                }
                return false;
            }
        };
    }

    private void destroyBase() {
        if (base.destroy()) {
            explosionManager.show(base);
            AudioManager.bufferEffect(AudioManager.EXPLOSION_LARGE, getVolume(
                base));
            showGameOverDialog();
        }
    }

    private void destroyPlayer() {
        if (player.destroy()) {
            explosionManager.show(player);
            AudioManager.bufferEffect(AudioManager.EXPLOSION_LARGE, 0);
        }
    }

    private void destroyEnemy(Enemy enemy) {
        if (enemy.destroy()) {
            explosionManager.show(enemy);
            AudioManager.bufferEffect(AudioManager.EXPLOSION_LARGE, getVolume(
                enemy));
            hud.updateEnemies(getNumberOfEnemies());
            if (!isGameOver()) {
                score += enemy.getPoints();
                if (hud.getEnemies() == 0) {
                    score += lives * POINTS_PER_LIFE;
                    showLevelCompleteDialog();
                }
                hud.updateScore(score);
            }
        }
    }

    private void startPlaying() {
        spawnTank(player);
        Enemy[] enemies = enemyManager.all();
        for (int i = 0; i < enemies.length; i++) {
            spawnTank(enemies[i]);
        }
    }

    private void clearScreen(Graphics g) {
        g.setColor(0x00000000);
        g.fillRect(0, 0, viewportWidth, viewportHeight);
    }

    private void refreshViewport() {
        setViewportCenter(player.getCenterX(), player.getCenterY());
    }

    private void setViewportCenter(int x, int y) {
        final int maxLeft = level.widthInPixels - viewportWidth;
        viewportLeft = maxLeft > 0 ? Math.max(Math.min(x - viewportWidth / 2,
            maxLeft), 0) : maxLeft / 2;
        final int maxTop = level.heightInPixels - viewportHeight;
        viewportTop = maxTop > 0 ? Math.max(Math.min(y - viewportHeight / 2,
            maxTop), 0) : maxTop / 2;
        layerManager.setViewWindow(viewportLeft, viewportTop, maxLeft > 0
            ? viewportWidth : level.widthInPixels - viewportLeft, maxTop > 0
            ? viewportHeight : level.heightInPixels - viewportTop);
    }

    private void showLevelCompleteDialog() {
        bonusManager.stopSpawning();
        hud.updateLeftButton(Hud.CONTINUE);
        if (!levelCompleteDialog.isVisible()) {
            levelCompleteDialog.show(score, levelNumber);
        }
    }

    private void createManagers() {
        final int numberOfTanks = 1 + level.getEnemySpawnPointsLength();
        bulletManager = new BulletManager(2 * numberOfTanks, resources,
            createBulletListener());
        explosionManager = new ExplosionsManager(2 * numberOfTanks, resources);
        bonusManager = new BonusManager(level, resources, createBonusListener());
        treeManager = new TreeManager(level, resources);
        layerManager = new LayerManager();
        treeManager.appendTo(layerManager);
        treeManager.refresh();
        bonusManager.appendTo(layerManager);
        explosionManager.appendTo(layerManager);
        layerManager.append(base.getSprite());
        layerManager.append(player.getSprite());
        enemyManager.appendTo(layerManager);
        bulletManager.appendTo(layerManager);
        layerManager.append(level.getWallLayer());
        layerManager.append(level.getGroundLayer());
    }
}
