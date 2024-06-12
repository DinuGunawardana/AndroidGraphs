package com.example.androidparetochart;

public class ParetoChartData {
    private String category;
    private int value;

    public ParetoChartData() {
        // Default constructor required for calls to DataSnapshot.getValue(ParetoChartData.class)
    }

    public ParetoChartData(String category, int value) {
        this.category = category;
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

