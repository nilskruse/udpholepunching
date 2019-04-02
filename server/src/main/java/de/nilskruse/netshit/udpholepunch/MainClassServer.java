package de.nilskruse.netshit.udpholepunch;

public class MainClassServer {
	public static void main(String[] args) {
		UDPServer server = new UDPServer();
		new Thread(server).start();

		Receiver receiver = new Receiver();
		new Thread(receiver).start();
		// test
	}
}
