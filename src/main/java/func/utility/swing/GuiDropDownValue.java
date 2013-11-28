package func.utility.swing;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class GuiDropDownValue<T> extends GuiValue {
    private final JComboBox<T> comboBox;

    private final Map<ChangeListener,ActionListener> listenerMap =
        new HashMap<>();

    private ChangeEvent changeEvent;

    public GuiDropDownValue(String labelText,
                            Collection<T> list) {
        super(labelText);
        comboBox = new JComboBox<>(
            new DefaultComboBoxModel<>(new Vector<>(list)));
        comboBox.setSelectedItem(null);
    }

    public void setSelectionList(Collection<T> list) {
        comboBox.setModel(new DefaultComboBoxModel<>(new Vector<>(list)));
    }

    public PVector<T> getSelectionList() {
        ComboBoxModel<T> comboBoxModel = comboBox.getModel();
        List<T> list = new ArrayList<>();
        int size = comboBoxModel.getSize();
        for (int i = 0; i < size; i++) {
            list.add(comboBoxModel.getElementAt(i));
        }
        return TreePVector.from(list);
    }

    public void addChangeListener(final ChangeListener changeListener) {
        if (changeEvent == null) {
            changeEvent = new ChangeEvent(this);
        }
        ActionListener actionListener =
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    changeListener.stateChanged(changeEvent);
                }
            };
        listenerMap.put(changeListener, actionListener);
        comboBox.addActionListener(actionListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        if (listenerMap.containsKey(changeListener)) {
            ActionListener actionListener = listenerMap.get(changeListener);
            comboBox.removeActionListener(actionListener);
            listenerMap.remove(changeListener);
        }
    }

    public JComponent getInputWidget() {
        return comboBox;
    }

    public T getValue() {
        return (T) comboBox.getSelectedItem();
    }

    public void setValue(T t) {
        comboBox.setSelectedItem(t);
    }

    public void clearValue() {
        comboBox.setSelectedItem(null);
    }

}
