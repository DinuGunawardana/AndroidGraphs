package com.example.androiddonutchart;

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
import com.anychart.charts.Pie;
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

        databaseReference = FirebaseDatabase.getInstance().getReference("doughnutChartData");

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

        DoughnutChartData doughnutChartData = new DoughnutChartData(category, value);
        databaseReference.push().setValue(doughnutChartData).addOnCompleteListener(task -> {
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
                List<DoughnutChartData> doughnutChartDataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DoughnutChartData doughnutChartData = snapshot.getValue(DoughnutChartData.class);
                    if (doughnutChartData != null) {
                        doughnutChartDataList.add(doughnutChartData);
                    }
                }
                updateChart(doughnutChartDataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed to fetch data", databaseError.toException());
            }
        });
    }

    private void updateChart(List<DoughnutChartData> doughnutChartDataList) {
        Pie doughnut = AnyChart.pie();
        doughnut.innerRadius("40%"); // This makes it a doughnut chart

        List<DataEntry> data = new ArrayList<>();
        for (DoughnutChartData doughnutChartData : doughnutChartDataList) {
            data.add(new ValueDataEntry(doughnutChartData.getCategory(), doughnutChartData.getValue()));
        }

        doughnut.data(data);
        doughnut.title("Doughnut Chart");

        anyChartView.setChart(doughnut);
    }
}