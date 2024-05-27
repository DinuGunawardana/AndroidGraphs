package com.example.androidcolumnchartfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BarChart barChart;
    private DatabaseReference databaseReference;
    private EditText editTextValue;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barChart = findViewById(R.id.barChart);
        editTextValue = findViewById(R.id.editTextValue);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        databaseReference = FirebaseDatabase.getInstance().getReference("columnchartdataPoints");

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

        long timestamp = System.currentTimeMillis();
        float value = Float.parseFloat(valueStr);

        Log.d(TAG, "submitData: Submitting data point - Timestamp: " + timestamp + ", Value: " + value);

        DataPoint dataPoint = new DataPoint(timestamp, value);
        databaseReference.push().setValue(dataPoint).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "submitData: Data point submitted successfully");
            } else {
                Log.d(TAG, "submitData: Failed to submit data point", task.getException());
            }
        });

        editTextValue.setText("");
    }

    private void fetchData() {
        Log.d(TAG, "fetchData: Fetching data from Firebase");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<BarEntry> entries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataPoint dataPoint = snapshot.getValue(DataPoint.class);
                    if (dataPoint != null) {
                        Log.d(TAG, "onDataChange: Data point fetched - Timestamp: " + dataPoint.getTimestamp() + ", Value: " + dataPoint.getValue());
                        entries.add(new BarEntry(dataPoint.getTimestamp(), dataPoint.getValue()));
                    }
                }
                BarDataSet dataSet = new BarDataSet(entries, "Data Points");
                BarData barData = new BarData(dataSet);
                barChart.setData(barData);
                barChart.invalidate(); // refresh
                Log.d(TAG, "onDataChange: Bar chart updated with new data");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed to fetch data", databaseError.toException());
            }
        });
    }
}