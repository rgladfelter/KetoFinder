package edu.radford.cerj.ketofinder;

import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.GraphRequest;
import com.facebook.places.PlaceManager;
import com.facebook.places.model.PlaceSearchRequestParams;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class PlacesRepository {

    interface Callback {
        void receivedData(List<Place> places);
        void onError(JSONException e);
    }

    interface OnPlaceUpdateListener {
        void onPlaceUpdateListener(Place place);
    }

    interface OnCustomPlaceAddedListener {
        void onCustomPlaceAdded(Place place);
    }

    interface OnKetoRatingReceivedListener {
        void onKetoRatingReceived(Map<String, Float> ratings);
    }
    static void getPlaces(LatLng latLng, final Callback callback) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);

        PlaceSearchRequestParams params = new PlaceSearchRequestParams.Builder()
                .addCategory("FOOD_BEVERAGE")
                .setDistance(5000)
                .addField("name")
                .addField("location")
                .addField("overall_star_rating")
                .addField("rating_count")
                .addField("website")
                .build();
        GraphRequest request = PlaceManager.newPlaceSearchRequestForLocation(params, location);
        request.setCallback(response -> {
            try {
                List<Place> fbPlaces = new Gson().fromJson(response.getJSONObject().getJSONArray("data").toString(), new TypeToken<List<Place>>(){}.getType());
                List<Place> reducedPlaces = new ArrayList<>();
                DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference().child("place");
                placesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Place> firebasePlaces = new ArrayList<>();
                        for (DataSnapshot placeSnapshot: dataSnapshot.getChildren()) {
                            firebasePlaces.add(placeSnapshot.getValue(Place.class));
                        }

                        for (Place fbPlace: fbPlaces) {
                            Place firebasePlace = exists(fbPlace, firebasePlaces);
                            if(firebasePlace != null) {
                                reducedPlaces.add(firebasePlace);
                            } else {
                                placesRef.child(fbPlace.getId()).setValue(fbPlace);
                                reducedPlaces.add(fbPlace);
                            }
                        }
                        DatabaseReference customPlaces = FirebaseDatabase.getInstance().getReference().child("custom_places");
                        customPlaces.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                List<Place> customPlaces = new ArrayList<>();
                                for (DataSnapshot placeSnapshot: dataSnapshot.getChildren()) {
                                    customPlaces.add(placeSnapshot.getValue(Place.class));
                                }
                                reducedPlaces.addAll(customPlaces);
                                callback.receivedData(reducedPlaces);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            } catch (JSONException e) {
                callback.onError(e);
            }

        });
        request.executeAsync();
    }

    public static void setOnPlaceUpdateListener(final OnPlaceUpdateListener listener) {
        DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference().child("place");
        placesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Place place = dataSnapshot.getValue(Place.class);
                listener.onPlaceUpdateListener(place);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public static void setOnCustomPlaceAddedListener(final OnCustomPlaceAddedListener listener) {
        DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference().child("custom_places");
        placesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Place place = dataSnapshot.getValue(Place.class);
                listener.onCustomPlaceAdded(place);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private static @Nullable Place exists(Place place, List<Place> places) {
        for (Place p : places) {
            if(p.getId().equals(place.getId())) {
                return p;
            }
        }
        return null;
    }
    static void getKetoRating(String id, OnKetoRatingReceivedListener listener) {
        DatabaseReference ketoRatingsRef = FirebaseDatabase.getInstance().getReference().child("place").child(id).child("keto_ratings");
        ketoRatingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Float>> t = new GenericTypeIndicator<Map<String, Float>>() {};
                Map<String, Float> ratings = dataSnapshot.getValue(t);
                listener.onKetoRatingReceived(ratings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    static void addKetoRating(float rating, String id, String userId) {
        DatabaseReference ketoRatingsRef = FirebaseDatabase.getInstance().getReference().child("place").child(id).child("keto_ratings");
        ketoRatingsRef.child(userId).setValue(rating);
    }
}

