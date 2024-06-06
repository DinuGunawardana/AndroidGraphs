package com.example.androidvenndiagram;

public class VennData {
    private float set1;
    private float set2;
    private float overlap;

    public VennData() {
        // Default constructor required for calls to DataSnapshot.getValue(VennData.class)
    }

    public VennData(float set1, float set2, float overlap) {
        this.set1 = set1;
        this.set2 = set2;
        this.overlap = overlap;
    }

    public float getSet1() {
        return set1;
    }

    public void setSet1(float set1) {
        this.set1 = set1;
    }

    public float getSet2() {
        return set2;
    }

    public void setSet2(float set2) {
        this.set2 = set2;
    }

    public float getOverlap() {
        return overlap;
    }

    public void setOverlap(float overlap) {
        this.overlap = overlap;
    }
}

