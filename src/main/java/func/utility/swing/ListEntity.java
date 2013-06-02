package func.utility.swing;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

public class ListEntity extends JToggleButton {
    private String name;
    private Runnable whenSelected;
    private ItemListener whenCheckedChanged;
    private List<? extends Action> menuEntities = new ArrayList<>();
    private boolean isChecked;

    public ListEntity(String name,
                      boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChecked(boolean value) {
        if (isChecked != value) {
            isChecked = value;
            if (whenCheckedChanged != null) {
                whenCheckedChanged.itemStateChanged(
                    new ItemEvent(
                        this,
                        ItemEvent.ITEM_FIRST,
                        null,
                        value ? ItemEvent.SELECTED : ItemEvent.DESELECTED));
            }
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setMenuEntities(List<? extends Action> menuEntities) {
        this.menuEntities = menuEntities;
    }

    public List<? extends Action> getMenuEntities() {
        return menuEntities;
    }

    public void setWhenSelected(Runnable whenSelected) {
        this.whenSelected = whenSelected;
    }

    public void setWhenCheckedChanged(ItemListener whenCheckedChanged) {
        this.whenCheckedChanged = whenCheckedChanged;
    }

    public ItemListener getWhenCheckedChanged() {
        return whenCheckedChanged;
    }

    public String getName() {
        return name;
    }

    public Runnable getWhenSelected() {
        return whenSelected;
    }

    public String toString() {
        return name;
    }
}
