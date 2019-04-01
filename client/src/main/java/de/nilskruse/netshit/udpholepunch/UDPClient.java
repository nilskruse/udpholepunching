package de.nilskruse.netshit.udpholepunch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

public class UDPClient implements Runnable
{

    private boolean running = true;
    private int clientPort = new Random().nextInt(60000);
    private InetAddress serverAddress;
    private int serverPort;

    public UDPClient(InetAddress serverAddress, int serverPort)
    {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void run()
    {
        try (DatagramSocket dSocket = new DatagramSocket(clientPort))
        {

            try
            {
                String command = "UHP~~0~~";

                byte buffer[] = command.getBytes();
                DatagramPacket packet =
                        new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);

                dSocket.send(packet);



                while (running)
                {

                    byte answer[] = new byte[1024];
                    packet = new DatagramPacket(answer, answer.length);
                    try
                    {
                        dSocket.receive(packet);
                    } catch (SocketTimeoutException | NullPointerException e)
                    {
                        dSocket.send(packet);
                        continue;
                    }

                    String msg = new String(packet.getData());
                    Thread.sleep(500);

                }
            } catch (IOException e1)
            {
                e1.printStackTrace();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        } catch (SocketException e1)
        {
            e1.printStackTrace();
        }

    }

}
