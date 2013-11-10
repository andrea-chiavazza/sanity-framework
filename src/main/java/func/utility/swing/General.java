package func.utility.swing;

import func.persist.XMLRead;
import func.persist.XMLWrite;
import org.pcollections.PCollection;
import org.pcollections.PSet;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import static func.persist.XMLRead.verifyCollection;

public class General {


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
        List<TreeNode> children = new ArrayList<TreeNode>();
        while (childrenEnumeration.hasMoreElements()) {
            children.add((TreeNode) childrenEnumeration.nextElement());
        }
        return children;
    }

    public static FileInputStream promptForFile(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                return new FileInputStream(selectedFile);
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(
                    frame, "File not found " + selectedFile.getName());
            }
        }
        return null;
    }

    public static <T> PCollection<T> promptAndLoadCollection(JFrame frame,
                                                             Class<T> cl) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                return loadCollection(new FileInputStream(selectedFile), cl);
            } catch (IOException ignored) {
            }
            JOptionPane.showMessageDialog(
                frame, "Error opening file " + selectedFile.getName());
        }
        return null;
    }


    public static <T> void promptAndSaveCollection(JFrame frame,
                                                   PSet<T> set) {
        saveCollection(frame, set);
    }

    public static <T> void saveCollection(JFrame frame,
                                          Collection<T> coll) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                XMLWrite.valueToXMLWriter(coll, new FileWriter(selectedFile));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame,
                                              "Error saving to file " + selectedFile.getName());
            }
        }
    }

}
