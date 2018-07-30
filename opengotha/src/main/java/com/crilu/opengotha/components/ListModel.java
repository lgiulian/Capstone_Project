package com.crilu.opengotha.components;

public class ListModel {

    private Object[] data;

    public ListModel(Object[] objects) {
        data = objects;
    }

    public int getSize() {
        return data.length;
    }

    public Object getElementAt(int i) {
        return i>=0? data[i]: null;
    }
}
