package com.example.androidparetochart;

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
import com.anychart.core.cartesian.series.Column;
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

        databaseReference = FirebaseDatabase.getInstance().getReference("paretoChartData");

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
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "submitData: Empty values");
            return;
        }

        int value = Integer.parseInt(valueStr);

        Log.d(TAG, "submitData: Submitting data - Category: " + category + ", Value: " + value);

        ParetoChartData paretoChartData = new ParetoChartData(category, value);
        databaseReference.push().setValue(paretoChartData).addOnCompleteListener(task -> {
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
                List<DataEntry> data = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ParetoChartData paretoChartData = snapshot.getValue(ParetoChartData.class);
                    if (paretoChartData != null) {
                        data.add(new ValueDataEntry(paretoChartData.getCategory(), paretoChartData.getValue()));
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
        Cartesian cartesian = AnyChart.cartesian();

        cartesian.animation(true);
        cartesian.title("Pareto Chart");

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(com.anychart.enums.HoverMode.BY_X);

        cartesian.xAxis(0).title("Category");
        cartesian.yAxis(0).title("Value");

        anyChartView.setChart(cartesian);
    }
}