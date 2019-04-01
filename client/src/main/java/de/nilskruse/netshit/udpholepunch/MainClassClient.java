package de.nilskruse.netshit.udpholepunch;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainClassClient {
	public static void main(String[] args) {
		InetAddress ia = null;
		try {
			ia = InetAddress.getByName("192.168.0.171");
			UDPClient client = new UDPClient(ia, 5123);
			new Thread(client).start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

}
