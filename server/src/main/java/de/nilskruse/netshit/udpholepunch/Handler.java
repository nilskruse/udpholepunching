package de.nilskruse.netshit.udpholepunch;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler extends Thread {
	private static final Logger LOG = LoggerFactory.getLogger(Handler.class);
	DatagramPacket packet;

	public Handler(DatagramPacket packet) {
		this.packet = packet;
	}

	@Override
	public void run() {
		String msg = new String(packet.getData()).trim();
		LOG.info("Handler received Message: {}", msg);

		try (Scanner sc = new Scanner(msg).useDelimiter("~~");) {
			if (sc.next().equals("UHP")) {
				switch (sc.next()) {
					case "1" :
						handleKeepAlive();
						break;
					case "5" :
						handleKeepAlive();
						handleRequestOtherClient();
						break;
					default :
						break;
				}
			}
		} catch (

		Exception e) {
			LOG.error("Error: ", e);
		}
	}

	private void handleRequestOtherClient() {
		if (UDPServer.getSessions().size() < 2) {
			String msgString = "UHP~~7~~";
			byte[] msg = msgString.getBytes();
			DatagramPacket sPacket = new DatagramPacket(msg, msg.length, packet.getAddress(), packet.getPort());
			Sender.getInstance().sendPacket(sPacket);
		} else {
			InetAddress ia = packet.getAddress();
			int port = packet.getPort();
			UDPServerThread client1 = null;
			UDPServerThread client2 = null;
			client1 = getFirstClient(ia, port, client1);
			client2 = getSecondClient(ia, port, client2);

			if (client1 == null || client2 == null) {
				return;
			}
			String msg1String = "UHP~~6~~" + client1.getClientAddress().toString().substring(1) + "~~" + client1.getClientPort() + "~~";
			String msg2String = "UHP~~6~~" + client2.getClientAddress().toString().substring(1) + "~~" + client2.getClientPort() + "~~";

			byte[] msg1 = msg1String.getBytes();
			byte[] msg2 = msg2String.getBytes();
			DatagramPacket sPacket1 = new DatagramPacket(msg1, msg1.length, client2.getClientAddress(), client2.getClientPort());
			DatagramPacket sPacket2 = new DatagramPacket(msg2, msg2.length, client1.getClientAddress(), client1.getClientPort());
			Sender.getInstance().sendPacket(sPacket1);
			Sender.getInstance().sendPacket(sPacket2);

			client1.halt();
			client2.halt();

			UDPServer.getSessions().remove(client1);
			UDPServer.getSessions().remove(client2);
		}
	}

	private UDPServerThread getSecondClient(InetAddress ia, int port, UDPServerThread client2) {
		for (UDPServerThread session : UDPServer.getSessions()) {
			if (!(session.getClientAddress().equals(ia) && session.getClientPort() == port)) {
				client2 = session;
			}
		}
		return client2;
	}

	private UDPServerThread getFirstClient(InetAddress ia, int port, UDPServerThread client1) {
		for (UDPServerThread session : UDPServer.getSessions()) {
			if (session.getClientAddress().equals(ia) && session.getClientPort() == port) {
				client1 = session;
			}
		}
		return client1;
	}
	private void handleKeepAlive() {
		InetAddress ia = packet.getAddress();
		int port = packet.getPort();

		for (UDPServerThread session : UDPServer.getSessions()) {
			if (session.getClientAddress().equals(ia) && session.getClientPort() == port) {
				session.setAlive();
				LOG.info("{}:{} set alive", ia, port);
			}
		}

	}

}
