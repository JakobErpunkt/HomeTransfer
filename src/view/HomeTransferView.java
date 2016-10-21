package view;

import controller.HomeTransferController;
import model.HomeTransferModel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.AbstractList;

/**
 * Created by jakobriga on 05.12.15.
 */
public class HomeTransferView extends JFrame implements ActionListener{

    private static HomeTransferView view;

    private JLabel fileLabel;
    private JLabel serverLabel;
    private JList fileList;
    private JList serverList;
    private JPanel filePanel;
    private JPanel serverPanel;
    private JPanel buttonPanel;
    private Box fileBox;
    private Box serverBox;
    private Box buttonBox;
    private JScrollPane fileScrollPane;
    private JScrollPane serverScrollPane;
    private JButton browse;
    private JButton refresh;
    private JButton send;
    private JButton clearList;
    private HomeTransferProgressFrame homeTransferProgressFrame;

    private HomeTransferView() {

        super(HomeTransferModel.getInstance().getLocalHomeTransferData().name + "@" + HomeTransferModel.getInstance().getLocalHomeTransferData().IP);
        setSize(400, 100);
        setLayout(new FlowLayout());

        fileLabel = new JLabel("Files to be sent");
        fileList = new JList(new DefaultListModel());
        fileList.setDragEnabled(true);
        fileList.setMinimumSize(new Dimension(400, 300));
        fileList.setCellRenderer(new FileListRenderer());
        fileBox = new Box(BoxLayout.Y_AXIS);
        filePanel = new JPanel();
        filePanel.add(fileList);
        fileScrollPane = new JScrollPane(fileList);
        fileScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        fileScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        fileScrollPane.setPreferredSize(new Dimension(400, 300));
        fileBox.add(fileLabel);
        fileBox.add(fileScrollPane);
        this.add(fileBox);

        TransferHandler handler = new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport info) {
                // We only import FileList
                if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    return false;
                }
                return true;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport info) {
                System.out.println("dropped it like it's hot");
                if (!info.isDrop()) {
                    return false;
                }

                //Check for FileList flavor
                if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    displayDropLocation("List doesn't accept a drop of this type.");
                    return false;
                }

                AbstractList<File> data;
                // Get the fileList that is being dropped
                Transferable t = info.getTransferable();
                try {
                    data = (AbstractList<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
                } catch (Exception e) {
                    return false;
                }
//                DefaultListModel model = (DefaultListModel) fileList.getModel();
                File[] files = new File[data.size()];
                int index = 0;
                for (File file : data) {
//                    model.addElement(file);
                    files[index++] = file;
                }
                HomeTransferModel.getInstance().addFiles(files);
                return true;
            }

            private void displayDropLocation(String string) {
                System.out.println(string);
            }
        };
        fileList.setTransferHandler(handler);


        buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        buttonBox = new Box(BoxLayout.Y_AXIS);
        browse = new JButton("browse");
        browse.setActionCommand("browse");
        browse.addActionListener(this);
        clearList = new JButton("clear list");
        clearList.setActionCommand("clear list");
        clearList.addActionListener(this);
        refresh = new JButton("refresh");
        refresh.setActionCommand("refresh");
        refresh.addActionListener(this);
        send = new JButton("send");
        send.setActionCommand("send");
        send.addActionListener(this);
        buttonBox.add(browse);
        buttonBox.add(clearList);
        buttonBox.add(send);
        buttonBox.add(refresh);
        buttonPanel.add(buttonBox);
        this.add(buttonPanel);

        serverLabel = new JLabel("Servers available");
        serverList = new JList();
        serverList.setCellRenderer(new ServerListRenderer());
        serverBox = new Box(BoxLayout.Y_AXIS);
        serverPanel = new JPanel();
        serverPanel.add(serverList);
        serverScrollPane = new JScrollPane(serverList);
        serverScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        serverScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        serverScrollPane.setPreferredSize(new Dimension(400, 300));
        serverBox.add(serverLabel);
        serverBox.add(serverScrollPane);
        this.add(serverBox);


        HomeTransferProgressFrame.getInstance();

        setVisible(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                HomeTransferController.getInstance().closeController();
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        refresh();
    }

    public void init() {
        // This has to be performed once because the usage of singelton pattern
        HomeTransferController.getInstance().initController();
        HomeTransferController.getInstance().broadcastUDP("discover");
    }

    public static HomeTransferView getInstance() {
        if (view == null) {
            view = new HomeTransferView();
        }
        return view;
    }

    /**
     * Updates the serverlist's data by overwriting it
     * @param serverNames
     */
    public void setServerData(String[] serverNames) {
        serverList.setListData(serverNames);
        refresh();
    }

    /**
     * Updates the filelist's data by overwriting it
     * @param files
     */
    public void setFileData(String[] files) {
        fileList.setListData(files);
        refresh();
    }

    private void refresh() {
        System.out.println("Refresh started");
        validate();
        pack();
        repaint();
        System.out.println("Refresh ended");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("browse")) {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = chooser.getSelectedFiles();
                HomeTransferModel.getInstance().addFiles(files);
            }
        } else if (e.getActionCommand().equals("clear list")) {
            HomeTransferModel.getInstance().deleteFiles();
        } else if (e.getActionCommand().equals("send")) {
            String server = "";
            String servers[];
            HomeTransferModel model = HomeTransferModel.getInstance();
            if ((servers=model.getServers()).length == 1) {
                server = servers[0];
            } else if (servers.length > 1){
                if ((server=serverList.getSelectedValue().toString()) == null) {
                    JOptionPane.showMessageDialog(null, "No server selected");
                    return;
                }
            } else if (servers.length < 1) {
                return;
            }
            HomeTransferController.sendFiles(server, HomeTransferModel.getInstance().getFiles());
        }
        else if (e.getActionCommand().equals("refresh")) {
            HomeTransferController.getInstance().refreshList();
            refresh();
        }
    }
}
