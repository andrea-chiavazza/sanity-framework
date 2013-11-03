package func.utility.swing;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GuiValueGroup {
    private final PVector<? extends GuiValue> guiValues;
    private final JPanel panel;

    public GuiValueGroup(GuiValue... guiValues) {
        panel = new JPanel(new GridBagLayout());
        this.guiValues = TreePVector.from(Arrays.asList(guiValues));
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 3, 0, 3);
        c.gridy = 0;
        for (GuiValue guiValue : this.guiValues) {
            c.gridx = 0;
            panel.add(guiValue.getLabel(), c);
            c.gridx = 1;
            panel.add(guiValue.getInputWidget(), c);
            c.gridy++;
        }
    }

//    public GuiValueGroup(String... labels) {
//        this(
//            MapFunc.map(
//                new F1<String,GuiValue>() {
//                    public GuiValue execute(String label) {
//                        return new GuiValue(label);
//                    }
//                },
//                labels).toArray(new GuiValue[labels.length]);
//    }

//    public void setValues(List<?> values) {
//        if (values.size() != fields.size()) {
//            throw new IllegalArgumentException("Wrong number of values.");
//        }
//        for (int i = 0; i < fields.size(); i++) {
//            fields.get(i).setValue(values.get(i));
//        }
//    }
//
//    public PVector<?> getValues() {
//        return TreePVector.from(
//            MapFunc.map(
//                new F1<GuiDecimalValue,Object>() {
//                    public Object execute(GuiDecimalValue guiDecimalValue) {
//                        return guiDecimalValue.getValue();
//                    }
//                },
//                fields));
//    }

    public JComponent getComponent() {
        return panel;
    }

}
