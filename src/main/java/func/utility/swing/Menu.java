package func.utility.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu {
    public static JMenuItem makeMenuItem(String name,
                                         KeyStroke keyStroke,
                                         Integer keyEvent,
                                         String accessibleContent,
                                         final Runnable runnable) {
        JMenuItem menuItem = new JMenuItem(name);
        if (keyStroke != null) {
            menuItem.setAccelerator(keyStroke);
        }
        if (keyEvent != null) {
            menuItem.setMnemonic(keyEvent);
        }
        if (accessibleContent != null) {
            menuItem.getAccessibleContext().setAccessibleDescription(accessibleContent);
        }
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        });
        return menuItem;
    }

    public static JMenu makeMenu(String name,
                                 Integer keyEvent,
                                 String accessibleContent,
                                 JMenuItem... menuItems) {
        JMenu menu = new JMenu(name);
        if (keyEvent != null) {
            menu.setMnemonic(keyEvent);
        }
        if (accessibleContent != null) {
            menu.getAccessibleContext().setAccessibleDescription(accessibleContent);
        }
        for (JMenuItem menuItem : menuItems) {
            menu.add(menuItem);
        }
        return menu;
    }

    public static JMenuBar makeMenuBar(String accessibleContent,
                                       JMenu... menus) {
        JMenuBar menuBar = new JMenuBar();
        if (accessibleContent != null) {
            menuBar.getAccessibleContext().setAccessibleDescription("The main menu");
        }
        for (JMenu menu : menus) {
            menuBar.add(menu);
        }
        return menuBar;
    }

}
