package com.kundalini.android.pianomirror;

//
// Kundalini Piano Mirror Bluetooth interface routines, to be exported to Javascript
// for calling from the UI
//
// History
// 21-Dec-2019:     Initial Release; only two commands implemented
//
// Benjamin Pritchard / Kundalini Software
//
//

import android.content.Context;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PianoMirrorBTProtocolCordova extends CordovaPlugin {

    public static Context thisContext;
    private PianoMirrorBTProtocol PianoMirror;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        thisContext = cordova.getActivity().getApplicationContext();
        PianoMirror = new PianoMirrorBTProtocol(thisContext);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("connect")) {
            PianoMirror.connect();
            callbackContext.success();
            return true;
        }

        if (action.equals("setMode")) {
            PianoMirror.sendSendModeChangeCommand(Integer.parseInt(args.get(0).toString()));
            callbackContext.success();
            return true;
        }

        return false;
    }

}

