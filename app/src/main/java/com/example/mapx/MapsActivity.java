package com.example.mapx;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private Marker mMarker;
    private double mLatitude;
    private double mLongitude;
    private static final int SMS_PERMISSION_CODE = 100;


    private GoogleMap mMap;
    private EditText mPhoneNumberEditText;
    private EditText mMessageEditText;
    private Button mEnterButton;
    private static final int REQUEST_SEND_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);
        LatLng latLng = new LatLng(latitude, longitude);

        // Add marker to map


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mPhoneNumberEditText = findViewById(R.id.phone_number_textbox);
        mMessageEditText = findViewById(R.id.message);
        mEnterButton = findViewById(R.id.enter_button);


        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = mPhoneNumberEditText.getText().toString();
                String message = mMessageEditText.getText().toString();

                if (phoneNumber.isEmpty() || message.isEmpty()) {
                    Toast.makeText(MapsActivity.this, "Please enter a phone number and message", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        sendSMS(phoneNumber, message);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS);
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);
        LatLng latLng = new LatLng(latitude, longitude);
        mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("SMS Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        // Add a marker in Sydney and move the camera

    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String phoneNumber = mPhoneNumberEditText.getText().toString();
                String message = mMessageEditText.getText().toString();
                sendSMS(phoneNumber, message);
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



}