package de.nilskruse.netshit.udpholepunch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Receiver implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(Receiver.class);

	private boolean running = true;

	@Override
	public void run() {
		LOG.info("Server receiver loop started");
		long timer = System.currentTimeMillis();
		DatagramSocket socket = Sender.getInstance().getSocket();
		while (running) {
			DatagramPacket packet = new DatagramPacket(new byte[1000], 1000);
			try {
				socket.receive(packet);
				new Handler(packet).start();
			} catch (IOException e) {
				LOG.error("Error: ", e);
			}

		}

	}

}
