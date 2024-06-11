package com.example.androidtreemap;

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
import com.anychart.chart.common.dataentry.TreeDataEntry;
import com.anychart.charts.TreeMap;
import com.anychart.core.ui.Tooltip;
import com.anychart.enums.TooltipDisplayMode;
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
    private EditText editTextCategory, editTextValue;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anyChartView = findViewById(R.id.any_chart_view);
        editTextCategory = findViewById(R.id.editTextCategory);
        editTextValue = findViewById(R.id.editTextValue);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        databaseReference = FirebaseDatabase.getInstance().getReference("treeMapData");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

        fetchData();
    }

    private void submitData() {
        String category = editTextCategory.getText().toString().trim();
        String valueStr = editTextValue.getText().toString().trim();

        if (category.isEmpty() || valueStr.isEmpty()) {
            Toast.makeText(this, "Please enter category and value", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "submitData: Empty values");
            return;
        }

        float value = Float.parseFloat(valueStr);

        Log.d(TAG, "submitData: Submitting values - Category: " + category + ", Value: " + value);

        TreeMapData treeMapData = new TreeMapData(category, value);
        databaseReference.push().setValue(treeMapData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "submitData: Data submitted successfully");
            } else {
                Log.d(TAG, "submitData: Failed to submit data", task.getException());
            }
        });

        editTextCategory.setText("");
        editTextValue.setText("");
    }

    private void fetchData() {
        Log.d(TAG, "fetchData: Fetching data from Firebase");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<TreeMapData> treeMapDataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TreeMapData treeMapData = snapshot.getValue(TreeMapData.class);
                    if (treeMapData != null) {
                        treeMapDataList.add(treeMapData);
                    }
                }
                updateChart(treeMapDataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed to fetch data", databaseError.toException());
            }
        });
    }

    private void updateChart(List<TreeMapData> treeMapDataList) {
        TreeMap treeMap = AnyChart.treeMap();

        List<DataEntry> data = new ArrayList<>();
        for (TreeMapData treeMapData : treeMapDataList) {
            data.add(new CustomTreeDataEntry(treeMapData.getCategory(), null, treeMapData.getValue()));
        }

        treeMap.data(data);

        treeMap.title("Tree Map Chart");

        Tooltip tooltip = treeMap.tooltip();
        tooltip.titleFormat("{%name}");
        tooltip.format("{%value}");
        tooltip.positionMode(TooltipPositionMode.POINT);
        tooltip.displayMode(TooltipDisplayMode.SINGLE);

        anyChartView.setChart(treeMap);
    }

    private static class CustomTreeDataEntry extends TreeDataEntry {
        CustomTreeDataEntry(String id, String parent, float value) {
            super(id, parent, (int) value); // Convert float to int
        }
    }
}