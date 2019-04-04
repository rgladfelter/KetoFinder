package edu.radford.cerj.ketofinder;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RestaurantInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PLACE = "PLACE";
    private @Nullable OnCloseListener mOnCloseListener;
    @BindView(R.id.address_text)
    TextView addressText;
    @BindView(R.id.save_pin_button)
    FloatingActionButton savePinButton;
    @BindView(R.id.restaurant_name)
    EditText restaurantText;

    interface OnCloseListener {
        void onClose();
    }

    // TODO: Rename and change types of parameters
    private Place mPlace;

    public RestaurantInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param place Parameter 1.
     * @return A new instance of fragment RestaurantInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RestaurantInfoFragment newInstance(Place place) {
        RestaurantInfoFragment fragment = new RestaurantInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PLACE, place);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlace = getArguments().getParcelable(ARG_PLACE);
        }
    }

    public void attachParentFragment(OnCloseListener onSaveListener) {
        mOnCloseListener = onSaveListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_restaurant_info, container, false);

        ButterKnife.bind(this, view);
        savePinButton.setOnClickListener(v -> {
            if (mOnCloseListener != null) {
                mOnCloseListener.onClose();
            }
        });

        Location location = mPlace.getLocation();
        String address = String.format(getString(R.string.address_format2), location.getStreet(), location.getCity(), location.getState(), location.getZip());
        addressText.setText(address);
        restaurantText.setText(mPlace.getName());
        return view;
    }
}
