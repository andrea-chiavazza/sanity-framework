package func.utility.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CheckboxListView {
    private static final int CHECKBOX_WIDTH = new JCheckBox().getPreferredSize().width;
    private final ListView listView;

    public CheckboxListView(ListView listView) {
        this.listView = listView;

        final JList<func.utility.swing.ListEntity> list = listView.getComponent();

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // adds check-boxes
        list.setCellRenderer(
            new ListCellRenderer<func.utility.swing.ListEntity>() {
                public Component getListCellRendererComponent(JList<? extends func.utility.swing.ListEntity> list,
                                                              func.utility.swing.ListEntity value,
                                                              int index,
                                                              boolean isSelected,
                                                              boolean cellHasFocus) {
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                    JCheckBox checkBox = new JCheckBox();
                    checkBox.setSelected(value.isChecked());
                    panel.add(checkBox);
                    panel.add(new JLabel(value.getName()));

                    if (isSelected) {
                        panel.setBackground(list.getSelectionBackground());
                        panel.setForeground(list.getSelectionForeground());
                    } else {
                        panel.setBackground(list.getBackground());
                        panel.setForeground(list.getForeground());
                    }

                    return panel;
                }
            }
        );

        list.addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();
                    int clickedRow = list.locationToIndex(new Point(x, y));
                    if (clickedRow != -1) {
                        func.utility.swing.ListEntity node = list.getModel().getElementAt(clickedRow);
                        if (e.getX() < CHECKBOX_WIDTH) {
                            node.setChecked(!node.isChecked());
                            Rectangle cellBounds = list.getCellBounds(clickedRow, clickedRow);
                            if (cellBounds != null) {
                                list.repaint(cellBounds);
                            } else {
                                System.out.println("this shouldn't happen");
                                list.repaint();
                            }
                        }
                    }
                }
            }
        );
    }

    public void setModel(ListModel<func.utility.swing.ListEntity> listModel) {
        listView.setModel(listModel);
    }

    public JList<func.utility.swing.ListEntity> getComponent() {
        return listView.getComponent();
    }
}
