package com.example.androidbubblechart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.core.scatter.series.Bubble;
import com.anychart.enums.HoverMode;
import com.anychart.enums.TooltipDisplayMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private AnyChartView anyChartView;
    private DatabaseReference databaseReference;
    private EditText editTextXValue, editTextYValue, editTextSize;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anyChartView = findViewById(R.id.any_chart_view);
        editTextXValue = findViewById(R.id.editTextXValue);
        editTextYValue = findViewById(R.id.editTextYValue);
        editTextSize = findViewById(R.id.editTextSize);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        databaseReference = FirebaseDatabase.getInstance().getReference("bubbleChartData");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

        fetchData();
    }

    private void submitData() {
        String xValueStr = editTextXValue.getText().toString().trim();
        String yValueStr = editTextYValue.getText().toString().trim();
        String sizeStr = editTextSize.getText().toString().trim();

        if (xValueStr.isEmpty() || yValueStr.isEmpty() || sizeStr.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "submitData: Empty values");
            return;
        }

        double xValue = Double.parseDouble(xValueStr);
        double yValue = Double.parseDouble(yValueStr);
        double size = Double.parseDouble(sizeStr);

        Log.d(TAG, "submitData: Submitting data - X: " + xValue + ", Y: " + yValue + ", Size: " + size);

        BubbleChartData bubbleChartData = new BubbleChartData(xValue, yValue, size);
        databaseReference.push().setValue(bubbleChartData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "submitData: Data submitted successfully");
            } else {
                Log.d(TAG, "submitData: Failed to submit data", task.getException());
            }
        });

        editTextXValue.setText("");
        editTextYValue.setText("");
        editTextSize.setText("");
    }

    private void fetchData() {
        Log.d(TAG, "fetchData: Fetching data from Firebase");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DataEntry> data = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BubbleChartData bubbleChartData = snapshot.getValue(BubbleChartData.class);
                    if (bubbleChartData != null) {
                        data.add(new CustomBubbleDataEntry(bubbleChartData.getX(), bubbleChartData.getY(), bubbleChartData.getSize()));
                    }
                }
                renderChart(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed to fetch data", databaseError.toException());
            }
        });
    }

    private void renderChart(List<DataEntry> data) {
        com.anychart.charts.Scatter scatter = AnyChart.scatter();

        scatter.animation(true);
        scatter.title("Bubble Chart");

        Bubble series = scatter.bubble(data);
        series.name("Values")
                .hovered()
                .markers(true);

        series.labels().enabled(true);

        scatter.yScale().minimum(0d);
        scatter.xScale().minimum(0d);

        scatter.yAxis(0).title("Y Value");
        scatter.xAxis(0).title("X Value");

        scatter.interactivity().hoverMode(HoverMode.BY_X);

        scatter.tooltip().displayMode(TooltipDisplayMode.UNION);

        anyChartView.setChart(scatter);
    }

    private static class CustomBubbleDataEntry extends DataEntry {
        public CustomBubbleDataEntry(double x, double y, double size) {
            setValue("x", x);
            setValue("value", y);
            setValue("size", size);
        }
    }
}