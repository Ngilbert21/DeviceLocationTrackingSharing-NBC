package edu.bsu.dlts.capstone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private JSONObject geojson;
    private boolean isPrevious;

    private Boolean mLocationPermissionGranted = false;

    private Location currentLocation;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getLocationPermission();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String geojsonStr = getIntent().getStringExtra("geojson");

        if (geojsonStr != null){
            try {
                geojson = new JSONObject(geojsonStr);
                GeoJsonLayer layer = new GeoJsonLayer(mMap, geojson);
                layer.addLayerToMap();
                isPrevious = true;
                Log.d("MapStat", "isPrevious");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else  {
            geojson = new JSONObject();
            isPrevious = false;

            try {
                geojson.put("type", "FeatureCollection");
                geojson.put("features", new JSONArray());
                Log.d("MapStat", "isCurrent");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        configureFinishButton();
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
            }
        }else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void configureFinishButton(){
        Button endTour = (Button) findViewById(R.id.button1);
        endTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MapsActivity.this, MainMenu.class);
                startActivity(intent2);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0 ){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }

                    mLocationPermissionGranted = true;

                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(geojson, mMap);

        if (mLocationPermissionGranted && locationManager != null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
    }
}

class MyLocationListener implements LocationListener {
    private GoogleMap googleMap;
    private JSONObject geojson;

    MyLocationListener(JSONObject geojson, GoogleMap googleMap) {
        this.geojson = geojson;
        this.googleMap = googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            String timestamp = Calendar.getInstance().getTime().toString();
            try {
                JSONArray features = geojson.getJSONArray("features");
                JSONObject feature = new JSONObject();
                JSONObject geometry = new JSONObject();
                JSONObject properties = new JSONObject();
                geometry.put("type", "Point");
                geometry.put("coordinates", new JSONArray("[" + location.getLongitude() + ", " + location.getLatitude() + "]"));
                feature.put("type", "Feature");
                feature.put("geometry", geometry);
                properties.put("Timestamp", timestamp);
                feature.put("properties", properties);
                features.put(feature);
                geojson.put("features", features);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            googleMap.addMarker(new MarkerOptions().position(current).title("Location on " + timestamp));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(current)      // Sets the center of the map to Mountain View
                    .zoom(18)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            BlobParams params = new BlobParams(null, geojson.toString());
            AzureBlobAdapter blobAdapter = new AzureBlobAdapter();
            blobAdapter.execute(params);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}
