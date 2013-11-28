package func.utility.swing;

import javax.swing.*;
import java.awt.*;

public class GuiIntegerValue extends GuiTextValue {
    private long value;

    public GuiIntegerValue(String label) {
        super(label);
        JTextField inputWidget = getInputWidget();
        inputWidget.setFont(new Font(Font.MONOSPACED,
                                     Font.PLAIN,
                                     inputWidget.getFont().getSize()));
    }

    public String format(long value) {
        return (value < 0 ? "" : " ") + Long.toString(value);
    }

    public String format() {
        return format(value);
    }

    public void setValue(long value) {
        this.value = value;
        super.setValue(format());
    }

    public Long getValue() {
        String text = getInputWidget().getText();
        if (text.equals(format(value))) {
            return value;
        } else if ("".equals(text)) {
            return null;
        } else {
            try {
                return new Long(text);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

}
