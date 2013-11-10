package func.utility.swing;

import javax.swing.*;
import javax.swing.event.ChangeListener;

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

    public abstract void addChangeListener(ChangeListener changeListener);

    public abstract void removeChangeListener(ChangeListener changeListener);

    public abstract JComponent getInputWidget();

    public abstract Object getValue();

    public abstract void clearValue();

}
