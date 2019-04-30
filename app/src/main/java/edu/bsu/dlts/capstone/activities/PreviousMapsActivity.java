package edu.bsu.dlts.capstone.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPoint;

import org.json.JSONException;
import org.json.JSONObject;

import edu.bsu.dlts.capstone.R;

public class PreviousMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private JSONObject geojson;

    private String geojsonStr;

    private Boolean mLocationPermissionGranted = false;

    private Location currentLocation;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geojsonStr = getIntent().getStringExtra("geojson");



        configureFinishButton();
    }

    private void configureFinishButton(){
        Button endTour = (Button) findViewById(R.id.endTour);
        endTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(PreviousMapsActivity.this, PreviousToursActivity.class);
                startActivity(intent2);
            }
        });
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

        if (geojsonStr != null){
            try {
                Log.d("MapStat", geojsonStr);
                geojson = new JSONObject(geojsonStr);
                GeoJsonLayer layer = new GeoJsonLayer(mMap, geojson);
                layer.addLayerToMap();

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(((GeoJsonPoint)layer.getFeatures().iterator().next().getGeometry()).getCoordinates())      // Sets the center of the map to Mountain View
                        .zoom(18)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else  {
            Log.d("PreviousMapsActivity", "Failed to load geojson");
        }


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(geojson, mMap);

        if (mLocationPermissionGranted && locationManager != null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
    }

//    public static class AzureBlobAdapter extends AsyncTask<BlobParams, Void, Void> {
//        private static final String storageURL = "http://brstrayer.blob.core.windows.net";
//        private static final String storageContainer = "geojson";
//        private static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=brstrayer;AccountKey=9RqBtAtKXwGsDInFfz2uECfwe3VBymaRriMG9ms7gLjfKJ9ku0uG3MZX7P855AdcOwU72WTnei8q2FMlwcB1MA==;EndpointSuffix=core.windows.net";
//
//        @Override
//        protected Void doInBackground(BlobParams... params) {
//            try{
//                CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
//                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
//                CloudBlobContainer container = blobClient.getContainerReference(storageContainer);
//                CloudBlockBlob blob = container.getBlockBlobReference("blobTest");
//                blob.uploadText(params[0].geojson);
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }
//
//    public static class BlobParams{
//        TrackToTripPerson user;
//        String geojson;
//
//        BlobParams(TrackToTripPerson user, String geojson){
//            this.user = user;
//            this.geojson = geojson;
//        }
//    }
}


