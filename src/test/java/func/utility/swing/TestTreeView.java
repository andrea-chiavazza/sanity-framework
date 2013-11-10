package func.utility.swing;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class TestTreeView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    final TreeEntity root = new TreeEntity(2, "rootA");
                    TreeView treeView = new TreeView(root);

                    root.setWhenSelected(
                        new Runnable() {
                            public void run() {
                                System.out.println(root.getName() + " selected");
                            }
                        }
                    );

                    root.setWhenExpanded(
                        new Runnable() {
                            public void run() {
                                System.out.println(root.getName() + " expanded");
                            }
                        }
                    );

                    JTree.DynamicUtilTreeNode.createChildren(
                        treeView.getRoot(),
                        new DefaultMutableTreeNode[] {
                            new DefaultMutableTreeNode("aaa"),
                            new DefaultMutableTreeNode("bbb"),
                            new DefaultMutableTreeNode("cc cc"),
                        }
                    );

                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    Component component = treeView.getComponent();
                    component.setPreferredSize(new Dimension(300, 300));
                    frame.add(component);
                    frame.pack();
                    frame.setVisible(true);
                }
            }
        );
    }
}
