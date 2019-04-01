package de.nilskruse.netshit.udpholepunch;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPServer implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(UDPServer.class);
    private boolean running = true;
    private List<UDPServerThread> sess = new ArrayList<>();
    private List<UDPServerThread> sessions = Collections.synchronizedList(sess);

    public void run()
    {
        LOG.info("Server main loop started");

        try (DatagramSocket socket = new DatagramSocket(5123))
        {
            while (running)
            {
                DatagramPacket packet = new DatagramPacket(new byte[1000], 1000);
                try
                {
                    socket.receive(packet);
                    UDPServerThread tempThread = new UDPServerThread(packet, socket);
                    tempThread.start();
                    sessions.add(tempThread);


                } catch (Exception e)
                {

                }

            }
        } catch (Exception e)
        {

        }
    }

}
