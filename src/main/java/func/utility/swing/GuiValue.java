package func.utility.swing;

import javax.swing.*;

public abstract class GuiValue {
    private final JLabel label = new JLabel();

    public GuiValue(String labelText) {
        setLabelText(labelText);
    }

    public void setLabelText(String text) {
        label.setText(text);
    }

    public JLabel getLabel() {
        return label;
    }

    public abstract JComponent getInputWidget();

    public abstract Object getValue();

}
