//
// IOS objective-C code for interfacing to the Kundalin Piano Mirror
//
// Benjamin Pritchard / Kundalini Software
// 23-Dec-2019
//

#include "stdio.h"
#include "stdlib.h"
#include "string.h"
#include "assert.h"

 // transposition modes the embedded device supports
#define  TRANSPOSITION_NONE = 0;
#define  TRANSPOSITION_LEFTASCENDING = 1;
#define  TRANSPOSITION_RIGHTDESCENDING = 2;
#define  TRANSPOSITION_MIRROR = 3;

// commands the Kundalini Piano Mirror supports
#define  COMMAND_NOCOMMAND = -1;
#define  COMMAND_SETTRANSPOSITIONMODE = 20;
#define  COMMAND_GETHARDWAREVERSION = 21;

// magic values to indicate the start / end of a message
#define  END_OF_MESSAGE = 43;

// message type
#define  COMMAND = 100;
#define  NO_RESPONSE = -1;
#define  RESPONSE_OK = 101;
#define  RESPONSE_NAK = 102;
#define  RESPONSE_BAD_PARAM = 103;

const char *SerialPortServiceClass_UUID = "baae2a87-e21e-47bb-903d-ae30fa61d548";

/*
char* return UUID_as_string() {
}
*/

int main(int argc, char *argv[]) {
	printf("IOS version of Cordova Bluetooth Plugin\n");
}