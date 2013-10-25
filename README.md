Battle Tank 
===========

This application demonstrates the usage of Java ME gaming features: sprites, tiles 
etc. Additionally, a reusable game structure is introduced where resources are
loaded according to the screen size of device. In the game the player's objective
is to defend the base and destroy all enemy tanks.

This example application is hosted in GitHub:
https://github.com/nokia-developer/battle-tank

This example application demonstrates:
* using Java ME gaming features such as sprites and tiles
* resusable game structure
* using different resources for different screen sizes

1. Project structure and implementation
-------------------------------------------------------------------------------
Prerequisites

* Java ME basics
* Java ME threads and timers

1.1 Important files and classes
-------------------------------------------------------------------------------
* `src\..\BattleTankCanvas.java`
* `src\..\Game.java`
* `src\..\Resources.java`
* `src\..\Level.java`

1.2 Used J2ME classes
-------------------------------------------------------------------------------
Classes: GameCanvas, Vector, Graphics, Image, Sprite, TiledLayer.

1.3 Design considerations
-------------------------------------------------------------------------------
There is a game loop running in a thread which force-sleeps at least 1 ms 
between loops to ensure that UI thread events are caught. The thread is paused when a 
screensaver starts, there is a incoming call, or so on, to prevent excess power 
consumption.

Image resources are pre-scaled for three different resolutions: high
(360x640), medium (240x320), and low (128x160).

The state of the game is stored when the user closes the application. The user 
can resume to the saved state when returning to play again.

2. Compatibility
-------------------------------------------------------------------------------
Nokia Asha software platform and Series 40 phones supporting CLDC 1.1, MIDP 2.0
and Mobile Media API (JSR-135)

Tested on:
Nokia Asha 501, Nokia Asha 311, Nokia Asha 308, Nokia Asha 306, Nokia Asha 305,
Nokia Asha 303, Nokia Asha 302, Nokia Asha 203, Nokia Asha 202
and Nokia Asha 201

Developed with:
* Netbeans 7.1.2
* Nokia Asha SDK 1.0

2.1 Known issues
----------------
Mixing sounds does not work in Symbian phones.

3. Building, installing, and running the application
-------------------------------------------------------------------------------
The example has been created with NetBeans 7.1.2 and Nokia SDK 2.0.
The project can be easily opened in NetBeans by selecting 'Open Project' 
from the File menu and selecting the application. 

Before opening the project, make sure the Series 40 6th Edition, FP1 SDK or newer is 
installed and added to NetBeans. Building is done by selecting 'Build main 
project'.

Installing the application on a phone can be done by transfering the JAR file 
via Nokia Suite or via Bluetooth.

The example can also be built using Eclipse.

3.1 Running the application
-------------------------------------------------------------------------------
From the menu, the player can resume to the latest game, start a new game,
set sounds on/off, and exit the game.

The player's objective is to destroy all enemy tanks and not let them destroy the
base. The player can collect various bonus items which have different effects: 
make the player's tank indestructible for a short time, destroy all enemy tanks, 
etc.

The top bar shows the number of player's tanks, current score points, and
enemies left in the current level. The menu button is in the bottom-right
corner.

4.1 Version history
-------------------------------------------------------------------------------

* 1.4 Nokia Asha software platform support added, In-app purchasing removed. 
  Fixed menu navigation for non touch devices
* 1.3 Ported for FT devices
* 1.2 Added restoration
* 1.1 In-app purchase Release
* 1.0 First release