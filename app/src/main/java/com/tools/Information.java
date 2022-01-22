package com.tools;

import java.util.ArrayList;

public class Information {
    private final ArrayList<Object> list = new ArrayList<>();

    public void putInfo(Object object) {
        list.add(object);
    }

    public Object getInfo(int i) {
        return list.get(i);
    }
}
