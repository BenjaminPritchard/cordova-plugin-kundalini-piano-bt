#!/usr/bin/python
# -*- coding: utf-8 -*-


import bluetooth

server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
server_sock.bind(("", bluetooth.PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "baae2a87-e21e-47bb-903d-ae30fa61d548"

bluetooth.advertise_service(server_sock, "PianoMirror", service_id=uuid,
                            service_classes=[uuid, bluetooth.SERIAL_PORT_CLASS],
                            profiles=[bluetooth.SERIAL_PORT_PROFILE],
                            # protocols=[bluetooth.OBEX_UUID]
                            )

print("Waiting for connection on RFCOMM channel", port)

client_sock, client_info = server_sock.accept()
print("Accepted connection from", client_info)

try:
    while True:
        data = client_sock.recv(1024)
        if not data:
            break
	
	b = bytearray(data)
	
	if (b[0] == 42 and b[7] == 43 and b[2] == 20):
	
		# reply to their message
		r = bytearray(8)
		r[0] = 42			# start of message
		r[1] = 20			# command we are replying to 
		r[2] = 101			# RESPONSE_OK
		r[3] = 0			# 0 
		r[4] = 0			# 0
		r[5] = 0			# 0
		r[6] = 0			# 0
		r[7] = 43			# end of message
		client_sock.send(r.decode("utf-8"))

		mode = 0
		if b[3] == 48: mode = "0"
		if b[3] == 49: mode = "1"
		if b[3] == 50: mode = "2"
		if b[3] == 51: mode = "3"
	
		#send a command over to the piano mirror telling it to update the transposition mode...
		with open('piano_mirror.pipe', 'w') as f:
			f.write(mode + "/n")
	
	if (b[0] == 42 and b[7] == 43 and b[2] == 21):
		r = bytearray(8)
		r[0] = 42			# start of message
		r[1] = 21			# command we are replying to 
		r[2] = 101			# RESPONSE_OK
		r[3] = 48			# 0 
		r[4] = 48			# 0
		r[5] = 48			# 0
		r[6] = 49			# 1 (to be incremented?)
		r[7] = 43			# end of message
		client_sock.send(r.decode("utf-8"))

except IOError:
    pass

print("Disconnected.")

client_sock.close()
server_sock.close()
print("All done.")
