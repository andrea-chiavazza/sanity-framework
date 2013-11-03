package func.utility.swing;

import javax.swing.*;

public abstract class GuiTextValue extends GuiValue {
    private final JTextField textField = new JTextField();

    public GuiTextValue(String labelText) {
        super(labelText);
        textField.setColumns(10);
    }

    public JTextField getInputWidget() {
        return textField;
    }

    public void setValue(String text) {
        textField.setText(text);
    }

}
