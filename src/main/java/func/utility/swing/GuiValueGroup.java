package func.utility.swing;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiValueGroup<T> {
    protected PVector<? extends GuiValue> guiValues;

    public abstract T getValue();

    public abstract void setValue(T t);

    //todo: use EventListenerList ?
    private final List<ChangeListener> changeListeners = new ArrayList<>();

    private ChangeEvent changeEvent = null;

    public void setGuiValues(List<? extends GuiValue> guiValues) {
        this.guiValues = TreePVector.from(guiValues);
        setChangeListeners(guiValues);
    }

    public void clearAllValues() {
        for (GuiValue guiValue : guiValues) {
            guiValue.clearValue();
        }
    }

    public static JPanel makePanel(List<? extends GuiValue> guiValues) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        for (GuiValue guiValue : guiValues) {
            c.gridx = 0;
            c.weightx = 0.0;
            c.insets = new Insets(0, 5, 0, 2);
            panel.add(guiValue.getLabel(), c);
            c.gridx = 1;
            c.weightx = 1.0;
            c.insets = new Insets(0, 2, 0, 5);
            panel.add(guiValue.getInputWidget(), c);
            c.gridy++;
        }
        return panel;
    }

    /** Each valueList will be displayed in a column.
     *  A null GuiValue can be used to have an empty cell. */
    public static JPanel makeGridPanel(List<? extends GuiValue>... valuesLists) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        for (List<? extends GuiValue> valuesList : valuesLists) {
            c.gridy = 0;
            for (GuiValue guiValue : valuesList) {
                if (guiValue != null) {
                    c.weightx = 0.0;
                    c.insets = new Insets(0, 5, 0, 2);
                    panel.add(guiValue.getLabel(), c);
                    c.gridx++;
                    c.weightx = 1.0;
                    c.insets = new Insets(0, 2, 0, 5);
                    panel.add(guiValue.getInputWidget(), c);
                    c.gridx--;
                }
                c.gridy++;
            }
            c.gridx += 2;
        }
        return panel;
    }

    private void setChangeListeners(List<? extends GuiValue> guiValues) {
        ChangeListener changeListener =
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (changeEvent == null) {
                        changeEvent = new ChangeEvent(GuiValueGroup.this);
                    }
                    for (ChangeListener changeListener : changeListeners) {
                        changeListener.stateChanged(changeEvent);
                    }
                }
            };
        for (GuiValue guiValue : guiValues) {
            guiValue.addChangeListener(changeListener);
        }
    }

    //TODO: use JPanel or JComponent ?
//    public JComponent getComponent() {
//        if (panel == null) {
//            if (guiValues == null) {
//                guiValues = TreePVector.from(getGuiValues());
//            }
//            panel = makePanel(guiValues);
//        }
//        return panel;
//    }

    public void addChangeListener(ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }

    public PVector<? extends GuiValue> getGuiValues() {
        return guiValues;
    }

    /** Useful for testing. */
    public void setField(String label,
                         Object value) {
        for (GuiValue guiValue : guiValues) {
            if (guiValue.getLabelText().equals(label)) {
                guiValue.setValueInWidget(value);
            }
        }
    }

}
