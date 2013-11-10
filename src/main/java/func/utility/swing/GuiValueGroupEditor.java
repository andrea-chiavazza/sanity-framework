package func.utility.swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public abstract class GuiValueGroupEditor<T> extends GuiValueGroup<T> {
    private static final int GAP = 10;

    private JPanel panel;

    protected T previousValue;

    public abstract void saveValue(T previousValue,
                                   T newValue);

    public abstract void addValue(T value);

    public abstract void removeValue(T value);

    public JPanel getComponent() {
        if (panel == null) {
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
            saveButton.setEnabled(false);
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
                        clearAllValues();
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

            addChangeListener(
                new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        T currentValue = getValue();
                        boolean changed = !currentValue.equals(previousValue);
                        saveButton.setEnabled(changed);
                        revertButton.setEnabled(changed);
                        removeButton.setEnabled(! changed);
                        addButton.setEnabled(changed);
                    }
                }
            );

            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
            JComponent fieldsComponent = super.getComponent();
            fieldsComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(fieldsComponent);
            panel.add(Box.createRigidArea(new Dimension(0, GAP)));
            bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            bottomPanel.setMinimumSize(bottomPanel.getPreferredSize());
            panel.add(bottomPanel);
//            panel.setMinimumSize(panel.getPreferredSize());
            panel.add(Box.createVerticalGlue());
        }
        return panel;
    }

    public void addChangeListener(ChangeListener changeListener) {
        for (GuiValue guiValue : getGuiValues()) {
            guiValue.addChangeListener(changeListener);
        }
    }

}
