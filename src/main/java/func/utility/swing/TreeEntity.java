package func.utility.swing;

import func.basic.F1;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeEntity extends DefaultMutableTreeNode {
    private String name;
    private Runnable whenSelected;
    private Runnable whenExpanded;
    private Runnable whenCollapsed;
    private Runnable beforeWillExpand;
    private Runnable beforeWillCollapse;
    private MenuActionMap menuActions;
    private F1<TreeEntity,MenuActionMap> popupEntriesMaker;

    public TreeEntity(Object object,
                      String name) {
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
        setUserObject(object);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMenuActions(MenuActionMap menuActions) {
        this.menuActions = menuActions;
    }

    public void setMenuActionsMaker(F1<TreeEntity,MenuActionMap> popupEntriesMaker) {
        this.popupEntriesMaker = popupEntriesMaker;
    }

    public F1<TreeEntity,MenuActionMap> getMenuActionsMaker() {
        return popupEntriesMaker;
    }

    public MenuActionMap getMenuActions() {
        return menuActions;
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
