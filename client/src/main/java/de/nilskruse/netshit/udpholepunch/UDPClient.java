package de.nilskruse.netshit.udpholepunch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPClient implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(UDPClient.class);

	private boolean running = true;
	private int clientPort = new Random().nextInt(60000);
	private InetAddress serverAddress;
	private int serverPort;
	private DatagramSocket dSocket;
	private boolean getOtherClient = false;
	private boolean gotOtherClient = false;
	private long sendTimer = System.currentTimeMillis();

	private boolean canSend() {
		return System.currentTimeMillis() - sendTimer > 500;
	}
	public UDPClient(InetAddress serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		try {
			dSocket = new DatagramSocket(clientPort);
		} catch (Exception e) {

		}
	}

	@Override
	public void run() {
		long timer = System.currentTimeMillis();
		register();
		while (running) {
			DatagramPacket packet = receive();
			handle(packet);
			if (System.currentTimeMillis() - timer > 7000 && !gotOtherClient) {
				getOtherClient = true;
			}
			if (getOtherClient && !gotOtherClient) {
				requestOtherClient(packet);
				getOtherClient = false;
			}

		}

	}

	private void requestOtherClient(DatagramPacket packet) {
		respond(packet, "UHP~~5~~");
	}

	private void handle(DatagramPacket packet) {
		String msg = new String(packet.getData()).trim();
		LOG.info("Client received message {} from {}:{}", msg, packet.getAddress(), packet.getPort());

		try (Scanner sc = new Scanner(msg).useDelimiter("~~");) {
			if (sc.next().equals("UHP")) {

				switch (sc.next()) {
					case "1" :
						respond(packet, "UHP~~1~~");
						break;
					case "6" :
						setOtherClient(sc);
						sendToClient("UHP~~10~~", serverAddress, serverPort);
						break;
					case "7" :
						LOG.info("No other client availabe, sending alive packet");
						respond(packet, "UHP~~1~~");
						break;
					case "10" :
						LOG.info("Received Message from other client {}:{}", packet.getAddress(), packet.getPort());
						respond(packet, "UHP~~10~~");
						break;

				}

			}
		} catch (Exception e) {
			LOG.error("Error: ", e);
		}
	}
	private void sendToClient(String msg, InetAddress address, int port) {
		byte[] responseBuffer = msg.getBytes();
		DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, address, port);
		try {
			dSocket.send(responsePacket);
		} catch (IOException e) {
			LOG.error("Error: ", e);
		}
	}
	private void setOtherClient(Scanner sc) {
		try {
			this.serverAddress = InetAddress.getByName(sc.next());
			this.serverPort = Integer.parseInt(sc.next());
			gotOtherClient = true;
			LOG.info("Registered new client {}:{}", serverAddress, serverPort);
		} catch (UnknownHostException e) {
			LOG.error("Error: ", e);
		}
	}

	private void respond(DatagramPacket packet, String response) {
		byte[] responseBuffer = response.getBytes();
		DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, packet.getAddress(), packet.getPort());
		try {
			dSocket.send(responsePacket);
		} catch (IOException e) {
			LOG.error("Error: ", e);
		}
	}

	private DatagramPacket receive() {
		byte[] answer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(answer, answer.length);
		try {
			dSocket.receive(packet);
		} catch (IOException | NullPointerException e) {
			LOG.error("Error: ", e);
		}
		return packet;
	}

	private void register() {
		String command = "UHP~~0~~";

		byte[] buffer = command.getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);

		try {
			dSocket.send(packet);
		} catch (IOException e) {
			LOG.error("Error: {}", e);
		}
	}

}
