package func.utility.swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiTextValue extends GuiValue {
    private final JTextField textField = new JTextField();

    //todo: use EventListenerList ?
    private final List<ChangeListener> changeListeners = new ArrayList<>();

    private ChangeEvent changeEvent = null;

    public GuiTextValue(String labelText) {
        super(labelText);
        textField.getDocument().addDocumentListener(
            new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    fireStateChanged();
                }

                public void removeUpdate(DocumentEvent e) {
                    fireStateChanged();
                }

                public void changedUpdate(DocumentEvent e) {
                    fireStateChanged();
                }
            }
        );
    }

    public JTextField getInputWidget() {
        return textField;
    }

    public void setValue(String text) {
        textField.setText(text);
        textField.setCaretPosition(0);
    }

    public void addChangeListener(final ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }

    private void fireStateChanged() {
        if (changeEvent == null) {
            changeEvent = new ChangeEvent(GuiTextValue.this);
        }
        for (ChangeListener changeListener : changeListeners) {
            changeListener.stateChanged(changeEvent);
        }
    }

    public void clearValue() {
        textField.setText("");
    }

}
