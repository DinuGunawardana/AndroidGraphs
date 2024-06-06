package com.example.androidlinegraph;

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
import com.anychart.charts.Cartesian;
import com.anychart.enums.Anchor;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
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
    private EditText editTextValue;
    private Button buttonSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anyChartView = findViewById(R.id.any_chart_view);
        editTextValue = findViewById(R.id.editTextValue);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        databaseReference = FirebaseDatabase.getInstance().getReference("lineData");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

        fetchData();
    }

    private void submitData() {
        String valueStr = editTextValue.getText().toString().trim();

        if (valueStr.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "submitData: Empty value");
            return;
        }

        float value = Float.parseFloat(valueStr);

        Log.d(TAG, "submitData: Submitting value: " + value);

        long timestamp = System.currentTimeMillis();

        LineData lineData = new LineData(timestamp, value);
        databaseReference.child(String.valueOf(timestamp)).setValue(lineData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "submitData: Data submitted successfully");
            } else {
                Log.d(TAG, "submitData: Failed to submit data", task.getException());
            }
        });

        editTextValue.setText("");
    }

    private void fetchData() {
        Log.d(TAG, "fetchData: Fetching data from Firebase");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<LineData> lineDataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LineData lineData = snapshot.getValue(LineData.class);
                    if (lineData != null) {
                        lineDataList.add(lineData);
                    }
                }
                updateChart(lineDataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed to fetch data", databaseError.toException());
            }
        });
    }

    private void updateChart(List<LineData> lineDataList) {
        Cartesian cartesian = AnyChart.line();

        List<DataEntry> data = new ArrayList<>();
        for (LineData lineData : lineDataList) {
            data.add(new ValueDataEntry(lineData.getTimestamp(), lineData.getValue()));
        }

        cartesian.data(data);

        cartesian.animation(true);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.title("Line Graph");

        cartesian.yAxis(0).title("Value");
        cartesian.xAxis(0).labels().format("{%Value}");

        cartesian.tooltip()
                .position(Position.RIGHT_TOP)
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        anyChartView.setChart(cartesian);
    }
}