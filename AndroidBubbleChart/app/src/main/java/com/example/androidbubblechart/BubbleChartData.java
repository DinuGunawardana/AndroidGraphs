package com.example.androidbubblechart;

public class BubbleChartData {
    private double x;
    private double y;
    private double size;

    public BubbleChartData() {
        // Default constructor required for calls to DataSnapshot.getValue(BubbleChartData.class)
    }

    public BubbleChartData(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }
}


