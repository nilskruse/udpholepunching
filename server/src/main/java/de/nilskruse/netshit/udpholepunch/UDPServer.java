package de.nilskruse.netshit.udpholepunch;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPServer implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(UDPServer.class);
	private boolean running = true;
	private static List<UDPServerThread> sess = new ArrayList<>();
	private static List<UDPServerThread> sessions = Collections.synchronizedList(sess);

	public static List<UDPServerThread> getSessions() {
		return sessions;
	}

	public void run() {
		LOG.info("Server main registry loop started");

		try (DatagramSocket socket = new DatagramSocket(5123)) {
			while (running) {
				DatagramPacket packet = new DatagramPacket(new byte[1000], 1000);

				socket.receive(packet);
				String msg = new String(packet.getData()).trim();
				LOG.info("Received Message: {}", msg);

				accept(packet, msg);

			}
		} catch (Exception e) {
			LOG.error("Error: ", e);
		}
	}

	private void accept(DatagramPacket packet, String msg) {
		try (Scanner sc = new Scanner(msg).useDelimiter("~~");) {
			if (sc.next().equals("UHP") && sc.next().equals("0")) {
				UDPServerThread tempThread = new UDPServerThread(packet);
				tempThread.start();
				sessions.add(tempThread);
			}
		} catch (Exception e) {
			LOG.error("Error: ", e);
		}
	}

}
