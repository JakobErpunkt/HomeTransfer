package controller;

import model.HomeTransferModel;
import model.HomeTransferServerData;
import view.HomeTransferView;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by jakobriga on 01.12.15.
 */
public class HomeTransferController implements Observer {

    private static HomeTransferController controller;
    private HomeTransferServer homeTransferServer;
    private HomeTransferUDPServer homeTransferUDPServer;

    /**
     * Constructor
     * Sets up the server privately
     */
    private HomeTransferController() {
    }

    public void initController() {
        try {
            homeTransferServer = new HomeTransferServer(HomeTransferModel.getInstance().getLocalHomeTransferData().port);
            homeTransferUDPServer = new controller.HomeTransferUDPServer();
            new Thread(homeTransferUDPServer).start();
            new Thread(homeTransferServer).start();
        } catch (Exception e) {
            System.out.println("Error: Server could not be created");
            e.printStackTrace();
        }
    }

    public static HomeTransferController getInstance() {
        if (controller == null) {
            System.out.println("Controller is made");
            controller = new HomeTransferController();
        }
        return controller;
    }

    /**
     * Called by the model when there appears to be a new Server in range.
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        updateGUI();
    }

    /**
     * When there is a change in the list of servers then the GUI has to be updated
     */
    public void updateGUI() {
        HomeTransferView view = HomeTransferView.getInstance();
        view.setServerData(HomeTransferModel.getInstance().getServers());
        view.setFileData(HomeTransferModel.getInstance().getFilesNames());
    }

    /**
     * Triggered from the GUI: Look again for servers
     */
    public void refreshList() {
        HomeTransferModel.getInstance().deleteServers();
        broadcastUDP("discover");
    }

    /**
     * Fulfills an UDP-broadcast using the given tag
     * @param tag "discovery" or "response"
     */
    public void broadcastUDP(String tag) {
        HomeTransferUDPClient.broadcastPackage(tag);
    }

    public static void sendFiles(String selectedValue, File[] files) {
        String[] str = selectedValue.split("@");
        String IP = str[1];
        HomeTransferServerData data = HomeTransferModel.getInstance().getServerData(IP);
        HomeTransferClient sender = new HomeTransferClient(data.IP, data.port, files);
        new Thread(sender).start();
    }

    /**
     * Closes all servers to release their resources.
     */
    public void closeController() {
        HomeTransferUDPClient.broadcastPackage("close");
        homeTransferServer.stop();
        homeTransferUDPServer.stop();
    }
}
