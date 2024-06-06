package com.example.androidscatterchart;

public class DataPoint {
    private long timestamp;
    private float value;

    public DataPoint() {
        // Default constructor required for calls to DataSnapshot.getValue(DataPoint.class)
    }

    public DataPoint(long timestamp, float value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}

