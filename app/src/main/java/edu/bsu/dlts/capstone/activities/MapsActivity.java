package edu.bsu.dlts.capstone.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Objects;

import edu.bsu.dlts.capstone.R;
import edu.bsu.dlts.capstone.models.TrackToTripPerson;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private JSONObject geojson;

    private Boolean mLocationPermissionGranted = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getLocationPermission();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        String geojsonStr = getIntent().getStringExtra("geojson");

        if (geojsonStr != null){
            try {
                geojson = new JSONObject(geojsonStr);
                GeoJsonLayer layer = new GeoJsonLayer(mMap, geojson);
                layer.addLayerToMap();
                Log.d("MapStat", "isPrevious");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else  {
            geojson = new JSONObject();

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
        Button endTour = findViewById(R.id.endTour);
        endTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMenu = new Intent(MapsActivity.this, MainMenuActivity.class);
                startActivity(toMenu);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = false;
                        return;
                    }
                }

                mLocationPermissionGranted = true;

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
        LocationListener locationListener = new MyLocationListener(geojson, mMap);

        if (mLocationPermissionGranted && locationManager != null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
    }

    /**
     * Adds the geojson data to the blob
     */
    public static class AzureBlobAdapter extends AsyncTask<BlobParams, Void, Void> {
        private static final String storageContainer = "geojson";
        private static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=brstrayer;AccountKey=9RqBtAtKXwGsDInFfz2uECfwe3VBymaRriMG9ms7gLjfKJ9ku0uG3MZX7P855AdcOwU72WTnei8q2FMlwcB1MA==;EndpointSuffix=core.windows.net";

        @Override
        protected Void doInBackground(BlobParams... params) {
            try{
                CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
                CloudBlobContainer container = blobClient.getContainerReference(storageContainer);
                CloudBlockBlob blob = container.getBlockBlobReference("blobTest");
                blob.uploadText(params[0].geojson);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    static class BlobParams{
        TrackToTripPerson user;
        String geojson;

        BlobParams(TrackToTripPerson user, String geojson){
            this.user = user;
            this.geojson = geojson;
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

            // Create a JSON object for the GeoJSON data
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

            // Add location to map
            googleMap.addMarker(new MarkerOptions().position(current).title("Location on " + timestamp));

            // Zoom on current location
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(current)      // Sets the center of the map to Mountain View
                    .zoom(18)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            MapsActivity.BlobParams params = new MapsActivity.BlobParams(null, geojson.toString());
            MapsActivity.AzureBlobAdapter blobAdapter = new MapsActivity.AzureBlobAdapter();
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
