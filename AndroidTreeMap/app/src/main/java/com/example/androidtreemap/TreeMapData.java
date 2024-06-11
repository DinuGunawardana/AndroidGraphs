package com.example.androidtreemap;

public class TreeMapData {
    private String category;
    private float value;

    public TreeMapData() {
        // Default constructor required for calls to DataSnapshot.getValue(TreeMapData.class)
    }

    public TreeMapData(String category, float value) {
        this.category = category;
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}

