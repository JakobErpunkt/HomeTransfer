package model;

import java.net.DatagramPacket;

/**
 * Created by jakobriga on 02.12.15.
 * A simple struct-like class for storing server data like IP and port number
 */
public class HomeTransferServerData {

    public String name;
    public String IP;
    public int port;
    public String type;

    public static HomeTransferServerData createFromUDP(DatagramPacket packet) {
        HomeTransferServerData data = new HomeTransferServerData();
        String packetData = new String(packet.getData()).trim();
        String packetDataSplitted[] = packetData.split(";");
        data.name = packetDataSplitted[0];
        data.IP = packetDataSplitted[1];
        data.port = Integer.parseInt(packetDataSplitted[2]);
        data.type = packetDataSplitted[3];
        return data;
    }

}
