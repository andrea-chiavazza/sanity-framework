package func.utility.swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public abstract class GuiValueGroupEditor<T> extends GuiValueGroup<T> {
    private static final int GAP = 10;

    protected T previousValue;

    public abstract void saveValue(T previousValue,
                                   T newValue);

    public abstract void addValue(T value);

    public abstract void removeValue(T value);

    public JPanel makeControlsPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));

        final JButton saveButton = Button.makeButton(
            "Save",
            new Runnable() {
                public void run() {
                    T newValue = getValue();
                    saveValue(previousValue, newValue);
                    previousValue = newValue;
                }
            });
        bottomPanel.add(saveButton);
        bottomPanel.add(Box.createRigidArea(new Dimension(GAP, 0)));

        final JButton revertButton = Button.makeButton(
            "Revert",
            new Runnable() {
                public void run() {
                    setValue(previousValue);
                }
            });
        bottomPanel.add(revertButton);
        bottomPanel.add(Box.createRigidArea(new Dimension(GAP, 0)));

        final JButton addButton = Button.makeButton(
            "Add",
            new Runnable() {
                public void run() {
                    addValue(getValue());
                }
            });
        bottomPanel.add(addButton);
        bottomPanel.add(Box.createRigidArea(new Dimension(GAP, 0)));

        final JButton removeButton = Button.makeButton(
            "Remove",
            new Runnable() {
                public void run() {
                    removeValue(getValue());
                    clearAllValues();
                }
            });
        bottomPanel.add(removeButton);

        bottomPanel.add(Box.createHorizontalGlue());

        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent ignored) {
                T currentValue = getValue();
                boolean changed = !currentValue.equals(previousValue);
                saveButton.setEnabled(changed && previousValue != null);
                revertButton.setEnabled(changed && previousValue != null);
                removeButton.setEnabled(!changed);
                addButton.setEnabled(changed);
            }
        };
        addChangeListener(changeListener);
        changeListener.stateChanged(null);
        return bottomPanel;
    }

    public JComponent makeEditorPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JComponent fieldsComponent = makePanel(guiValues);
        fieldsComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(fieldsComponent);
        panel.add(Box.createRigidArea(new Dimension(0, GAP)));
        JComponent bottomPanel = makeControlsPanel();
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomPanel.setMinimumSize(bottomPanel.getPreferredSize());
        panel.add(bottomPanel);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    public void addChangeListener(ChangeListener changeListener) {
        for (GuiValue guiValue : guiValues) {
            guiValue.addChangeListener(changeListener);
        }
    }

}
