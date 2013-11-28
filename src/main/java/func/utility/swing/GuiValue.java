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

    public String getLabelText() {
        return label.getText();
    }

    public JLabel getLabel() {
        return label;
    }

    public abstract void addChangeListener(ChangeListener changeListener);

    public abstract void removeChangeListener(ChangeListener changeListener);

    public abstract JComponent getInputWidget();

    public abstract Object getValue();

    /** This is mainly useful to test the gui.
     *  The argument must be of the right type.
     *  Example String for JTextWidgets and Boolean for JCheckBox.
     */
    public void setValueInWidget(Object object) {
        JComponent inputWidget = getInputWidget();
        if (inputWidget instanceof JTextField) {
            ((JTextField) inputWidget).setText((String) object);
        } else if (inputWidget instanceof JCheckBox) {
            inputWidget.setEnabled((Boolean) object);
        } else if (inputWidget instanceof JComboBox) {
            ((JComboBox) inputWidget).setSelectedItem(object);
        } else {
            throw new RuntimeException("Implement me");
        }
    }

    public abstract void clearValue();

}
