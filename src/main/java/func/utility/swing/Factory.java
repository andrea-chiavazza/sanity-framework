package func.utility.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Factory {
    private static Button makeButton(final String label,
                                     final Runnable callback) {
        JButton button = new JButton(label);
        button.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    callback.run();
                }
            }
        );
        return new Button(button, callback);
    }

    public static class Button {
        private final JButton component;
        private final Runnable callback;

        public Button(JButton component, Runnable callback) {
            this.component = component;
            this.callback = callback;
        }

        public JButton getComponent() {
            return component;
        }

        public Runnable getCallback() {
            return callback;
        }
    }
}
