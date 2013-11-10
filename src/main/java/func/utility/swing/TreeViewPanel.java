package func.utility.swing;

import func.basic.F1;
import org.pcollections.OrderedPSet;
import org.pcollections.PSet;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class TreeViewPanel {
    private final TreeView treeView;
    private final Map<String,TreeEntity> sets = new LinkedHashMap<>();
    private final JFrame frame;

    public TreeViewPanel(JFrame frame) {
        treeView = new TreeView();
        treeView.getComponent().setRootVisible(false);
        this.frame = frame;
    }

    public <T> void setEntities(final String name,
                                final PSet<T> set,
                                final String nameGetterName,
                                final Class<T> cl,
                                final F1<T,Void> whenSelected,
                                final F1<T,Void> whenRemoved)
                                    throws NoSuchMethodException,
                                           InvocationTargetException,
                                           IllegalAccessException {
        if (! sets.containsKey(name)) {
            sets.put(name, new TreeEntity(set, name));
            final TreeEntity treeEntity = sets.get(name);

            treeEntity.setMenuActionsMaker(
                new F1<Void,List<? extends Action>>() {
                    public List<? extends Action> execute(Void aVoid) {
                        List<Action> menuActions = new ArrayList<>();
                        menuActions.add(
                            new TreeView.MenuAction("Load") {
                                public void actionPerformed(ActionEvent e) {
                                    try {
                                        setEntities(name,
                                                    OrderedPSet.from(
                                                        General.promptAndLoadCollection(frame, cl)),
                                                    nameGetterName,
                                                    cl,
                                                    whenSelected,
                                                    whenRemoved);
                                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e1) {
                                        throw new RuntimeException(e1);
                                    }
                                }
                            }
                        );
                        if (((PSet) treeEntity.getUserObject()).isEmpty()) {
                            menuActions.add(
                                new TreeView.MenuAction("Save") {
                                    public void actionPerformed(ActionEvent e) {
                                        General.promptAndSaveCollection(frame, set);
                                    }
                                }
                            );
                        }
                        return menuActions;
                    }
                });
            treeEntity.setWhenSelected(
                new Runnable() {
                    public void run() {
                        //todo:
                    }
                }
            );
            TreeEntity root = treeView.getRoot();
            root.add(treeEntity);
        }

        final TreeEntity treeEntity = sets.get(name);
        treeEntity.setUserObject(set);
        treeEntity.removeAllChildren();
        for (final T t : set) {
            Method m = t.getClass().getMethod(nameGetterName);
            TreeEntity child = new TreeEntity(t, m.invoke(t).toString());
            treeEntity.add(child);
            child.setWhenSelected(
                new Runnable() {
                    public void run() {
                        whenSelected.execute(t);
                    }
                }
            );
            child.setMenuActions(
                Arrays.asList(
                    new TreeView.MenuAction("Remove") {
                        public void actionPerformed(ActionEvent e) {
                            try {
                                setEntities(name, set.minus(t), nameGetterName, cl, whenSelected, whenRemoved);
                                whenRemoved.execute(t);
                            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e1) {
                                throw new RuntimeException(e1);
                            }
                        }
                    }
                )
            );
        }
        ((DefaultTreeModel) this.treeView.getComponent().getModel()).reload();
        treeView.getComponent().expandPath(new TreePath(treeEntity.getPath()));
    }


    public <T> OrderedPSet<T> getEntities(String name) {
        return OrderedPSet.from(
            General.<T>getTreeNodeUserObjects(sets.get(name)));
    }

    public void removeEntities(String name) {
        if (sets.containsKey(name)) {
            treeView.getRoot().remove(sets.get(name));
            sets.remove(name);
            ((DefaultTreeModel) this.treeView.getComponent().getModel()).reload();
        }
    }



    public void selectObject(Object object) {
        for (String name : sets.keySet()) {
            for (TreeNode treeNode : General.getTreeNodeChildren(sets.get(name))) {
                DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeNode;
                if (object.equals(defaultMutableTreeNode.getUserObject())) {
                    treeView.getComponent().setSelectionPath(new TreePath(defaultMutableTreeNode.getPath()));
                    return;
                }
            }
        }
    }

    public JTree getComponent() {
        return treeView.getComponent();
    }

}
