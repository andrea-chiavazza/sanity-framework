package func.utility.swing;

import javax.swing.*;
import javax.swing.event.ChangeListener;

public class GuiBooleanValue extends GuiValue {
    private final JCheckBox checkBox;

    public GuiBooleanValue(String labelText) {
        super(labelText);
        checkBox = new JCheckBox();
    }

    public JCheckBox getInputWidget() {
        return checkBox;
    }

    public Boolean getValue() {
        return checkBox.isEnabled();
    }

    public void setValue(boolean value) {
        checkBox.setEnabled(value);
    }

    public void addChangeListener(ChangeListener changeListener) {
        checkBox.addChangeListener(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        checkBox.removeChangeListener(changeListener);
    }

    public void clearValue() {
        checkBox.setEnabled(false);
    }
}
