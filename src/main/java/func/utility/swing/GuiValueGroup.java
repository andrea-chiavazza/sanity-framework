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
    private PVector<? extends GuiValue> guiValues;
    private JPanel panel;

    public abstract List<? extends GuiValue> getGuiValues();

    public abstract T getValue();

    public abstract void setValue(T t);

    private final List<ChangeListener> changeListeners = new ArrayList<>();

    //todo: use EventListenerList ?
    private ChangeEvent changeEvent = null;

    public void clearAllValues() {
        for (GuiValue guiValue : guiValues) {
            guiValue.clearValue();
        }
    }

    public JComponent getComponent() {
        if (panel == null) {
            guiValues = TreePVector.from(getGuiValues());
            panel = new JPanel(new GridBagLayout());
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
        ChangeListener changeListener = new ChangeListener() {
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
        return panel;
    }

    public void addChangeListener(final ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }

}
