package func.utility.swing;

import func.basic.F1;
import func.basic.MapFunc;
import func.persist.XMLRead;
import func.persist.XMLWrite;
import org.pcollections.PCollection;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.*;
import java.util.*;

import static func.persist.XMLRead.verifyCollection;

public class General {

    public static final FileFilter xmlFilter = new FileFilter() {
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".xml");
        }

        public String getDescription() {
            return "XML file";
        }
    };

    // todo: should be moved somewhere else
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
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile;
        } else {
            return null;
        }
    }

    /** If initialDir is null then the user's default directory will be used.
     * If the user clicks cancel then null will be returned. */
    public static File promptForDirectory(String message,
                                          JFrame frame,
                                          File initialDir) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(message);
        fileChooser.setCurrentDirectory(initialDir);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }

    /** if dir is null the user default directory will be used. */
    public static List<File> promptForFileToOpen(JFrame frame,
                                                 FileFilter fileFilter,
                                                 boolean multiSelection,
                                                 File dir) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(multiSelection);
        fileChooser.setCurrentDirectory(dir);
        fileChooser.setFileFilter(fileFilter);
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles;
            if (multiSelection) {
                selectedFiles = fileChooser.getSelectedFiles();
            } else {
                selectedFiles = new File[] {fileChooser.getSelectedFile()};
            }
            return Arrays.asList(selectedFiles);
        } else {
            return null;
        }
    }

    /** if dir is null the user default directory will be used. */
    public static List<FileInputStream> promptForFileISToOpen(final JFrame frame,
                                                              FileFilter fileFilter,
                                                              boolean multiSelection,
                                                              File dir) {
        return MapFunc.map(
            new F1<File,FileInputStream>() {
                public FileInputStream execute(File file) {
                    try {
                        return new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        JOptionPane.showMessageDialog(frame, e.getMessage());
                        return null;
                    }
                }
            },
            promptForFileToOpen(frame, fileFilter, multiSelection, dir));
    }

    public static <T> PCollection<T> promptAndLoadCollection(JFrame frame,
                                                             Class<T> cl,
                                                             File dir) {
        FileInputStream fis = promptForFileISToOpen(frame, xmlFilter, false, dir).get(0);
        if (fis != null) {
            return loadCollection(fis, cl);
        } else {
            return null;
        }
    }

    /** Returns the file where it has been saved.
     /*  if dir is null the user default directory will be used. */
    public static <T> File promptAndSaveCollection(JFrame frame,
                                                   Collection<T> coll,
                                                   File dir) {
        try {
            List<File> selectedFiles = promptForFileToOpen(frame, xmlFilter, false, dir);
            if (selectedFiles != null) {
                File file = selectedFiles.get(0);
                XMLWrite.valueToXMLWriter(coll, new FileWriter(file));
                return file;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
        return null;
    }

}
