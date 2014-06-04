Battle Tank 
===========

This application demonstrates the usage of Java ME gaming features: sprites,
tiles etc. Additionally, a reusable game structure is introduced where resources
are loaded according to the screen size of device. In the game the player's
objective is to defend the base and destroy all enemy tanks.

In addition, the example features the Nokia In-Application Payment API.
Initially the application is a limited trial version. The user can then purchase
the full version via the main menu. This example utilises the test identifiers
for the purchasable item and buying the full version does not generate any
actual costs.

This example application demonstrates:
* Using Java ME gaming features such as sprites and tiles
* Resusable game structure
* Using different resources for different screen sizes
* Nokia In-Application Payment API

This example application is hosted in GitHub:
https://github.com/nokia-developer/battle-tank

For more information on the implementation, visit Java Developer's Library:
http://developer.nokia.com/Resources/Library/Java/#!code-examples/game-api-battletank.html


1. Important files and classes
-------------------------------------------------------------------------------

* `src\..\BattleTankCanvas.java`
* `src\..\Game.java`
* `src\..\Resources.java`
* `src\..\Level.java`


Main J2ME classes used:

* GameCanvas
* Graphics
* Image
* Sprite
* TiledLayer
* Vector


2. Design considerations
-------------------------------------------------------------------------------
There is a game loop running in a thread which force-sleeps at least 1 ms 
between loops to ensure that UI thread events are caught. The thread is paused
when a screensaver starts, there is a incoming call, or so on, to prevent excess
power consumption.

Image resources are pre-scaled for three different resolutions: high
(360x640), medium (240x320), and low (128x160).

The state of the game is stored when the user closes the application. The user 
can resume to the saved state when returning to play again.


3. Compatibility
-------------------------------------------------------------------------------

Nokia Asha software platform and Series 40 phones supporting CLDC 1.1, MIDP 2.0
and Mobile Media API (JSR-135)


3. Building, installing, and running the application
-------------------------------------------------------------------------------

The example has been created with NetBeans 7.1.2 and Nokia Asha SDK 1.2. The
project can be easily opened in NetBeans by selecting 'Open Project' from the
File menu and selecting the application. 

Before opening the project, make sure the Series 40 6th Edition, FP1 SDK or
newer is installed and added to NetBeans. Building is done by selecting 'Build
main project'.

Installing the application on a phone can be done by transfering the JAD and
JAR file via USB or Bluetooth.

The example can also be built using Eclipse.

Note that you need to add the Nokia In-Application Payment API library to the
project build path. In Eclipse, you also need to make sure the library is
exported.


4. Running the application
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


5. Version history
-------------------------------------------------------------------------------

* 1.5.1 Minor fixes.
* 1.5 Added support for new Nokia In-App Payment API.
* 1.4 Nokia Asha software platform support added, In-app purchasing removed. 
  Fixed menu navigation for non touch devices
* 1.3 Ported for FT devices
* 1.2 Added restoration
* 1.1 In-app purchase Release
* 1.0 First release
