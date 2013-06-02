package func.utility.swing;

import func.values.Mixed;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static func.values.Values.m1;
import static func.values.Values.m2;

public class VisualTestValueEditor {

    private static void showClass(Class<?> cl,
                                  Map<Object,Object> aliases,
                                  String title) {
        makeValueEditorAndShow(cl, aliases, title);
    }

    private static void showValue(Object value,
                                  Map<Object,Object> aliases,
                                  String title) {
        makeValueEditorAndShow(value.getClass(), aliases, title).setValue(value);
    }

    private static ValueEditor makeValueEditorAndShow(Class cl,
                                                      Map<Object,Object> aliases,
                                                      String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ValueEditor valueEditor = new ValueEditor(cl, null, aliases);
        JScrollPane scrollPane = new JScrollPane(valueEditor);
        scrollPane.setPreferredSize(new Dimension(400, 700));
        frame.add(scrollPane);
        frame.pack();
        frame.setVisible(true);
        return valueEditor;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    Map<Object,Object> aliases = new HashMap<>();
                    aliases.put("func.values.Primitives/getS", "aShort");
                    aliases.put("func.values.Mixed/getVs", "string vec");
                    aliases.put("func.values.Mixed/getVs/TYPE", "java.lang.String");
                    showValue(m1, aliases, "m1");
                    showValue(m2, aliases, "m2");
                    showClass(Mixed.class, aliases, "Mixed.class");
                }
            }
        );
    }
}
