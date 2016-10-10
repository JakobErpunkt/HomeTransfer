package model;

import controller.HomeTransferController;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;

/**
 * Created by jakobriga on 02.12.15.
 * The class is singleton so that everyone can add information here.
 * This class serves as a data pool for the list of servers.
 * Note that we only memorize information necessary to establish a connection,
 * there is no connection yet made without explicit request by the user.
 */
public class HomeTransferModel extends Observable{

    // private instance for singleton
    private static HomeTransferModel homeTransferModel;
    // private List for all available servers around.
    // note that every server is uniquely identified with a String which is its IP
    private static HashMap<String, HomeTransferServerData> homeTransferServerHashMap;
    // represents the local instance of this program
    private static HomeTransferServerData localData;
    // represents the current files which are about to be sent. Identified by name
    private static HashMap<String, File> homeTransferFileHashMap;


    // private constructor for singleton
    private HomeTransferModel() {
        homeTransferServerHashMap = new HashMap<String, HomeTransferServerData>();
        homeTransferFileHashMap = new HashMap<String, File>();
        addObserver(HomeTransferController.getInstance());

        // The broadcast message consists of name;IP;port:
        // - The user Name
        String name = System.getProperty("user.name");
        // - The user's IP address
        String IP = "";
        try {
            InetAddress machine = InetAddress.getLocalHost();
            IP = machine.getHostAddress();
        } catch (Exception e) {
            System.out.println("Error: Local host not available");
            e.printStackTrace();
        }
        // - The port offered for communication
        // Creation of port: use 11[last two digits of IP address]
        String port = "11" + IP.substring(IP.length() - 2);
        // Do this to make sure a single digit number is avoided and the 0 is given
        port = port.replace(".", "0");
        port = port.replace("%", "0");

        localData = new HomeTransferServerData();
        localData.name = name;
        localData.IP = IP;
        localData.port = Integer.parseInt(port);
    }

    // public getter to make the singleton pattern work
    public static HomeTransferModel getInstance() {
        if (homeTransferModel == null) {
            homeTransferModel = new HomeTransferModel();
        }
        return homeTransferModel;
    }

    /**
     * Put the new ServerData to the list if not already received before
     * @param data
     */
    public void addServer(HomeTransferServerData data) {
        if ( !(homeTransferServerHashMap.containsKey(data.IP) || data.IP.equals(localData.IP)) ) {
            homeTransferServerHashMap.put(data.IP, data);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Remove a server when the order comes in
     * @param data
     */
    public void removeServer(HomeTransferServerData data) {
        if (homeTransferServerHashMap.containsKey(data.IP)) {
            homeTransferServerHashMap.remove(data.IP);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Add a given directory by calling this method recursively
     * @param directory
     */
    private void addDirectory(File directory) {
        for(File f : directory.listFiles()) {
            if (f.isDirectory()) {
                addDirectory(f);
            } else if (!homeTransferFileHashMap.containsKey(f.getName())) {
                homeTransferFileHashMap.put(f.getName(), f);
            }
        }
    }

    /**
     * Adds the given array of files
     * @param files
     */
    public void addFiles(File[] files) {
        for(File f : files) {
            if (f.isDirectory()) {
                addDirectory(f);
            } else if (!homeTransferFileHashMap.containsKey(f.getName())) {
                homeTransferFileHashMap.put(f.getName(), f);
            }
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Clears all received server data
     */
    public void deleteServers() {
        homeTransferServerHashMap.clear();
        setChanged();
        notifyObservers();
    }

    /**
     * Clears all memorized files
     */
    public void deleteFiles() {
        homeTransferFileHashMap.clear();
        setChanged();
        notifyObservers();
    }

    /**
     * Getter for the controller (-> GUI) code to show the available servers
     * @return
     */
    public String[] getServers() {
        String str[] = new String[homeTransferServerHashMap.size()];
        Iterator iter = homeTransferServerHashMap.entrySet().iterator();
        int index = 0;
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            HomeTransferServerData data = ((HomeTransferServerData)pair.getValue());
            str[index++] = data.name + "@" + data.IP;
        }
        return str;
    }

    /**
     * Getter for the controller (-> GUI) code to show the available filenames
     */
    public String[] getFilesNames() {
        String fileNames[] = new String[homeTransferFileHashMap.size()];
        Iterator iter = homeTransferFileHashMap.entrySet().iterator();
        int index = 0;
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            File file = ((File)pair.getValue());
            fileNames[index++] = file.getName();
        }
        return fileNames;
    }

    /**
     * Getter for the controller (-> GUI) code to show the files about to be sent
     * @return
     */
    public File[] getFiles() {
        File files[] = new File[homeTransferFileHashMap.size()];
        Iterator iter = homeTransferFileHashMap.entrySet().iterator();
        int index = 0;
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            File file = ((File)pair.getValue());
            files[index++] = file;
        }
        return files;
    }

    /**
     * Returns all data to a given IP (used as key)
     * @param IP
     * @return
     */
    public HomeTransferServerData getServerData(String IP) {
        return homeTransferServerHashMap.get(IP);
    }

    /**
     * Returns the file to the given name (used as key)
     * @param name
     * @return
     */
    public File getFile(String name) { return homeTransferFileHashMap.get(name); }

    /**
     * Getter for the local instance
     * @return
     */
    public HomeTransferServerData getLocalHomeTransferData() {
        return localData;
    }
}
