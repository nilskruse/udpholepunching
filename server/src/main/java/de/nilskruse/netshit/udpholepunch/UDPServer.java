package de.nilskruse.netshit.udpholepunch;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
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
        LOG.info("Server main registry loop started");

        try (DatagramSocket socket = new DatagramSocket(5123))
        {
            while (running)
            {
                DatagramPacket packet = new DatagramPacket(new byte[1000], 1000);

                socket.receive(packet);
                String msg = new String(packet.getData());
                LOG.info("Received Message: {}", msg);

                accept(socket, packet, msg);

            }
        } catch (Exception e)
        {
            LOG.error("Error: ", e);
        }
    }

    private void accept(DatagramSocket socket, DatagramPacket packet, String msg)
    {
        LOG.info("test1");
        try (Scanner sc = new Scanner(msg).useDelimiter("~~");)
        {

            LOG.info("test2");
            if (sc.next().equals("UHP"))
            {
                if (sc.next().equals("0"))
                {
                    LOG.info("test3");
                    UDPServerThread tempThread = new UDPServerThread(packet, socket);
                    tempThread.start();
                    sessions.add(tempThread);
                }
            }
        } catch (Exception e)
        {
            LOG.error("Error: ", e);

        }
    }

}
