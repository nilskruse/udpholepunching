package de.nilskruse.netshit.udpholepunch;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Deque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UDPServerThread extends Thread
{

    private static final Logger LOG = LoggerFactory.getLogger(UDPServerThread.class);

    DatagramPacket packet;
    DatagramSocket socket;
    Deque<String> cmdQueue;
    InetAddress clientAddress;
    int clientPort;

    public UDPServerThread(DatagramPacket packet, DatagramSocket socket)
    {
        this.packet = packet;
        this.socket = socket;
        this.clientAddress = packet.getAddress();
        this.clientPort = packet.getPort();
        LOG.info("New client registered:{}:{}", clientAddress, clientPort);
    }

    @Override
    public void run()
    {

    }

}
