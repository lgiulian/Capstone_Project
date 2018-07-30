package com.crilu.opengotha.components;

public class JList {

    private ListModel model;
    private int selectedIndex;

    public void removeAll() {
        model = new ListModel(null);
    }

    public Object getSelectedValue() {
        return model.getElementAt(selectedIndex);
    }

    public void clearSelection() {
        selectedIndex = -1;
    }

    public void setListData(String[] strings) {
        model = new ListModel(strings);
    }

    public ListModel getModel() {
        return model;
    }

    public void setSelectedIndex(int i) {
        selectedIndex = i;
    }
}
