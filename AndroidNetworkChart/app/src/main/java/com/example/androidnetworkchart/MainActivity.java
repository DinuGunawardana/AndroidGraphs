package com.example.androidnetworkchart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DatabaseReference databaseReference;
    private EditText editTextNodeId, editTextNodeName, editTextNodeTargets;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNodeId = findViewById(R.id.editTextNodeId);
        editTextNodeName = findViewById(R.id.editTextNodeName);
        editTextNodeTargets = findViewById(R.id.editTextNodeTargets);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        databaseReference = FirebaseDatabase.getInstance().getReference("networkChartData");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

        fetchData();
    }

    private void submitData() {
        String nodeId = editTextNodeId.getText().toString().trim();
        String nodeName = editTextNodeName.getText().toString().trim();
        String nodeTargetsStr = editTextNodeTargets.getText().toString().trim();

        if (nodeId.isEmpty() || nodeName.isEmpty() || nodeTargetsStr.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "submitData: Empty values");
            return;
        }

        List<String> nodeTargets = Arrays.asList(nodeTargetsStr.split(","));

        Log.d(TAG, "submitData: Submitting node - ID: " + nodeId + ", Name: " + nodeName + ", Targets: " + nodeTargets);

        NetworkChartNode networkChartNode = new NetworkChartNode(nodeId, nodeName, nodeTargets);
        databaseReference.child(nodeId).setValue(networkChartNode).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "submitData: Data submitted successfully");
            } else {
                Log.d(TAG, "submitData: Failed to submit data", task.getException());
            }
        });

        editTextNodeId.setText("");
        editTextNodeName.setText("");
        editTextNodeTargets.setText("");
    }

    private void fetchData() {
        Log.d(TAG, "fetchData: Fetching data from Firebase");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Custom implementation for rendering network chart using fetched data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed to fetch data", databaseError.toException());
            }
        });
    }
}