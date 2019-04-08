package de.nilskruse.netshit.udpholepunch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainClassClient {
	private static final Logger LOG = LoggerFactory.getLogger(MainClassClient.class);

	public static void main(String[] args) {
		InetAddress ia = null;
		try {
			ia = InetAddress.getByName("82.165.115.188");
			UDPClient client = new UDPClient(ia, 5123);
			client.start();
		} catch (UnknownHostException e) {
			LOG.error("Error: ", e);
		}

	}

}
