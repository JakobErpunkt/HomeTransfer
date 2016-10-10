package controller;

import model.HomeTransferModel;
import model.HomeTransferServerData;

import java.net.*;
import java.util.Enumeration;

/**
 * Created by jakobriga on 05.12.15.
 * Broadcasts UDP discovery messages using port 1138
 * The broadcast is only needed once when the program is started
 */
public class HomeTransferUDPClient {

    /**
     * Broadcasts a package to every device in the local network using a tag. This tag differs between the mode for
     * discovery and response.
     * @param tag is equal to either "discovery" or "response"
     */
    public static void broadcastPackage(String tag) {

        try {

            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);

            HomeTransferModel model = HomeTransferModel.getInstance();
            HomeTransferServerData local = model.getLocalHomeTransferData();
            byte[] sendData = (local.name + ";" + local.IP + ";" + local.port + ";" + tag).getBytes();

//            byte[] sendData = (name + ";" + IP + ";" + port).getBytes();

            // Try 255.255.255.255 first
            try {

                DatagramPacket sendPacket = new DatagramPacket(
                        sendData,
                        sendData.length,
                        InetAddress.getByName("255.255.255.255"),
                        1138);
                c.send(sendPacket);

            } catch (Exception e) {
                System.out.println("Error: Sending of UDP package failed");
                e.printStackTrace();
            }

            // Now Broadcast over all network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {

                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                // Prevent loopback interface
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                for (InterfaceAddress interfaceAddress: networkInterface.getInterfaceAddresses()) {

                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    // Only process if broadcast is available
                    if (broadcast == null) {
                        continue;
                    }
                    // Broadcast here
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(
                                sendData,
                                sendData.length,
                                broadcast,
                                1138
                        );
                        c.send(sendPacket);
                    } catch (Exception e) {
                        System.out.println("Error: Broadcast of UDP package failed");
                        e.printStackTrace();
                    }
                }

            }


        } catch (Exception e) {
            System.out.println("Error: Creation of UDP broadcast failed");
            e.printStackTrace();
        }

    }


}
