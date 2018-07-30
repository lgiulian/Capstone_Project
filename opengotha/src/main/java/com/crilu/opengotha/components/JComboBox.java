package com.crilu.opengotha.components;

import java.util.ArrayList;
import java.util.Vector;

public class JComboBox {

    private ComboBoxModel model;

    public ComboBoxModel getModel() {
        return model;
    }

    public void setModel(DefaultComboBoxModel model) {
        this.model = new ComboBoxModel();
        this.model.dataModel = new ArrayList<>();
        String[] data = model.getModelData();
        for (int i=0; i < data.length; i++) {
            Vector<String> row = new Vector<>();
            row.add(data[i]);
            this.model.dataModel.add(row);
        }
    }
}
