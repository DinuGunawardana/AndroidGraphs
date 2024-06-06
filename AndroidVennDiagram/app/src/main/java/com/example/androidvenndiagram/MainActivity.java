package com.example.androidvenndiagram;

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
import com.anychart.charts.Venn;
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
    private EditText editTextSet1, editTextSet2, editTextOverlap;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anyChartView = findViewById(R.id.any_chart_view);
        editTextSet1 = findViewById(R.id.editTextSet1);
        editTextSet2 = findViewById(R.id.editTextSet2);
        editTextOverlap = findViewById(R.id.editTextOverlap);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        databaseReference = FirebaseDatabase.getInstance().getReference("vennData");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

        fetchData();
    }

    private void submitData() {
        String set1Str = editTextSet1.getText().toString().trim();
        String set2Str = editTextSet2.getText().toString().trim();
        String overlapStr = editTextOverlap.getText().toString().trim();

        if (set1Str.isEmpty() || set2Str.isEmpty() || overlapStr.isEmpty()) {
            Toast.makeText(this, "Please enter all values", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "submitData: Empty values");
            return;
        }

        float set1 = Float.parseFloat(set1Str);
        float set2 = Float.parseFloat(set2Str);
        float overlap = Float.parseFloat(overlapStr);

        Log.d(TAG, "submitData: Submitting values - Set1: " + set1 + ", Set2: " + set2 + ", Overlap: " + overlap);

        VennData vennData = new VennData(set1, set2, overlap);
        databaseReference.setValue(vennData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "submitData: Data submitted successfully");
            } else {
                Log.d(TAG, "submitData: Failed to submit data", task.getException());
            }
        });

        editTextSet1.setText("");
        editTextSet2.setText("");
        editTextOverlap.setText("");
    }

    private void fetchData() {
        Log.d(TAG, "fetchData: Fetching data from Firebase");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                VennData vennData = dataSnapshot.getValue(VennData.class);
                if (vennData != null) {
                    Log.d(TAG, "onDataChange: Data fetched - Set1: " + vennData.getSet1() + ", Set2: " + vennData.getSet2() + ", Overlap: " + vennData.getOverlap());
                    updateChart(vennData.getSet1(), vennData.getSet2(), vennData.getOverlap());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed to fetch data", databaseError.toException());
            }
        });
    }

    private void updateChart(float set1, float set2, float overlap) {
        Venn venn = AnyChart.venn();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("A", set1));
        data.add(new ValueDataEntry("B", set2));
        data.add(new ValueDataEntry("A&B", overlap));

        venn.data(data);

        anyChartView.setChart(venn);
    }
}