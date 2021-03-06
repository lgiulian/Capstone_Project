package com.crilu.opengotha.components;

import java.util.ArrayList;
import java.util.List;

public class JPanel {

    private boolean visible;

    private List<String> components = new ArrayList<>();

    public void add(String label) {
        components.add(label);
    }

    public void remove(String label) {
        components.remove(label);
    }

    public void setVisible(boolean b) {
        this.visible = b;
    }
}
