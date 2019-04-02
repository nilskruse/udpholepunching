package de.nilskruse.netshit.udpholepunch;

import java.net.DatagramPacket;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPServerThread extends Thread {

	private static final Logger LOG = LoggerFactory.getLogger(UDPServerThread.class);

	private InetAddress clientAddress;
	private int clientPort;
	private boolean running = true;
	private long lastAlive;

	public UDPServerThread(DatagramPacket packet) {
		this.clientAddress = packet.getAddress();
		this.clientPort = packet.getPort();
		LOG.info("New client registered: {}:{}", clientAddress, clientPort);
		setAlive();

	}

	private void keepAlive() {
		String msgString = "UHP~~1~~";
		byte[] msg = msgString.getBytes();
		DatagramPacket sPacket = new DatagramPacket(msg, msg.length, clientAddress, clientPort);
		Sender.getInstance().sendPacket(sPacket);

	}

	public void setAlive() {
		lastAlive = System.currentTimeMillis();
	}

	@Override
	public void run() {
		while (running) {

			keepAlive();

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				LOG.info("Interrupted! : ", e);
				this.interrupt();
			}

			if (System.currentTimeMillis() - lastAlive > 5000) {
				running = false;
				LOG.info("{}:{} died", clientAddress, clientPort);
				UDPServer.getSessions().remove(this);
			}
		}
	}

	public InetAddress getClientAddress() {
		return clientAddress;
	}

	public void halt() {
		running = false;
	}

	public int getClientPort() {
		return clientPort;
	}

}
