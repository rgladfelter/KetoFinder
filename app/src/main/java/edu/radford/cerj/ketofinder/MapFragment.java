package edu.radford.cerj.ketofinder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.facebook.FacebookSdk;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, PlacesRepository.OnPlaceUpdateListener, PlacesRepository.OnCustomPlaceAddedListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


//    @BindView(R.id.search_button) Button searchButton;
    @BindView(R.id.suggest_button)
    FloatingActionButton suggestButton;

    @BindView(R.id.search_edit_text)
    EditText locationSearch;

    @BindView(R.id.information_frag)
    FrameLayout informationLayout;

    private LocationManager lm;
    private GoogleMap mMap;
    private Map<String, Marker> mMarkerMap;
    private @Nullable DropPinFragment mDropPinFragment;
    private @Nullable RestaurantInfoFragment mRestaurantInfoFragment;
    private Marker mDroppedPin;
    private @Nullable List<Place> mPlaces;
    public MapFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.setClientToken(getString(R.string.fb_client_token));
        PlacesRepository.setOnPlaceUpdateListener(this);
        PlacesRepository.setOnCustomPlaceAddedListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        suggestButton.setOnClickListener(v -> dropPin());
//        searchButton.setOnClickListener(v -> );

        locationSearch.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                onMapSearch();
                return true;
            }
            return false;
        });
        return view;
    }

    private void dropPin() {
        informationLayout.bringToFront();
        mDropPinFragment = new DropPinFragment();
        mDropPinFragment.attachParentFragment(() -> {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
            transaction.remove(mDropPinFragment);
            transaction.commit();
            mDropPinFragment = null;
            if(mDroppedPin != null) {
                mDroppedPin.remove();
                mDroppedPin = null;
            }
        });

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up);
        transaction.replace(R.id.information_frag, mDropPinFragment, "Fragment");
        transaction.commit();
    }

    private void setMarkers(List<Place> places) {
        mPlaces = places;
        mMarkerMap = new HashMap<>();
        for (Place place : places) {
            edu.radford.cerj.ketofinder.Location location = place.getLocation();
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .title(place.getName())
                    .position(position)
                    .icon(getIcon(place)));
            mMarkerMap.put(place.getId(), marker);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
        mMap.setOnMyLocationButtonClickListener(() -> {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Criteria criteria = new Criteria();

                Location location = lm.getLastKnownLocation(lm.getBestProvider(criteria, false));
                if (location != null) {
                    setLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                }

            }
            return false;
        });

        mMap.setOnMapClickListener(latLng -> {
            if(mDropPinFragment != null) {
                if(mDroppedPin != null) {
                    mDroppedPin.remove();
                }
                mDroppedPin = mMap.addMarker(
                    new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
                ));
                mDropPinFragment.setPinLocation(latLng);
            }
        });

        mMap.setOnMarkerClickListener(marker -> {
            String id = getKey(mMarkerMap, marker);
            Place place = getPlace(id);
            if(place != null) {
                mRestaurantInfoFragment = RestaurantInfoFragment.newInstance(place);
                mRestaurantInfoFragment.attachParentFragment(() -> {
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                    assert mRestaurantInfoFragment != null;
                    transaction.remove(mRestaurantInfoFragment);
                    transaction.commit();
                    mRestaurantInfoFragment = null;
                });
                if(mDroppedPin != null) {
                    mDroppedPin.remove();
                    mDroppedPin = null;
                }
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up);
                assert mRestaurantInfoFragment != null;
                transaction.replace(R.id.information_frag, mRestaurantInfoFragment, "Fragment");
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }
            return false;
        });
        // Add a marker in Sydney and move the camera
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!checkLocationPermission()) {
            return;
        }
        this.setLatLng();
    }

    public <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        this.setLatLng();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    private void setLatLng() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);


            Criteria criteria = new Criteria();

            Location location = lm.getLastKnownLocation(lm.getBestProvider(criteria, false));
            if (location != null) {
                setLocation(new LatLng(location.getLatitude(), location.getLongitude()));
            }

        }
    }

    private void setLocation(LatLng latLng) {
        mMap.clear();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to location user
                .zoom(13)                   // Sets the zoom
                .bearing(180)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        PlacesRepository.getPlaces(latLng, new PlacesRepository.Callback() {
            @Override
            public void receivedData(List<Place> places) {
                setMarkers(places);
            }

            @Override
            public void onError(JSONException e) {}
        });
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Do I have permission?")
                        .setMessage("pls")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public void onMapSearch() {

        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(getContext());
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            setLocation(latLng);
        }
    }

    @Override
    public void onPlaceUpdateListener(Place place) {
        Marker marker = mMarkerMap.get(place.getId());
        if(marker != null) {
            marker.setIcon(getIcon(place));
        }
    }

    private static BitmapDescriptor getIcon(Place place) {
        float color = BitmapDescriptorFactory.HUE_RED;
        if(place.getCustom()) {
            color = BitmapDescriptorFactory.HUE_MAGENTA;
        } else if(place.getKeto()) {
            color = BitmapDescriptorFactory.HUE_GREEN;
        }
        return BitmapDescriptorFactory.defaultMarker(color);
    }

    @Override
    public void onCustomPlaceAdded(Place place) {
        LatLng position = new LatLng(place.getLocation().getLatitude(), place.getLocation().getLongitude());
        if(mMap != null) {
            if(mPlaces != null) {
                mPlaces.add(place);
            }
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .title(place.getName())
                    .position(position)
                    .icon(getIcon(place)));
            mMarkerMap.put(place.getId(), marker);
        }
    }

    private @Nullable Place getPlace(String id) {
        if(mPlaces == null) return null;

        for(Place place: mPlaces) {
            if(place.getId().equals(id)) {
                return place;
            }
        }

        return null;
    }
}

