package func.utility.swing;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TreeView {
    private final JTree tree = new JTree();

    public TreeView() {
        this(new func.utility.swing.TreeEntity("root"));
    }

    public TreeView(func.utility.swing.TreeEntity root) {
        tree.setModel(new DefaultTreeModel(root));

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        tree.addTreeSelectionListener(
            new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    //This method is useful only when the selection model allows a single selection.
                    Object lastSelectedPathComponent = tree.getLastSelectedPathComponent();
//                    Object lastSelectedPathComponent = e.getPath().getLastPathComponent();

                    if (lastSelectedPathComponent instanceof func.utility.swing.TreeEntity) {
                        ((func.utility.swing.TreeEntity) lastSelectedPathComponent).getWhenSelected().run();
                    }
                }
            }
        );

        // detects right clicking - doesn't consider selected items
        tree.addMouseListener(
            new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        int x = e.getX();
                        int y = e.getY();
                        int clickedRow = tree.getRowForLocation(x, y);
                        if (clickedRow != -1) {
                            Object node = tree.getPathForLocation(x, y).getLastPathComponent();
                            if (node instanceof func.utility.swing.TreeEntity) {
                                List<? extends Action> menuActions = ((func.utility.swing.TreeEntity) node).getMenuEntities();
                                if (!menuActions.isEmpty()) {
                                    JPopupMenu popup = new JPopupMenu();
                                    for (Action action : menuActions) {
                                        popup.add(action);
                                    }
                                    popup.show(tree, x, y);
                                }
                            }
                        }
                    }
                }
            }
        );

        tree.addTreeExpansionListener(
            new TreeExpansionListener() {
                public void treeExpanded(TreeExpansionEvent event) {
                    Object lastSelectedPathComponent = tree.getLastSelectedPathComponent();
                    if (lastSelectedPathComponent instanceof func.utility.swing.TreeEntity) {
                        ((func.utility.swing.TreeEntity) lastSelectedPathComponent).getWhenExpanded().run();
                    }
                }

                public void treeCollapsed(TreeExpansionEvent event) {
                    Object lastSelectedPathComponent = tree.getLastSelectedPathComponent();
                    if (lastSelectedPathComponent instanceof func.utility.swing.TreeEntity) {
                        ((func.utility.swing.TreeEntity) lastSelectedPathComponent).getWhenCollapsed().run();
                    }
                }
            }
        );

        tree.addTreeWillExpandListener(
            new TreeWillExpandListener() {
                public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                    Object lastSelectedPathComponent = tree.getLastSelectedPathComponent();
                    if (lastSelectedPathComponent instanceof func.utility.swing.TreeEntity) {
                        ((func.utility.swing.TreeEntity) lastSelectedPathComponent).getBeforeWillExpand().run();
                    }
                }

                public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                    Object lastSelectedPathComponent = tree.getLastSelectedPathComponent();
                    if (lastSelectedPathComponent instanceof func.utility.swing.TreeEntity) {
                        ((func.utility.swing.TreeEntity) lastSelectedPathComponent).getBeforeWillCollapse().run();
                    }
                }
            }
        );
    }

    public func.utility.swing.TreeEntity getRoot() {
        return (func.utility.swing.TreeEntity) tree.getModel().getRoot();
    }

    public JTree getComponent() {
        return tree;
    }
}

