"use strict";

var exec = require("cordova/exec");

var kundalinipianomirror = {

connect: function(sc, ec) {
    exec(sc, ec, "PianoMirrorBTProtocolCordova", "connect", []);
},

setMode: function(mode, sc, ec) {
    exec(sc, ec, "PianoMirrorBTProtocolCordova", "setMode", [mode]);
}

};

module.exports = kundalinipianomirror;