package func.utility.swing;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ListView {
    private final JList<func.utility.swing.ListEntity> list = new JList<>();

    public ListView() {
        setModel(new DefaultListModel<func.utility.swing.ListEntity>());

        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    //This method is useful only when the selection model allows a single selection.
                    func.utility.swing.ListEntity lastSelectedPathComponent = list.getSelectedValue();

                    // it can be null when the change consists in the element having been removed
                    if (lastSelectedPathComponent != null) {
                        Runnable whenSelected = lastSelectedPathComponent.getWhenSelected();
                        if (whenSelected != null) {
                            whenSelected.run();
                        }
                    }
                }
            }
        );

        // detects right clicking - doesn't consider selected items
        list.addMouseListener(
            new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int x = e.getX();
                    int y = e.getY();
                    int clickedRow = list.locationToIndex(new Point(x, y));
                    if (clickedRow != -1) {
                        Object node = list.getModel().getElementAt(clickedRow);
                        if (node instanceof func.utility.swing.ListEntity) {
                            List<? extends Action> menuActions = ((func.utility.swing.ListEntity) node).getMenuEntities();
                            if (!menuActions.isEmpty()) {
                                JPopupMenu popup = new JPopupMenu();
                                for (Action action : menuActions) {
                                    popup.add(action);
                                }
                                popup.show(list, x, y);
                            }
                        }
                    }
                }
            }
        });
    }

    public void setModel(ListModel<func.utility.swing.ListEntity> listModel) {
        list.setModel(listModel);
    }

    public JList<func.utility.swing.ListEntity> getComponent() {
        return list;
    }
}
