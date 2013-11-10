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

    public String format() {
        return format.format(value);
    }

    public void setValue(Number o) {
        value = (o == null ? null : new BigDecimal(o.toString()));
        getInputWidget().setText(format());
    }

    public BigDecimal getValue() {
        String labelText = getInputWidget().getText();
        if (labelText.equals(String.valueOf(value))) {
            return value;
        } else if (labelText.equals("")) {
            return null;
        } else {
            value = (new BigDecimal(labelText));
        }
        return value;
    }

}
