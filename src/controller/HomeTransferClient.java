package controller;

import view.HomeTransferProgressFrame;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by jakobriga on 11.12.15.
 */
public class HomeTransferClient implements Runnable{

    private static Socket client;
    public static boolean running;
    private String IP;
    private int port;
    private File[] files;
    private final int NUMBER_OF_BYTES = 1<<20;
    private final double MB_PER_ROUND = NUMBER_OF_BYTES / 1000000.0;
    private final boolean REALTIME = false;
    private final int UPDATE_TIME_MILIS = 5000;

    public HomeTransferClient(String IP, int port, File[] files) {
        this.IP = IP;
        this.port = port;
        this.files = files;
    }

    @Override
    public void run() {
        HomeTransferProgressFrame progressFrame = HomeTransferProgressFrame.getInstance();
        progressFrame.setVisible(true);
        for (int i=0; i<files.length; i++) {
            try {
                client = new Socket(IP, port);


                File file = files[i];
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);

                // Transmitting the name of the file first
                OutputStream os = client.getOutputStream();
                byte[] filename = new byte[1<<8];
                System.arraycopy(file.getName().getBytes(), 0, filename, 0, file.getName().getBytes().length);
                os.write(filename);
                System.out.println("Sending filename of file: " + file.getName());
                os.flush();

                byte[] contents;
                long fileLength = file.length();
                long current = 0;

                // Handling the GUI
                double speed = 0;
                long lastTimeMilis = System.currentTimeMillis();
                long lastTimePrintedMilis = lastTimeMilis-1000;
                long currentTimeMilis;
                int numberOfMeasurements = 0;
                int numberOfMeasurementsTotal = 0;
                int size;

                // Transfer the file content
                while (current != fileLength) {
                    size = NUMBER_OF_BYTES;
                    numberOfMeasurements++;
                    numberOfMeasurementsTotal++;
                    if (fileLength - current >= size) {
                        current += size;
                    } else {
                        size = (int)(fileLength - current);
                        current = fileLength;
                    }
                    contents = new byte[size];
                    bis.read(contents, 0, size);
                    os.write(contents);

                    // Handling the GUI
                    currentTimeMilis = System.currentTimeMillis();
                    if (REALTIME || currentTimeMilis-lastTimePrintedMilis >= UPDATE_TIME_MILIS) {
                        speed = NUMBER_OF_BYTES*numberOfMeasurements/((currentTimeMilis-lastTimePrintedMilis)*1024.0);
                        numberOfMeasurements = 0;
                        lastTimePrintedMilis = currentTimeMilis;
                    }
                    lastTimeMilis = currentTimeMilis;
                    progressFrame.setProgress((current*1.0)/(1.0*fileLength), speed, numberOfMeasurementsTotal*MB_PER_ROUND);
                }

                System.out.println("Sending file: " + file.getName());
                os.flush();
                os.close();
                client.close();
            } catch (Exception e) {
                System.out.println("Error: File could not be sent.");
                e.printStackTrace();
            }
        }
        progressFrame.setVisible(false);
    }
}
