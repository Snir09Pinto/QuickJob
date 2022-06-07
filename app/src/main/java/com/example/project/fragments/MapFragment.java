package com.example.project.fragments;

import static android.content.Context.LOCATION_SERVICE;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.project.adapters.CustomMarkerInfoWindowAdapter;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.MapsManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.OfflineRequest;
import com.example.project.R;

import com.example.project.objects.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class for showing the user his location and the offline requests around him.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, LocationListener
{
    /**
     * The view of the screen (xml).
     */
    private View view;
    /**
     * The map object (Google map).
     */
    private GoogleMap map;
    /**
     * The chosen location (in search bar).
     */
    private TextView locationTV;
    /**
     * Current list of all offline requests.
     */
    private List<OfflineRequest> offlineRequestsList;
    /**
     * The location manager.
     */
    private LocationManager locationManager;
    /**
     * Current device location.
     */
    private Location currentLocation;
    /**
     * GoogleApiContext object for interacting with google api.
     */
    private GeoApiContext geoApiContext = null;
    /**
     * An interface for attaching and detaching the listener.
     */
    private ListenerRegistration registration;


    /**
     * Initializes the view if MapsManager.isServicesOK(...) returns true.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if (MapsManager.isServicesOK(getContext(), getActivity()))
        {
            view = inflater.inflate(R.layout.map_fragment_layout, container, false);
            initWidgets();
        }
        return view;
    }


    /**
     * Handles when the user is permitting using his location - Calls: startLocationService().
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case ConstantsManager.LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startLocationService();
                }
            default:
                break;
        }
    }

    /**
     * Attaches the firebase listener - listening to all offline requests.
     */
    private void attachListener()
    {
        if(registration == null && map != null && offlineRequestsList != null)
        {
            registration = FirebaseManager.getDBReference().collection(ConstantsManager.REQUESTS_COLLECTION)
                    .whereEqualTo(ConstantsManager.REQUEST_TYPE_FIELD, ConstantsManager.OFFLINE_REQUEST_TYPE)
                    .addSnapshotListener(offlineRequestsListener);
        }
    }

    /**
     * Detaches the listener.
     */
    private void detachListener()
    {
        if(registration != null)
        {
            registration.remove();
            registration = null;
        }
    }
    /**
     * Calls detachListener().
     * Calls onPause() parent function.
     */
    @Override
    public void onPause()
    {
        detachListener();
        super.onPause();
    }

    /**
     * Calls attachListener().
     * Calls onResume() parent function.
     */
    @Override
    public void onResume()
    {
        attachListener();
        super.onResume();
    }

    /**
     * Calls attachListener().
     * Calls onDestroy() parent function.
     */
    @Override
    public void onDestroy()
    {
        detachListener();
        super.onDestroy();
    }

    /**
     * Responsible for triggering the setOfflineRequestsMarkersAndList() (updating offline requests list) each time a change occurs.
     */
    private final EventListener<QuerySnapshot> offlineRequestsListener = new EventListener<QuerySnapshot>()
    {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
        {
            if(error != null) return;

            if(value != null)
            {
                if(map != null)
                {
                    setOfflineRequestsMarkersAndList(value);
                }
            }
        }
    };

    /**
     * Initializes the screen widgets (xml).
     */
    private void initWidgets()
    {
        offlineRequestsList = new ArrayList<>();
        locationTV = view.findViewById(R.id.mfl_search_input);
        handleSearchBar();
        initMap();
    }


    /**
     * Initializes the map, geoApiContext and calls setOfflineRequestsMarkersAndList().
     */
    private void initMap()
    {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mfl_map);
        supportMapFragment.getMapAsync(this);

        if(geoApiContext == null)
        {
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.api_key)).build();
        }
    }


    /**
     * The function sets the current google map object to the given parameter.
     * Calls startLocationService(), setMapUISettings().
     * @param googleMap
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        map = googleMap;
        startLocationService();
        setMapUISettings();
        attachListener();
    }

    /**
     * The function checks for permissions and starts the location service if permitted.
     */
    private void startLocationService()
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(ConstantsManager.permissions, ConstantsManager.LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }
        }
        map.setMyLocationEnabled(true);
        requestLocation();
    }

    /**
     * The function requests the device location each period of time, and gets a callback on "onLocationChanged".
     */
    private void requestLocation()
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(ConstantsManager.permissions, ConstantsManager.LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }
        }
        if(locationManager == null)
        {
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        }
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this);
        }
    }

    /**
     * The function updates device's current location and calls updateUI().
     * @param location
     */
    @Override
    public void onLocationChanged(@NonNull Location location)
    {
        currentLocation = location;
        updateUI();
    }

    /**
     * The function moves the camera to current device location.
     */
    private void updateUI()
    {
        if(currentLocation != null)
        {
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ConstantsManager.DEFAULT_ZOOM));
        }
    }


    /**
     * The function sets the mapUI settings (Design), and sets the device location button.
     */
    private void setMapUISettings()
    {
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        //Location button placement

        View locationButton = ((View) view.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
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
                    locationTV.setText(null);
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ConstantsManager.DEFAULT_ZOOM));
                }
            }
        });
    }


    /**
     * Initializes the search bar.
     */
    private void handleSearchBar()
    {
        /**
         * Initializes the Places object with the Google api key.
         */
        if(!Places.isInitialized())
        {
            Places.initialize(getContext(), getString(R.string.api_key));
        }

        locationTV.setFocusable(false);

        /**
         * Sets a listener for the locationTV
         */
        locationTV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(map != null)
                {
                    /**
                     * A list of what type of fields it contains
                     */
                    List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

                    /**
                     * An intent for the AutoComplete activity
                     */
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(getContext());

                    /**
                     * Starts the AutoComplete activity (suggesting places according to user input)
                     */
                    startActivityForResult(intent, ConstantsManager.SEARCH_REQUEST_CODE);
                }

            }
        });
    }


    /**
     * The function updates the offline requests list.
     * Calls applyMarkers().
     */
    private void setOfflineRequestsMarkersAndList(QuerySnapshot value)
    {
        if(value == null) return;
        offlineRequestsList.clear();
        for(DocumentSnapshot documentSnapshot : value)
        {
            if(documentSnapshot != null && documentSnapshot.exists())
            {
                OfflineRequest offlineRequest = documentSnapshot.toObject(OfflineRequest.class);
                offlineRequestsList.add(offlineRequest);
            }
        }
        applyMarkers();
    }

    /**
     * The function refreshes the map and adds all offline requests from the offlineRequestsList.
     */
    private void applyMarkers()
    {
        map.clear();
        map.setInfoWindowAdapter(new CustomMarkerInfoWindowAdapter(getContext()));
        for(OfflineRequest offlineRequest : offlineRequestsList)
        {
            MarkerOptions marker = new MarkerOptions().position(new LatLng(offlineRequest.getLatitude(), offlineRequest.getLongitude()));
            Marker addedMarker = map.addMarker(marker);
            addedMarker.setTag(offlineRequest);
        }
        map.setOnInfoWindowClickListener(this);
    }


    /**
     * The function handles when a user picked a location on the "search bar"
     * The function moves the camera into that location and updates the search bar text to the address of the location.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ConstantsManager.SEARCH_REQUEST_CODE){
            if(resultCode == getActivity().RESULT_OK)
            {
                Place place = Autocomplete.getPlaceFromIntent(data);
                locationTV.setText(place.getAddress());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), ConstantsManager.DEFAULT_ZOOM));
            }
        }
    }

    /**
     * The function handles onClick function of the marker info window - moves the user to an activity with the request's info.
     * @param marker
     */
    @Override
    public void onInfoWindowClick(@NonNull Marker marker)
    {
        Request request = (Request) marker.getTag();

        if(request.getUserId().equals(UserManager.getCurrentUser().getUserId()))
        {
            startActivity(request.createUserViewActivity(getContext()));
        }
        else
        {
            startActivity(request.createOthersViewActivity(getContext()));
        }
    }



}



