package com.crilu.opengotha.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class DefaultTableModel {

    protected List<Vector<String>> dataModel = new ArrayList<>();

    public DefaultTableModel() {

    }

    public DefaultTableModel(String[][] objects) {
        for (String[] object : objects) {
            dataModel.add(new Vector<>(Arrays.asList(object)));
        }
    }

    public void addRow(Vector<String> row) {
        dataModel.add(row);
    }

    public int getRowCount() {
        return dataModel.size();
    }

    public void removeRow(int i) {
        dataModel.remove(i);
    }

    public List<Vector<String>> getDataModel() {
        return dataModel;
    }
}
