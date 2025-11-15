package com.example.smartwaste;  // <-- change this to your package name

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Firestore init
        db = FirebaseFirestore.getInstance();

        // Map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Default camera start (adjust to your area)
        LatLng campus = new LatLng(-1.3031, 36.7073);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campus, 15));

        // Load bins from Firestore
        db.collection("bins").get().addOnSuccessListener((QuerySnapshot snapshots) -> {
            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                Double lat = doc.getDouble("latitude");
                Double lng = doc.getDouble("longitude");
                String description = doc.getString("description");

                if (lat != null && lng != null && description != null) {
                    LatLng position = new LatLng(lat, lng);

                    // Add custom marker with bin name
                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .icon(createCustomMarker(description)) // custom icon
                            .title(description));

                    Log.d("MapActivity", "Marker added: " + description + " at " + lat + "," + lng);
                }
            }
        }).addOnFailureListener(e ->
                Log.e("MapActivity", "Error loading bins", e));
    }

    /**
     * Creates a custom marker with bin name written inside a rounded rectangle
     */
    private BitmapDescriptor createCustomMarker(String binName) {
        Paint paint = new Paint();
        paint.setTextSize(40);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAntiAlias(true);

        // measure text width + padding
        int width = (int) (paint.measureText(binName) + 40);
        int height = 80;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Background with rounded corners
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
        RectF rect = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rect, 20, 20, backgroundPaint);

        // Border
        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.DKGRAY);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);
        borderPaint.setAntiAlias(true);
        canvas.drawRoundRect(rect, 20, 20, borderPaint);

        // Draw text (bin name)
        canvas.drawText(binName, 20, height / 2 + 15, paint);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
