package com.crilu.opengotha.components;

public class ComboBoxModel extends DefaultTableModel {

    private String selectedItem;

    public ComboBoxModel() {
        super();
    }

    public ComboBoxModel(String[][] objects) {
        super(objects);
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(String s) {
        selectedItem = s;
    }
}
