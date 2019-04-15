package edu.radford.cerj.ketofinder;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class DropPinFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.address_text)
    TextView addressText;
    @BindView(R.id.save_pin_button)
    FloatingActionButton savePinButton;
    @BindView(R.id.restaurant_name)
    EditText restaurantText;

    private LatLng mLatLng;
    private Address mAddress;
    private OnSaveListener mOnSaveListener;

    public DropPinFragment() {
        // Required empty public constructor
    }

    interface OnSaveListener {
        void onSave();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_drop_pin, container, false);
        ButterKnife.bind(this, view);

        savePinButton.setOnClickListener(v -> {
            restaurantText.setError(null);

            String name = restaurantText.getText().toString();
            if(name.isEmpty()) {
                restaurantText.setError(getString(R.string.restaurant_name_error));
                return;
            }
            if(mAddress == null) {
                Toast.makeText(getContext(), getString(R.string.address_error), Toast.LENGTH_LONG).show();
                return;
            }

            Location location = new Location();
            location.setStreet(mAddress.getSubThoroughfare() + " " + mAddress.getThoroughfare());
            location.setCity(mAddress.getLocality());
            location.setCountry(mAddress.getCountryName());
            location.setLatitude(mAddress.getLatitude());
            location.setLongitude(mAddress.getLongitude());
            location.setState(mAddress.getAdminArea());
            location.setZip(mAddress.getPostalCode());

            Place newPlace = new Place();
            newPlace.setLocation(location);
            newPlace.setName(name);
            newPlace.setKeto(true);
            newPlace.setCustom(true);
            newPlace.setId(UUID.randomUUID().toString());

            savePlace(newPlace);
            if(mOnSaveListener != null) {
                mOnSaveListener.onSave();
            }
        });
        return view;
    }

    public void attachParentFragment(OnSaveListener onSaveListener) {
        mOnSaveListener = onSaveListener;
    }
    private void savePlace(Place newPlace) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("custom_places");
        ref.child(newPlace.getId()).setValue(newPlace);
    }

    public void setPinLocation(LatLng latLng) {
       Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(!addresses.isEmpty()) {
                mAddress = addresses.get(0);
                String addressStr = String.format(getString(R.string.address_format),
                        mAddress.getSubThoroughfare(),
                        mAddress.getThoroughfare(),
                        mAddress.getLocality(),
                        mAddress.getAdminArea(),
                        mAddress.getPostalCode()
                );
                addressText.setText(addressStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
