package func.utility.swing;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GuiDecimalValue extends GuiTextValue {
    private BigDecimal value;
    private NumberFormat format;

    public GuiDecimalValue(String label,
                           int fractionDigits) {
        super(label);
        format = DecimalFormat.getInstance();
        format.setMaximumFractionDigits(fractionDigits);
    }

    private String format(BigDecimal value) {
        try {
            return format.format(value);
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
        String text = getInputWidget().getText();
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
