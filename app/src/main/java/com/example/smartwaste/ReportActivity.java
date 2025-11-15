package com.example.smartwaste;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private EditText etReportDescription;
    private Button btnSubmitReport;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get UI elements
        etReportDescription = findViewById(R.id.etReportDescription);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);

        // Handle button click
        btnSubmitReport.setOnClickListener(v -> {
            String description = etReportDescription.getText().toString().trim();

            if (description.isEmpty()) {
                Toast.makeText(ReportActivity.this, "Please enter a description", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a report object
            Map<String, Object> report = new HashMap<>();
            report.put("description", description);
            report.put("timestamp", System.currentTimeMillis());

            // Save to Firestore (collection: reports)
            db.collection("reports")
                    .add(report)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ReportActivity.this, "Report submitted!", Toast.LENGTH_SHORT).show();
                        etReportDescription.setText(""); // clear input
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(ReportActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }
}
