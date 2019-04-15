package edu.radford.cerj.ketofinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
    TextView restaurantText;
    @BindView(R.id.fb_rating_bar)
    RatingBar fbRatingBar;
    @BindView(R.id.keto_rating_bar)
    RatingBar ketoRatingBar;
    @BindView(R.id.fb_rating_text)
    TextView fbRatingText;
    @BindView(R.id.fb_rating_count)
    TextView fbRatingCount;
    @BindView(R.id.keto_rating_text)
    TextView ketoRatingText;
    @BindView(R.id.keto_rating_count)
    TextView ketoRatingCount;
    @BindView(R.id.website_text)
    TextView websiteText;

    interface OnCloseListener {

        void onClose();
    }

    // TODO: Rename and change types of parameters
    private Place mPlace;
    private Map<String, Float> mKetoRatings;

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
        addressText.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse(String.format("geo:%1$s,%2$s?q=%3$s", location.getLatitude(), location.getLongitude(), mPlace.getName()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });
        restaurantText.setText(mPlace.getName());
        websiteText.setText(mPlace.getWebsite());
        websiteText.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(mPlace.getWebsite()));
            startActivity(i);
        });
        fbRatingBar.setRating(mPlace.getFbRating());
        fbRatingText.setText(String.format(Locale.US, "%.2f", mPlace.getFbRating()));
        fbRatingCount.setText(String.format(getString(R.string.rating_count_format), mPlace.getFbRatingCount()));
        PlacesRepository.getKetoRating(mPlace.getId(), this::setKetoInfo);
        ketoRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if(fromUser) {
                String userId = FirebaseAuth.getInstance().getUid();
                assert userId != null;
                PlacesRepository.addKetoRating(rating, mPlace.getId(), userId);
                if(mKetoRatings == null) {
                    mKetoRatings = new HashMap<>();
                }
                mKetoRatings.put(userId, rating);
                setKetoInfo(mKetoRatings);
            }
        });
        return view;
    }



    private static float findAverage(@NonNull Map<String, Float> ketoRatings) {
        float sum = 0;
        for (Map.Entry<String, Float> rating :
                ketoRatings.entrySet()) {
            sum += rating.getValue();
        }

        return sum / ketoRatings.size();
    }

    private void setKetoInfo(Map<String, Float> ratings) {
        mKetoRatings = ratings;
        if(ratings == null)
            return;

        float ketoAverage = findAverage(ratings);
        ketoRatingText.setText(String.format(Locale.US, "%.2f", ketoAverage));
        ketoRatingCount.setText(String.format(getString(R.string.rating_count_format), ratings.size()));
        ketoRatingBar.setRating(ketoAverage);
    }
}
