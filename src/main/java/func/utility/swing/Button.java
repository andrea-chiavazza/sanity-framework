package func.utility.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Button {
    public static JButton makeButton(final String label,
                                     final Runnable runnable) {
        JButton button = new JButton(label);
        button.setActionCommand(label);
        button.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (label.equals(e.getActionCommand())) {
                        runnable.run();
                    }
                }
            }
        );
        return button;
    }

}
