package de.nilskruse.netshit.udpholepunch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sender {
    private static final Logger LOG = LoggerFactory.getLogger(Sender.class);

    private DatagramSocket socket;

    public DatagramSocket getSocket() {
	return socket;
    }

    private static Sender instance = null;

    public static Sender getInstance() {
	if (instance == null) {
	    instance = new Sender();
	}
	return instance;
    }

    public Sender() {
	try {
	    socket = new DatagramSocket(5200);
	} catch (SocketException e) {
	}
    }

    public void sendPacket(DatagramPacket packet) {
	try {
	    socket.send(packet);
	    // LOG.info("Sent packet: {} to {}:{}", new String(packet.getData()),
	    // packet.getAddress(),packet.getPort());
	} catch (IOException e) {

	}
    }
}
