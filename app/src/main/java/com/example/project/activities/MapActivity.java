package com.example.project.activities;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project.R;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.MapsManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener
{
    /**
     * Current map google object.
     */
    private GoogleMap map;
    /**
     * Support map fragment
     */
    private SupportMapFragment mapFragment;
    /**
     * User location manager (Service).
     */
    private LocationManager locationManager;
    /**
     * User's current location.
     */
    private Location currentLocation;
    /**
     * The request latlng (location).
     */
    private LatLng latLng = null;
    /**
     * Geo api context.
     */
    private GeoApiContext geoApiContext = null;
    /**
     * The current request marker (on location).
     */
    private Marker currentMarker = null;
    /**
     * The current polyline shows the path from user location to request location.
     */
    private Polyline currentPolyline = null;
    /**
     * The button of the request's location.
     */
    private Button requestLocationBtn;
    /**
     * An object contains information about the way from user's location to request location.
     */
    private DirectionsLeg currentDirectionsLeg;
    /**
     * The time to arrive on foot to the request's location from user's location.
     */
    private TextView durationTV;
    /**
     * The distance text view (between user and request location).
     */
    private TextView distanceTV;
    /**
     * TAG
     */
    private static final String TAG = "tag";

    /**
     * Checks if google services is OK.
     * Calls initWidgets().
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(MapsManager.isServicesOK(MapActivity.this, this))
        {
            setContentView(R.layout.map_activity_layout);
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ConstantsManager.LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();
                }
            default:
                break;
        }
    }

    /**
     * Initializes all widgets and some of variables.
     * Calls initMap().
     */
    private void init()
    {
        latLng = getIntent().getParcelableExtra(ConstantsManager.LAT_LNG);
        durationTV = findViewById(R.id.mal_duration_text_view);
        distanceTV = findViewById(R.id.mal_distance_text_view);
        requestLocationBtn = findViewById(R.id.mal_request_location_button);
        requestLocationBtn.setOnClickListener(this);
        initMap();
    }

    /**
     * Initializing the map and geoApiContext.
     */
    private void initMap()
    {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mfl_map);
        mapFragment.getMapAsync(this);

        if(geoApiContext == null)
        {
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.api_key)).build();
        }

    }


    /**
     * When map is ready calls setMapUISettings() and startLocationService().
     * Also sets the map reference.
     * @param googleMap - received map.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        map = googleMap;
        setMapUISettings();
        startLocationService();
    }

    /**
     * Checks validity and enables user location and calls requestLocation().
     */
    private void startLocationService()
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(ConstantsManager.permissions, ConstantsManager.LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }
        }
        map.setMyLocationEnabled(true);
        requestLocation();
    }

    /**
     * Checks validity and starts the location service.
     */
    private void requestLocation()
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(ConstantsManager.permissions, ConstantsManager.LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }
        }
        if(locationManager == null)
        {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this);
        }
    }

    /**
     * updates user's location and calls changeUI().
     * @param location - current user location
     */
    @Override
    public void onLocationChanged(@NonNull Location location)
    {
        currentLocation = location;
        changeUI();
    }

    /**
     * calculating the directions once again (user's location has changed).
     * map focuses on marker position (request's position).
     */
    private void changeUI()
    {
        if(currentLocation != null)
        {
            if(currentMarker != null)
            {
                if(currentPolyline != null)
                {
                    currentPolyline.remove();
                }
                calculateDirections(currentMarker);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMarker.getPosition(), ConstantsManager.DEFAULT_ZOOM));
            }
        }
    }

    /**
     * Initialize user location button + its onClick function, zoom controls.
     * Calls setRequestMarker().
     */
    private void setMapUISettings()
    {
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        //Location button placement

        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 1200, 180, 0);
        locationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(currentLocation != null)
                {
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ConstantsManager.DEFAULT_ZOOM));
                }
            }
        });

        setRequestMarker();
    }

    /**
     * Adds the request marker to the map (UI).
     */
    private void setRequestMarker()
    {
        MarkerOptions marker = new MarkerOptions().position(latLng);
        currentMarker = map.addMarker(marker);
    }


    /**
     * The function is given a marker and has the user's current location.
     * Calls addPolylinesToMap(result), when result is an object "DirectionsResult" type holding the direction routes.
     * @param marker - request's location marker
     */
    private void calculateDirections(Marker marker)
    {

        /**
         * Marker position
         */
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        /**
         * Initializes the DirectionsApiRequest object with the geoApiContext (which has the api key)
         */
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        /**
         *  Directions origin (start point) is from current device location.
         */
        directions.alternatives(false); //returns only single polyline
        directions.origin(
                new com.google.maps.model.LatLng(
                        currentLocation.getLatitude(),
                        currentLocation.getLongitude()
                )
        );
        /**
         * Waits for result
         */
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result)
            {
                /**
                 * Gets the result and calls the addPolylineToMap with the result parameter.
                 */
                addPolylineToMap(result);
            }

            @Override
            public void onFailure(Throwable e)
            {
                Log.e(TAG, "onFailure: " + e.getMessage());
            }
        });
    }






    /**
     *
     * @param result - gets the DirectionsResult object from calculateDirections(...) function
     * Draws a polyline from user's location to request's location.
     * Calls setDurationAndDistance().
     */
    private void addPolylineToMap(final DirectionsResult result)
    {
        /**
         * Posts on the main thread
         */
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run() {

                /**
                 * For each route gets its LatLng list.
                 */
                for(DirectionsRoute route: result.routes)
                {
                    /**
                     * Gets the LatLng Object - decoded path
                     */
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    /**
                     * Adds the polyline
                     */
                    currentPolyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    /**
                     * Changes its color
                     */
                    currentPolyline.setColor(ContextCompat.getColor(MapActivity.this, R.color.blue));
                    /**
                     * Gets the DirectionLeg Object
                     */
                    currentDirectionsLeg = route.legs[0];
                    /**
                     * Sets duration and distance till destination
                     */
                    setDurationAndDistance();
                }
            }
        });
    }



    /**
     * Sets the duration and distance of destination with the currentDirectionsLeg variable (possessing single route information).
     */
    private void setDurationAndDistance()
    {
        if(currentDirectionsLeg != null)
        {
            durationTV.setText(currentDirectionsLeg.duration.humanReadable);
            distanceTV.setText(currentDirectionsLeg.distance.humanReadable);
        }
    }




    /**
     * Handles the click of request location button - zooms to the request's location (animation).
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == requestLocationBtn.getId())
        {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ConstantsManager.DEFAULT_ZOOM));
        }
    }
}
