package com.kundalini.android.pianomirror;

//
// Protocol code for sending commands and getting responses via Bluetooth from the
// Kundalini Piano Mirror
//
// The methods in here are exposed via Cordova to Javascript, so that an
// HTML user interface can interact with the Piano Mirror at a high level
//
// History
// 21-Dec-2019:     Initial Release; only two commands implemented
//
// Benjamin Pritchard / Kundalini Software
//
//

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.Set;
import java.util.UUID;

//  all of our messages are 8 bytes long, and have the following format:
//  0       - START_OF_MESSAGE
//  1       - message type
//  2       - command number
//  2..6    - data
//  7       - END_OF_MESSAGE
public class PianoMirrorBTProtocol {

    // transposition modes the embedded device supports
    public static final int TRANSPOSITION_NONE = 0;
    public static final int TRANSPOSITION_LEFTASCENDING = 1;
    public static final int TRANSPOSITION_RIGHTDESCENDING = 2;
    public static final int TRANSPOSITION_MIRROR = 3;

    // commands the Kundalini Piano Mirror supports
    private static final int COMMAND_NOCOMMAND = -1;
    private static final int COMMAND_SETTRANSPOSITIONMODE = 20;
    private static final int COMMAND_GETHARDWAREVERSION = 21;

    // magic values to indicate the start / end of a message
    private static final byte START_OF_MESSAGE = 42;
    private static final byte END_OF_MESSAGE = 43;

    // message type
    private static final byte COMMAND = 100;
    private static final byte NO_RESPONSE = -1;
    private static final byte RESPONSE_OK = 101;
    private static final byte RESPONSE_NAK = 102;
    private static final byte RESPONSE_BAD_PARAM = 103;

    // shared UUID between the python bluetooth service on the Raspberri PI, and this code...
    // this has to match in order for connections to work
    private static final UUID SerialPortServiceClass_UUID = UUID.fromString("baae2a87-e21e-47bb-903d-ae30fa61d548");

    private BluetoothAdapter mBluetoothAdapter = null;
    private static BluetoothSerialService mSerialService = null;

    // these variables will be filled in by the commands we get back from the raspberry pi
    private String m_HardwareVersion = "";

    // which command we are currently waiting to get back data from...
    private int m_outStandingCommand = COMMAND_NOCOMMAND;
    private int m_LastCommandResult = NO_RESPONSE;

    private String m_address  = "";

    // handler that gets called when we receive data via Bluetooths
    private  final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothSerialService.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    decodeReceivedCommand(readBuf);
                    break;
            }
        }
    };

    public int return42() {
        return 42;
    }

    public PianoMirrorBTProtocol(Context context) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mSerialService = new BluetoothSerialService(context, mHandler, SerialPortServiceClass_UUID);
    }

    public String HardwareVersion() {
        return m_HardwareVersion;
    }

    public int LastCommandResult() {
        return m_LastCommandResult;
    }

    public String LastCommandResultAsString() {
        String retVal = "";

        switch (m_LastCommandResult) {
            case NO_RESPONSE: retVal = "No Reponse"; break;
            case RESPONSE_OK: retVal = "OK"; break;
            case RESPONSE_NAK: retVal = "NAK"; break;
            case RESPONSE_BAD_PARAM: retVal = "Bad Param"; break;
        }

        return retVal;
    }

    public void connect() {

        if (m_address.equals("")) {
            if (!PianoMirrorIsBonded()) return;
        }

        // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(m_address);

        // Attempt to connect to the device
        mSerialService.connect(device);
    }

    public boolean BTIsEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    // returnd true if the android devices is paired with the piano mirror
    public boolean PianoMirrorIsBonded() {

        if (!mBluetoothAdapter.isEnabled()) return false;

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("KundaliniPiano")) {
                    m_address = device.getAddress();                    // remember the MAC address of the device...
                    return true;
                }
            }
        }

        return false;
    }

    // returns true if we currently have an RFComm channel connected to the remote device
    public boolean Connected() {
        return mSerialService.getState() == BluetoothSerialService.STATE_CONNECTED;
    }

    // sends a command to the piano mirror requesting a certain transposition mode
    public void sendSendModeChangeCommand(int newMode) {

        // send over a command to switch modes...
        byte[] buffer = new byte[8];
        byte commandCode = 0;

        switch (newMode) {
            case TRANSPOSITION_NONE:
                commandCode = 48;
                break;
            case TRANSPOSITION_LEFTASCENDING:
                commandCode = 49;
                break;
            case TRANSPOSITION_RIGHTDESCENDING:
                commandCode = 50;
                break;
            case TRANSPOSITION_MIRROR:
                commandCode = 51;
                break;
        }

        if (commandCode !=0 && this.Connected()) {

            buffer[0] = START_OF_MESSAGE;
            buffer[1] = COMMAND;
            buffer[2] = COMMAND_SETTRANSPOSITIONMODE;
            buffer[3] = commandCode;
            buffer[4] = 2;
            buffer[5] = 3;
            buffer[6] = 4;
            buffer[7] = END_OF_MESSAGE;

            m_outStandingCommand = COMMAND_SETTRANSPOSITIONMODE;

            mSerialService.write(buffer);
        }
    }

    // sends a command to the piano mirror asking for its version number
    // once we get the response back, we will fill in
    public void     sendGetHardwareVersion() {

        // send over a command to switch modes...
        byte[] buffer = new byte[8];

        if (this.Connected()) {

            buffer[0] = START_OF_MESSAGE;
            buffer[1] = COMMAND;
            buffer[2] = COMMAND_GETHARDWAREVERSION;
            buffer[3] = 1;
            buffer[4] = 2;
            buffer[5] = 3;
            buffer[6] = 4;
            buffer[7] = END_OF_MESSAGE;

            m_outStandingCommand = COMMAND_GETHARDWAREVERSION;

            mSerialService.write(buffer);
        }
    }

    private void decodeReceivedCommand(byte[] buffer) {

        switch (m_outStandingCommand) {
            case COMMAND_SETTRANSPOSITIONMODE:
                if (buffer[0] == START_OF_MESSAGE && buffer[1] == COMMAND_SETTRANSPOSITIONMODE && buffer[7] == END_OF_MESSAGE)  {
                    m_LastCommandResult = buffer[2];
                }
            case COMMAND_GETHARDWAREVERSION:
                if (buffer[0] == START_OF_MESSAGE && buffer[1] == COMMAND_GETHARDWAREVERSION && buffer[7] == END_OF_MESSAGE)  {
                    m_LastCommandResult = buffer[2];
                    m_HardwareVersion =
                            Character.toString((char)buffer[3]) +
                            Character.toString((char)buffer[4]) +
                            Character.toString((char)buffer[5]) +
                            Character.toString((char)buffer[6]);
                }
        }

        m_outStandingCommand = COMMAND_NOCOMMAND;

    }

}
