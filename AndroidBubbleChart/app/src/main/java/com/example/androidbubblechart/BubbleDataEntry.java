package com.example.androidbubblechart;

import com.anychart.chart.common.dataentry.DataEntry;

public class BubbleDataEntry extends DataEntry {
    public BubbleDataEntry(double x, double y, double size) {
        setValue("x", x);
        setValue("value", y);
        setValue("size", size);
    }
}
