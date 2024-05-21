package com.example.androidlinechart_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private LineChart lineChart;
    private DatabaseReference databaseReference;
    private EditText editTextTimestamp, editTextValue;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = findViewById(R.id.lineChart);
        editTextTimestamp = findViewById(R.id.editTextTimestamp);
        editTextValue = findViewById(R.id.editTextValue);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        databaseReference = FirebaseDatabase.getInstance().getReference("dataPoints");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
        
        fetchData();
    }

    private void submitData() {
        String timestampStr = editTextTimestamp.getText().toString().trim();
        String valueStr = editTextValue.getText().toString().trim();

        if (timestampStr.isEmpty() || valueStr.isEmpty()) {
            Toast.makeText(this, "Please enter both timestamp and value", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = Long.parseLong(timestampStr);
        float value = Float.parseFloat(valueStr);

        DataPoint dataPoint = new DataPoint(timestamp, value);
        databaseReference.push().setValue(dataPoint);

        editTextTimestamp.setText("");
        editTextValue.setText("");
    }

    private void fetchData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Entry> entries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataPoint dataPoint = snapshot.getValue(DataPoint.class);
                    if (dataPoint != null) {
                        entries.add(new Entry(dataPoint.getTimestamp(), dataPoint.getValue()));
                    }
                }
                LineDataSet dataSet = new LineDataSet(entries, "Data Points");
                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);
                lineChart.invalidate(); // refresh
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}