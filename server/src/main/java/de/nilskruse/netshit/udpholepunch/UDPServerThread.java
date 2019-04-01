package de.nilskruse.netshit.udpholepunch;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.Deque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UDPServerThread extends Thread
{

    private static final Logger LOG = LoggerFactory.getLogger(UDPServerThread.class);

    private DatagramPacket packet;
    private DatagramSocket socket;
    private Deque<String> cmdQueue = new ArrayDeque<>();
    private InetAddress clientAddress;
    private int clientPort;

    public UDPServerThread(DatagramPacket packet, DatagramSocket socket)
    {
        this.packet = packet;
        this.socket = socket;
        this.clientAddress = packet.getAddress();
        this.clientPort = packet.getPort();
        LOG.info("New client registered: {}:{}", clientAddress, clientPort);


    }

    private void keepAlive()
    {
        String msgString = "UHP~~1";
        byte[] msg = msgString.getBytes();
        DatagramPacket sPacket = new DatagramPacket(msg, msg.length, clientAddress, clientPort);
        Sender.getInstance().sendPacket(sPacket);

    }

    @Override
    public void run()
    {
        if (cmdQueue.isEmpty())
        {
            keepAlive();
        } else
        {
            switch (cmdQueue.poll())
            {
                default:
                    // keep alive
                    break;
            }
        }

        try
        {
            Thread.sleep(500);
        } catch (InterruptedException e)
        {
            LOG.info("Interrupted! : ", e);
            this.interrupt();
        }
    }

}
