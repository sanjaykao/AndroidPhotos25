package com.example.androidphotos25;

import java.io.Serializable;

public class Tag implements Serializable {
    public String name;
    public String value;

    public Tag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}