package func.utility.swing;

import func.basic.F1;
import func.basic.F2;
import func.basic.MapFunc;
import org.pcollections.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static func.utility.swing.General.getTreeNodeChildren;

/**
 * Stores sets of data objects divided in groups.
 */
public class TreeViewPanel {
    private final JTree tree;

    private MouseListener mouseListener;
    private TreeSelectionListener treeSelectionListener;

    // useful to programmatically trigger a selection action
    private F1<Collection<?>,Void> whenSelected;

    // useful to programmatically trigger a popup menu action
    private F2<Object,Collection<?>,MenuActionMap> popupMaker;

    public TreeViewPanel() {
        tree = new JTree(new DefaultTreeModel(makeNode("", null)));
        tree.setRootVisible(false);
    }

    private DefaultMutableTreeNode getBranch(String name) {
        for (TreeNode node : getTreeNodeChildren(getRoot())) {
            if (node instanceof DefaultMutableTreeNode &&
                name.equals(((DefaultMutableTreeNode) node).getUserObject())) {
                return (DefaultMutableTreeNode) node;
            }
        }
        return null;
    }

    private static List<?> treePathsToObjects(TreePath[] treePaths) {
        return MapFunc.map(
            new F1<TreePath,Object>() {
                public Object execute(TreePath treePath) {
                    DefaultMutableTreeNode component =
                        (DefaultMutableTreeNode) treePath.getLastPathComponent();
                    return component.getUserObject();
                }
            },
            Arrays.asList(treePaths));
    }

    public void setWhenSelected(final F1<Collection<?>,Void> whenSelected) {
        this.whenSelected = whenSelected;
        if (treeSelectionListener != null) {
            tree.removeTreeSelectionListener(treeSelectionListener);
        }
        treeSelectionListener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                Set<?> selectedObjects = getSelectedObjects();
                if (!selectedObjects.isEmpty()) {
                    whenSelected.execute(selectedObjects);
                }
            }
        };
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                Set<?> selectedObjects = getSelectedObjects();
                if (!selectedObjects.isEmpty()) {
                    whenSelected.execute(selectedObjects);
                }
            }
        });
    }

    public void setPopupMaker(final F2<Object,Collection<?>,MenuActionMap> popupMaker) {
        this.popupMaker = popupMaker;
        if (mouseListener != null) {
            tree.removeMouseListener(mouseListener);
        }
        mouseListener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int x = e.getX();
                    int y = e.getY();
                    int clickedRow = tree.getRowForLocation(x, y);
                    Object node = tree.getPathForLocation(x, y).getLastPathComponent();
                    Object clickedObject;
                    if (node instanceof DefaultMutableTreeNode) {
                        clickedObject = ((DefaultMutableTreeNode) node).getUserObject();
                    } else {
                        clickedObject = null;
                    }
                    if (clickedRow != -1) {
                        Set<?> selectedObjects = getSelectedObjects();
                        if (!selectedObjects.isEmpty()) {
                            MenuActionMap menuActions =
                                popupMaker.execute(clickedObject, selectedObjects);
                            if (menuActions != null && !menuActions.isEmpty()) {
                                JPopupMenu popup = new JPopupMenu();
                                for (Action action : menuActions.values()) {
                                    popup.add(action);
                                }
                                popup.show(tree, x, y);
                            }
                        }
                    }
                }
            }
        };
        tree.addMouseListener(mouseListener);
    }

    public PSet<?> getSelectedObjects() {
        TreePath[] selectionPaths = tree.getSelectionPaths();
        if (selectionPaths != null) {
            return HashTreePSet.from(treePathsToObjects(selectionPaths));
        } else {
            return Empty.set();
        }
    }

    public <T> void setEntities(final String name,
                                final PSet<T> set,
                                final String nameGetterName)
                                    throws NoSuchMethodException,
                                           InvocationTargetException,
                                           IllegalAccessException {
        DefaultMutableTreeNode node = getBranch(name);
        if (node == null) {
            node = makeNode(name, name);
            getRoot().add(node);
        }
//        if (!sets.containsKey(name)) {
//            sets.put(name, makeNode(name, name));
//            close if here ???
//            getRoot().add(sets.get(name));
//        }

//        final DefaultMutableTreeNode treeEntity = sets.get(name);
        node.setUserObject(name);
        node.removeAllChildren();
        for (final T t : set) {
            final String s = t.getClass().getMethod(nameGetterName).invoke(t).toString();
            final DefaultMutableTreeNode child = new DefaultMutableTreeNode(t) {
                public String toString() {
                    return s;
                }
            };
            node.add(child);
        }
        ((DefaultTreeModel) tree.getModel()).reload();
        for (TreeNode t : getTreeNodeChildren(getRoot())) {
            tree.expandPath(new TreePath(((DefaultMutableTreeNode) t).getPath()));
        }
    }

    public <T> POrderedSet<T> getEntities(String name) {
        DefaultMutableTreeNode node = getBranch(name);
        return node == null ? Empty.<T>orderedSet() :
            OrderedPSet.from(General.<T>getTreeNodeUserObjects(node));
    }

    public DefaultMutableTreeNode getRoot() {
        return (DefaultMutableTreeNode) tree.getModel().getRoot();
    }

    public void removeEntities(String name) {
        DefaultMutableTreeNode node = getBranch(name);
        if (node != null) {
            getRoot().remove(node);
            ((DefaultTreeModel) tree.getModel()).reload();
        }
    }

    public void removeEntity(String entitiesName,
                             Object entity) {
        DefaultMutableTreeNode node = getBranch(entitiesName);
        if (node != null) {
            List<Object> treeNodeUserObjects = General.getTreeNodeUserObjects(node);
            node.remove(treeNodeUserObjects.indexOf(entity));
            Object userObject = node.getUserObject();
            if (userObject instanceof PSet) {
                node.setUserObject(((PSet) userObject).minus(entity));
            }
            ((DefaultTreeModel) tree.getModel()).reload(node);
            tree.expandPath(new TreePath(node.getPath()));
        }
    }

    public void selectObject(Object object) {
        for (TreeNode branch : getTreeNodeChildren(getRoot())) {
            for (TreeNode treeNode : getTreeNodeChildren(branch)) {
                DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeNode;
                if (object.equals(defaultMutableTreeNode.getUserObject())) {
                    tree.setSelectionPath(new TreePath(defaultMutableTreeNode.getPath()));
                    return;
                }
            }
        }
    }

    public static DefaultMutableTreeNode makeNode(final String label,
                                                  final Object userObject) {
        return new DefaultMutableTreeNode(userObject) {
            public String toString() {
                return label;
            }
        };
    }

    public void runSelectionAction(Collection<?> selectedObjects) {
        whenSelected.execute(selectedObjects);
    }

    public void runMenuAction(String actionName,
                              Object clickedObject,
                              Collection<?> selectedObjects) {
        popupMaker.execute(clickedObject, selectedObjects)
            .get(actionName).actionPerformed(null);
    }

    public JTree getComponent() {
        return tree;
    }

}
