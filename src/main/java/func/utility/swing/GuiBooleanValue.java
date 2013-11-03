package func.utility.swing;

import javax.swing.*;

public class GuiBooleanValue extends GuiValue {
    private final JCheckBox checkBox;

    public GuiBooleanValue(String labelText) {
        super(labelText);
        this.checkBox = new JCheckBox();
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

}
