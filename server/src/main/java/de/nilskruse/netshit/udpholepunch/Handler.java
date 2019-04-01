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
						handleKeepAlive(sc);
						break;
					case "5" :
						handleKeepAlive(sc);
						handleRequestOtherClient(sc);
						break;
				}
			}
		} catch (

		Exception e) {
			LOG.error("Error: ", e);
		}
	}

	private void handleRequestOtherClient(Scanner sc) {
		if (UDPServer.getSessions().size() < 2) {
			String msgString = "UHP~~7~~";
			byte[] msg = msgString.getBytes();
			DatagramPacket sPacket = new DatagramPacket(msg, msg.length, packet.getAddress(), packet.getPort());
			Sender.getInstance().sendPacket(sPacket);
		} else {
			InetAddress ia = packet.getAddress();
			int port = packet.getPort();
			UDPServerThread client1 = null, client2 = null;
			for (UDPServerThread session : UDPServer.getSessions()) {
				if (session.getClientAddress().equals(ia) && session.getClientPort() == port) {
					client1 = session;
				}
			}

			for (UDPServerThread session : UDPServer.getSessions()) {
				if (!(session.getClientAddress().equals(ia) && session.getClientPort() == port)) {
					client2 = session;
				}
			}
			String msg1String = "UHP~~6~~" + client1.getClientAddress().toString().substring(1) + "~~" + client1.getClientPort() + "~~";
			String msg2String = "UHP~~6~~" + client2.getClientAddress().toString().substring(1) + "~~" + client2.getClientPort() + "~~";
			byte[] msg1 = msg1String.getBytes();
			byte[] msg2 = msg2String.getBytes();
			DatagramPacket sPacket1 = new DatagramPacket(msg1, msg1.length, client1.getClientAddress(), client1.getClientPort());
			DatagramPacket sPacket2 = new DatagramPacket(msg2, msg2.length, client2.getClientAddress(), client2.getClientPort());
			Sender.getInstance().sendPacket(sPacket1);
			Sender.getInstance().sendPacket(sPacket2);

			client1.halt();
			client2.halt();

			UDPServer.getSessions().remove(client1);
			UDPServer.getSessions().remove(client2);
		}
	}
	private void handleKeepAlive(Scanner sc) {
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
