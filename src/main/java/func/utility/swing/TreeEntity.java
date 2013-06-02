package func.utility.swing;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collections;
import java.util.List;

public class TreeEntity extends DefaultMutableTreeNode {
    private String name;
    private Runnable whenSelected;
    private Runnable whenExpanded;
    private Runnable whenCollapsed;
    private Runnable beforeWillExpand;
    private Runnable beforeWillCollapse;
    private List<? extends Action> menuEntities = Collections.emptyList();

    public TreeEntity(String name) {
        this.name = name;
        Runnable doNothing = new Runnable() {
            public void run() {
            }
        };
        whenSelected = doNothing;
        whenExpanded = doNothing;
        whenCollapsed = doNothing;
        beforeWillExpand = doNothing;
        beforeWillCollapse = doNothing;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setWhenExpanded(Runnable whenExpanded) {
        this.whenExpanded = whenExpanded;
    }

    public void setWhenCollapsed(Runnable whenCollapsed) {
        this.whenCollapsed = whenCollapsed;
    }

    public void setBeforeWillExpand(Runnable beforeWillExpand) {
        this.beforeWillExpand = beforeWillExpand;
    }

    public void setBeforeWillCollapse(Runnable beforeWillCollapse) {
        this.beforeWillCollapse = beforeWillCollapse;
    }

    public String getName() {
        return name;
    }

    public Runnable getWhenSelected() {
        return whenSelected;
    }

    public Runnable getWhenExpanded() {
        return whenExpanded;
    }

    public Runnable getWhenCollapsed() {
        return whenCollapsed;
    }

    public Runnable getBeforeWillExpand() {
        return beforeWillExpand;
    }

    public Runnable getBeforeWillCollapse() {
        return beforeWillCollapse;
    }

    public String toString() {
        return name;
    }
}
