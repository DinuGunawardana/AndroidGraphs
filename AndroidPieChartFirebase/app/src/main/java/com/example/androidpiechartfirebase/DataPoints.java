package com.example.androidpiechartfirebase;

public class DataPoints {
    private long timestamp;
    private float value;

    public DataPoints() {
        // Default constructor required for calls to DataSnapshot.getValue(DataPoint.class)
    }

    public DataPoints(long timestamp, float value) {
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

