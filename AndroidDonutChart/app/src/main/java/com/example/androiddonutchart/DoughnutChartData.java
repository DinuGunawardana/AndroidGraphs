package com.example.androiddonutchart;

public class DoughnutChartData {
    private String category;
    private float value;

    public DoughnutChartData() {
        // Default constructor required for calls to DataSnapshot.getValue(DoughnutChartData.class)
    }

    public DoughnutChartData(String category, float value) {
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

