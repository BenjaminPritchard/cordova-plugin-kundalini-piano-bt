
# About

This is a cordova plugin that lets an Android phone connect via Bluetooth to the Kundalini Piano Mirror. 

No Bluetooth or protocol information is exposed to Javascript; instead all BT and protocol information is handled in native code, so that the UI doesn't have to worry about it. (So from Cordova, you basically just have to connect and then set the mode you need.)

More information on the Kundalini Piano Mirror can be found at:
https://www.kundalinisoftware.com/kundalini-piano-mirror/   

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

