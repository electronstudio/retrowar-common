RetroWar-common
***************

RetroWar-common is a Java library that extends LibGDX.  It contains all the common functions that are shared
between games developed for the RetroWar project.  However it will be useful for non-RetroWar games, game jam games, etc
.  Most programmers seem to develop their own libraries of common code after doing several games, and this is mine!

# Features

* **Game** class that integrates with RetroWar.  If your game subclasses this, it will be compatible with RetroWar
and able to be added to RetroWar as a mini-game.

* A title screen and menu that can be used for any stand-alone game.

* A Frame Buffer Object renderer that draws everything to an FBO before drawing the FBO to the screen.
  * Allows you to set a virtual resolution for your game so no sub-pixels are rendered.
  * Options for scaling and stretching, and automatically centers camera no matter display aspect ratio.
  * Can change resolution on the fly to do SNES zoom effects.
  * Can apply shaders for CRT effects.

* Chiptune music players

* Standardized input devices
  * Players can join game at any time.
  * Supports keyboard, keyboard+mouse, or controller transparently.
  * PS4 and Xbox controllers mapped correctly on Windows, Mac, Linux.

* Menus

