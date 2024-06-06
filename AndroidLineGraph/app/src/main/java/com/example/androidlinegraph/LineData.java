package com.example.androidlinegraph;

public class LineData {
    private long timestamp;
    private float value;

    public LineData() {
        // Default constructor required for calls to DataSnapshot.getValue(LineData.class)
    }

    public LineData(long timestamp, float value) {
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

