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
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class WalkingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location mCurrentLocation;
    protected List<LineStorage> myPath = new ArrayList<>();
    private static WalkingActivity mainActivityRunningInstance;
    private Intent locationServiceIntent;

    public static WalkingActivity getInstance() {
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
        LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng current = null;
        PolylineOptions lineOptions = new PolylineOptions();

        if (mCurrentLocation == null) {
            mMap.addMarker(new MarkerOptions().position(newLocation).title("Start here"));
        } else {
            current = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            lineOptions.add(current, newLocation).width(15).color(Color.RED);
            mMap.addPolyline(lineOptions);
        }

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
        if (checkIntersection(lineOptions.getPoints())) {
            mMap.addMarker(new MarkerOptions().position(newLocation).title("Someone lost here"));
            showToast("Krock!!!");
        }

        if (current != null) {
            LineStorage path = new LineStorage();
            path.Start = current;
            path.Stop = newLocation;
            path.AddTime = new Date();
            myPath.add(path);
        }
    }

    protected boolean checkIntersection(List<LatLng> linePoints) {
        if (myPath.isEmpty()) {
            return false;
        }

        double step = 0.00001;
        double x1, x2, y1, y2, m, c, x, y;
        Double M;
        x1 = linePoints.get(0).latitude;
        y1 = linePoints.get(0).longitude;
        x2 = linePoints.get(1).latitude;
        y2 = linePoints.get(1).longitude;
        m = (y1 - y2) / (x1 - x2);
        M = m;
        c = y1 - x1 * m;
        if (m == 0 || M.isInfinite()) {
            m = 0.0;
            c = y1;
        }

        Collections.sort(myPath, new LineStorageDateSort());

        for (LineStorage line : myPath) {
            List<LatLng> path = new ArrayList<>();
            path.add(line.Start);
            path.add(line.Stop);

            if (x1 != x2) {
                if( x2 < x1){
                    double tmpX = x2;
                    x2 = x1;
                    x1 = tmpX;
                }
                for (x = x1 + step; x < x2; x = x + step) {
                    y = m * x + c;
                    LatLng point = new LatLng(x, y);
//                    mMap.addMarker(new MarkerOptions().position(point).title(String.valueOf(x)));
                    if (PolyUtil.isLocationOnPath(point, path, true, 0.1)) {
                        return true;
                    }
                }
            } else {
                if( y2 < y1){
                    double tmpY = y2;
                    y2 = y1;
                    y1 = tmpY;
                }
                for (y = y1 + step; y < y2; y = y + step) {
                    LatLng point = new LatLng(x1, y);
//                    mMap.addMarker(new MarkerOptions().position(point).title(String.valueOf(y)));
                    if (PolyUtil.isLocationOnPath(point, path, true, 0.1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void showToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}