package com.netro.troxrider.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.PolyUtil;
import com.netro.troxrider.R;
import com.netro.troxrider.util.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddressMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    CoordinatorLayout main;
    ImageView back;


    TextView orderID, senderName, contact, address, callNow;
    CardView btnContinue;

    private GoogleMap mMap;
    Marker marker;
    private Polyline mPolyline;
    private Location lastKnownLocation;
    List<Address> addresses = null;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private FusedLocationProviderClient fusedLocationProviderClient;

    double latitude;
    double longitude;
    double pickUpLat, pickUpLong;
    private LatLng mOrigin;
    private LatLng mDestination;

    String order_id;

    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    private static final String TAG = "AddressMapActivity";

    String data;

    Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_map);

        main = findViewById(R.id.main);
        back = findViewById(R.id.back);

        orderID = findViewById(R.id.order_id);
        senderName = findViewById(R.id.sender_name);
        contact = findViewById(R.id.contact);
        address = findViewById(R.id.address);
        btnContinue = findViewById(R.id.btn_continue);
        callNow = findViewById(R.id.call_now);

        tools = new Tools();

        order_id = getIntent().getStringExtra("order_id");

        AddressMapActivity.this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());


        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(AddressMapActivity.this);

        tools.logMessage(TAG, "Activity");
        tools.setLightStatusBar(main, this);

        FirebaseFirestore.getInstance().collection("orders").document(order_id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String SenderName = documentSnapshot.getString("sender_name");
                        String Contact = documentSnapshot.getString("sender_contact");
                        String Address = documentSnapshot.getString("sender_address");

                        orderID.setText("Order ID: #"+order_id);
                        senderName.setText("Sender: "+SenderName);
                        contact.setText(Contact);
                        address.setText(Address);
                    }
                });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void openBottomSheet() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(AddressMapActivity.this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottomsheet_address_map, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setCancelable(false);

        TextView orderID = mBottomSheetDialog.findViewById(R.id.order_id);
        TextView senderName = mBottomSheetDialog.findViewById(R.id.sender_name);
        TextView contact = mBottomSheetDialog.findViewById(R.id.contact);
        TextView address = mBottomSheetDialog.findViewById(R.id.address);
        CardView btnContinue = mBottomSheetDialog.findViewById(R.id.btn_continue);
        TextView callNow = mBottomSheetDialog.findViewById(R.id.call_now);


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        tools.logMessage(TAG, "onMapReady");

        getLocationPermission();

        updateLocationUI();

        getDeviceLocation();

    }


    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getParent(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                updateLocationUI();
                tools.logMessage(TAG, "onRequestPermissionsResult");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                tools.logMessage(TAG, "updateLocationUI");

            } else {
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void getDeviceLocation() {

        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {

                                tools.logMessage(TAG, "getDeviceLocation");

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                                FirebaseFirestore.getInstance().collection("orders").document(order_id).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    pickUpLat = documentSnapshot.getDouble("pickupLat");
                                                    pickUpLong = documentSnapshot.getDouble("pickupLong");

                                                    mOrigin = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                                    mDestination = new LatLng(pickUpLat, pickUpLong);

                                                    LatLng newLocation = new LatLng(pickUpLat, pickUpLong);
                                                    MarkerOptions markerOptions = new MarkerOptions()
                                                            .position(newLocation);
                                                    marker = mMap.addMarker(markerOptions);

                                                    getDirections(mOrigin, mDestination);
                                                }
                                            }
                                        });

                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }


    }


    private void getDirections(LatLng origin, LatLng destination) {

        tools.logMessage(TAG, "getDirections");

        // Build the URL for the Directions API request
        String url = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&mode=driving&key=" + getResources().getString(R.string.map_api_key);

        // Make the Directions API request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse the response to get the polyline
                        try {
                            JSONArray routes = response.getJSONArray("routes");
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String encodedPolyline = overviewPolyline.getString("points");

                            // Decode the polyline and add it to the map
                            List<LatLng> decodedPolyline = PolyUtil.decode(encodedPolyline);
                            int color = getResources().getColor(R.color.colorBlackHighEmp);
                            PolylineOptions polylineOptions = new PolylineOptions()
                                    .addAll(decodedPolyline)
                                    .color(color)
                                    .width(10);
                            mMap.addPolyline(polylineOptions);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        error.printStackTrace();
                    }
                });
        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}