package func.utility.swing;

public class GuiStringValue extends GuiTextValue {

    public GuiStringValue(String labelText) {
        super(labelText);
    }

    public String getValue() {
        return getInputWidget().getText();
    }

    public void setValue(String text) {
        getInputWidget().setText(text);
    }

}
