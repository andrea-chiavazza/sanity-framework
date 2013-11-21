package func.utility.swing;

public class GuiIntegerValue extends GuiTextValue {
    private long value;

    public GuiIntegerValue(String label) {
        super(label);
    }

    public String format(long value) {
        return Long.toString(value);
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
