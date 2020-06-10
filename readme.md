
# About

This is a cordova plugin that lets an Android phone connect via Bluetooth to the Raspberri PI-based version of the Kundalini Piano Mirror. (The Raspberri PI Piano Mirror has runs a bluetooth server that acceptings incoming commands.)

I developed this code mostly to learn how to create a cordova plugin.

# Notes

* No Bluetooth or protocol information is exposed to Javascript; instead all BT and protocol information is handled in native code, so that the UI doesn't have to worry about it. (So from Cordova, you basically just have to connect and then set the mode you need.)

* The Python server code (which runs on the PI) is included under the reference directory, even though it is not part of this project.

# More Information

More information on the Kundalini Piano Mirror can be found at:
https://www.kundalinisoftware.com/kundalini-piano-mirror/   

More information on this Cordova Plugin can be found at: 
https://www.benjaminpritchard.org/cordova-bluetooth-plugin-for-the-kundalini-piano-mirror/

# Supported Platform
Android Only

# Installation

cordova plugin add https://github.com/BenjaminPritchard/cordova-plugin-kundalini-piano-bt

# Notes

Kundalini Piano Mirror must already be paired with the phone via BT before this library will work. (Just do it from Android settings)

# Usage

      window.kundalinipianomirror.connect(,
            function(e){ // ok },
            function(e){ // error }
      );

      window.kundalinipianomirror.setMode(mode,
            function(e){ // ok },
            function(e){ // error }
      );

Valid Transpostion Modes:
    0 - None
    1 - Left Ascending
    2 - Right Descending
    3 - Mirror Image

