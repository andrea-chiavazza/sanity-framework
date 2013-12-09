package func.utility.swing;

import func.persist.XMLRead;
import func.persist.XMLWrite;
import org.pcollections.PCollection;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import static func.persist.XMLRead.verifyCollection;

public class General {

    private static final FileFilter xmlFilter = new FileFilter() {
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".xml");
        }

        public String getDescription() {
            return "XML file";
        }
    };

    public static <T> PCollection<T> loadCollection(InputStream is,
                                                    Class<T> cl) {
        try {
            return verifyCollection(XMLRead.xmlStreamToValue(is), cl);
        } catch (IOException | SAXException | ClassNotFoundException ignored) {
            return null;
        }
    }

    public static List<? extends TreeNode> getTreeNodeChildren(TreeNode tree) {
        Enumeration childrenEnumeration = tree.children();
        List<TreeNode> children = new ArrayList<>();
        while (childrenEnumeration.hasMoreElements()) {
            children.add((TreeNode) childrenEnumeration.nextElement());
        }
        return children;
    }

    public static <T> List<T> getTreeNodeUserObjects(DefaultMutableTreeNode treeNode) {
        List<T> list = new ArrayList<>();
        for (TreeNode node : getTreeNodeChildren(treeNode)) {
            list.add((T) ((DefaultMutableTreeNode) node).getUserObject());
        }
        return list;
    }

    public static File promptForFileToOpen(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
            new FileFilter() {
                public boolean accept(File f) {
                    return f.getName().endsWith(".xml");
                }

                public String getDescription() {
                    return "XML file";
                }
            });
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile;
        } else {
            return null;
        }
    }

    public static FileInputStream promptForFileISToOpen(JFrame frame,
                                                        FileFilter fileFilter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(fileFilter);
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                return new FileInputStream(selectedFile);
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(
                    frame, "Error opening file " + selectedFile.getName());
                return null;
            }
        } else {
            return null;
        }
    }

    public static <T> PCollection<T> promptAndLoadCollection(JFrame frame,
                                                             Class<T> cl) {
        FileInputStream fis = promptForFileISToOpen(frame, xmlFilter);
        if (fis != null) {
            return loadCollection(fis, cl);
        } else {
            return null;
        }
    }

    public static <T> void promptAndSaveCollection(JFrame frame,
                                                   Collection<T> coll) {
        File selectedFile = promptForFileToOpen(frame);
        try {
            if (selectedFile != null) {
                XMLWrite.valueToXMLWriter(coll, new FileWriter(selectedFile));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,
                                          "Error saving to file " + selectedFile.getName());
        }
    }

}
