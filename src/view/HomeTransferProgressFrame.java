package view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by jakobriga on 11.12.15.
 *
 * A simple view which is only visible when
 */
public class HomeTransferProgressFrame extends JFrame{

    private JProgressBar progressBar;
    private JLabel percent;
    private JLabel speed;
    private JLabel transmitted;
    private static HomeTransferProgressFrame homeTransferProgressFrame;
    private final int MIN_PROGRESS = 0;
    private final int MAX_PROGRESS = 1000;

    private HomeTransferProgressFrame() {
        super("Progress");
        setLayout(new FlowLayout());
        setSize(160, 130);
        percent = new JLabel();
        transmitted = new JLabel();
        speed = new JLabel();
        progressBar = new JProgressBar(MIN_PROGRESS, MAX_PROGRESS);
        progressBar.setValue(MIN_PROGRESS);
        this.add(percent);
        this.add(progressBar);
        this.add(transmitted);
        this.add(speed);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public static HomeTransferProgressFrame getInstance() {
        if (homeTransferProgressFrame == null) {
            homeTransferProgressFrame = new HomeTransferProgressFrame();
        }
        return homeTransferProgressFrame;
    }

    /**
     * Accepts a double and sets the progressbar as necessary.
     * 0 equals zero progress and 1 equals 100% progress.
     * @param progress
     */
    public void setProgress(double progress, double sendingSpeed, double transmittedMB) {
        setVisible(true);
        progressBar.setValue((int) (progress * MAX_PROGRESS));
        percent.setText("Progress: " + (((int) (progress * MAX_PROGRESS)) / 10.0) + "%");
        speed.setText("Speed: " + (Math.round(sendingSpeed*100.0)/100.0) + "MB/s");
        transmitted.setText("Transmitted: " + (((int)(transmittedMB*10))/10.0) + "MB");
        this.repaint();
        HomeTransferView.getInstance().repaint();
    }

    public void showFrame(boolean b) {
        setVisible(b);
    }
}
