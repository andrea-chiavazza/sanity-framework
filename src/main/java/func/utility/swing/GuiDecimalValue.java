package func.utility.swing;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GuiDecimalValue extends GuiTextValue {
    private BigDecimal value;
    private NumberFormat numberFormat;

    public GuiDecimalValue(String label,
                           int fractionDigits) {
        super(label);
        numberFormat = DecimalFormat.getInstance();
        numberFormat.setMaximumFractionDigits(fractionDigits);
        JTextField inputWidget = getInputWidget();
        inputWidget.setFont(new Font(Font.MONOSPACED,
                                     Font.PLAIN,
                                     inputWidget.getFont().getSize()));
    }

    protected String format(BigDecimal value) {
        try {
            if (value == null) {
                return null;
            }
            return (value.signum() < 0 ? "" : " ") + numberFormat.format(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String format() {
        return format(value);
    }

    public void setValue(Number o) {
        value = (o == null ? null : new BigDecimal(o.toString()));
        super.setValue(format());
    }

    public BigDecimal getValue() {
        String text = getInputWidget().getText().trim();
        if ("".equals(text)) {
            return null;
        } else if (text.equals(format(value))) {
            return value;
        } else {
            try {
                return new BigDecimal(text);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

}
