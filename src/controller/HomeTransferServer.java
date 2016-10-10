package controller;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jakobriga on 01.12.15.
 */
public class HomeTransferServer implements Runnable {

    private boolean running;
    private ServerSocket serverSocket;

    public HomeTransferServer(int port) throws Exception{
        serverSocket = new ServerSocket(port);
        running = false;
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("Error: Server could not be closed.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                System.out.println("The server is waiting for files.");
                Socket client = serverSocket.accept();
                InputStream is = client.getInputStream();
                // Chose 256 as filename buffer
                byte[] name = new byte[1<<8];
                is.read(name);
                String fileName = new String(name);
                fileName = fileName.trim();
                System.out.println("FileName: " + fileName);

                // Choose 134,... MB as buffer
                byte[] contents = new byte[1<<20];
                FileOutputStream fos = new FileOutputStream(fileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                int bytesRead = 0;
                while ((bytesRead = is.read(contents)) != -1) {
                    bos.write(contents, 0, bytesRead);
                }

                bos.flush();
                bos.close();
                fos.close();
                client.close();
                System.out.println("The server received the files!");

            } catch (Exception e) {
                System.out.println("Error: Accepting the client failed.");
                e.printStackTrace();
            }
        }
    }

}
