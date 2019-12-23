"use strict";

var exec = require("cordova/exec");

var kundalinipianomirror = {
	connect: function(txt, duration, sc, ec) {
		exec(sc, ec, "PianoMirrorBTProtocolCordova", "connect", [txt, duration]);
	}
};

module.exports = kundalinipianomirror;