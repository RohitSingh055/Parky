package com.example.parky;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.parky.databinding.ActivityMapBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;

public class MapActivity extends AppCompatActivity implements MapListener {

    private MapView mMap;
    private IMapController controller;
    private GeoPoint selectedMarkerGeoPoint;
    private MyLocationNewOverlay mMyLocationOverlay;
    private Marker selectedMarker; // Added to hold the selected marker
    private boolean isFollowingLocation = false;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMapBinding binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        );

        Button submit = binding.confirmLocation;
        ImageButton myLocation = binding.myLocation;

        mMap = binding.osmmap;
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.getMapCenter();
        mMap.setMultiTouchControls(true);
        mMap.getLocalVisibleRect(new Rect());

        // Set maximum zoom level
        mMap.setMaxZoomLevel(20.0); // Example maximum zoom level, adjust as needed

        followUserLocation();

        myLocation.setOnClickListener(v ->{
            isFollowingLocation = !isFollowingLocation;
            if(isFollowingLocation)
                followUserLocation();
            else
                stopFollowUserLocation();
        });

        controller.setZoom(20.0); // Example zoom level, adjust as needed

        mMap.getOverlays().add(mMyLocationOverlay);

        mMap.addMapListener(this);

        // Add MapEventsOverlay to handle long press events
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false; // We're not handling single tap events here
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                if (selectedMarker != null) {
                    mMap.getOverlays().remove(selectedMarker);
                    mMap.invalidate();
                }
                // Create a marker at the clicked position
                selectedMarker = new Marker(mMap);
                selectedMarker.setPosition(p);
                selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                // Set a bigger icon for the marker
                selectedMarker.setIcon(ContextCompat.getDrawable(MapActivity.this, R.drawable.location));
                mMap.getOverlays().add(selectedMarker);
                mMap.invalidate();

                selectedMarkerGeoPoint = p;
                return true;
            }
        });
        mMap.getOverlays().add(0, mapEventsOverlay); // Add the overlay to the map

        submit.setOnClickListener(v -> {
            if (selectedMarkerGeoPoint != null) {
                double latitude = selectedMarkerGeoPoint.getLatitude();
                double longitude = selectedMarkerGeoPoint.getLongitude();

                Toast.makeText(MapActivity.this, "Latitude: "+latitude+" & Longitude: "+longitude, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean onScroll(ScrollEvent event) {
        if(isFollowingLocation)
            isFollowingLocation = false;
        stopFollowUserLocation();

        return true;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        if(isFollowingLocation)
            isFollowingLocation = false;
        stopFollowUserLocation();
        return false;
    }

    public void followUserLocation(){
        mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mMap);
        controller = mMap.getController();

        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);

        mMyLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        controller.setCenter(mMyLocationOverlay.getMyLocation());
                        controller.animateTo(mMyLocationOverlay.getMyLocation());
                    }
                });
            }
        });
    }
    private void stopFollowUserLocation() {
        if (mMyLocationOverlay != null) {
            mMyLocationOverlay.disableFollowLocation();
        }
    }
}
