package view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by jakobriga on 16.05.16.
 */
public class ServerListRenderer extends DefaultListCellRenderer {

    Font font = new Font("helvetica", Font.PLAIN, 24);

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        ImageIcon icon = new ImageIcon(getClass().getResource("/pc_icon.jpeg"));
        label.setIcon(icon);
        label.setHorizontalTextPosition(JLabel.RIGHT);
        label.setFont(font);
        return label;
    }

}
