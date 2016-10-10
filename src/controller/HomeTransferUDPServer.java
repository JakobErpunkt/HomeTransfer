package controller;

import model.HomeTransferModel;
import model.HomeTransferServerData;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by jakobriga on 05.12.15.
 */
public class HomeTransferUDPServer implements Runnable{

    private DatagramSocket socket;
    private boolean running;

    public void run() {

        running = true;

        try {

            socket = new DatagramSocket(1138, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while(running) {
                //Receive a packet
                byte[] recvBuf = new byte[1500];
                DatagramPacket packet = new DatagramPacket(
                        recvBuf,
                        recvBuf.length);
                socket.receive(packet);

                // TODO: comment debug output out
                System.out.println("adress:" + packet.getAddress());
                System.out.println("port:" + packet.getPort());
                System.out.println("socketAdress:" + packet.getSocketAddress());
                System.out.println("data:" + new String(packet.getData()).trim());
                System.out.println("");

                HomeTransferServerData data = HomeTransferServerData.createFromUDP(packet);
                if (data.type.equals("close")) {
                    HomeTransferModel.getInstance().removeServer(data);
                } else {
                    HomeTransferModel.getInstance().addServer(data);
                    if (data.type.equals("discover")) {
                        HomeTransferController.getInstance().broadcastUDP("response");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error: Failed to start UDPserver");
            e.printStackTrace();
        }

    }

    public void stop() {
        running = false;
        try {
            socket.close();
        } catch (Exception e) {
            System.out.println("Error: UDPSocket could not be closed");
            e.printStackTrace();
        }
    }

}
