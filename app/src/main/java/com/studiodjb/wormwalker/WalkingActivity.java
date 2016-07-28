package com.studiodjb.wormwalker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class WalkingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location mCurrentLocation;
    private List<LatLng> myPath = new ArrayList<>();
    private static WalkingActivity mainActivityRunningInstance;
    private Intent locationServiceIntent;

    public static WalkingActivity  getInstance(){
        return mainActivityRunningInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivityRunningInstance = this;
        // Start service
        locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);

        setContentView(R.layout.activity_walking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        stopService(locationServiceIntent);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
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
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void updateUI(Location location) {
        if(mCurrentLocation == null){
            mCurrentLocation = location;
        }

        LatLng current = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());

        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.add(current, newLocation).width(15).color(Color.RED);
        mMap.addPolyline(lineOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
//        mMap.animateCamera(CameraUpdateFactory.zoomIn());

//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(newLocation)      // Sets the center of the map
//                .zoom(17)                   // Sets the zoom
//                .bearing( location.getBearing())                // Sets the orientation of the camera to east
//                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
//                .build();                   // Creates a CameraPosition from the builder
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//        mLastUpdateTimeTextView.setText(mLastUpdateTime);
        mCurrentLocation = location;
        if(checkIntersection(newLocation)){
            mMap.addMarker(new MarkerOptions().position(newLocation).title("Someone lost here"));
            showToast("Krock!!!");
        }
        myPath.add(newLocation);
    }

    private boolean checkIntersection(LatLng myPosition){
        if (com.google.maps.android.PolyUtil.isLocationOnEdge(myPosition, myPath, true, 1)) {
            return true;
        }
        return false;
    }

    private void showToast(String text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
