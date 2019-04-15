package edu.radford.cerj.ketofinder;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Place implements Parcelable
{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("custom")
    @Expose
    private boolean custom;
    @SerializedName("keto")
    @Expose
    private boolean keto;
    @SerializedName("overall_star_rating")
    @Expose
    private float fbRating;
    @SerializedName("rating_count")
    @Expose
    private int fbRatingCount;

    public final static Parcelable.Creator<Place> CREATOR = new Creator<Place>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        public Place[] newArray(int size) {
            return (new Place[size]);
        }

    }
            ;

    protected Place(Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.location = ((Location) in.readValue((Location.class.getClassLoader())));
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.website = ((String) in.readValue((String.class.getClassLoader())));
        this.custom = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.keto = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
    }

    public Place() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public boolean getKeto() {
        return keto;
    }

    public void setKeto(boolean keto) {
        this.keto = keto;
    }

    public float getFbRating() {
        return fbRating;
    }

    public void setFbRating(float fbRating) {
        this.fbRating = fbRating;
    }

    public int getFbRatingCount() {
        return fbRatingCount;
    }

    public void setFbRatingCount(int fbRatingCount) {
        this.fbRatingCount = fbRatingCount;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(location);
        dest.writeValue(id);
        dest.writeValue(custom);
        dest.writeValue(keto);
        dest.writeValue(fbRating);
        dest.writeValue(fbRatingCount);
        dest.writeValue(website);
    }

    public int describeContents() {
        return 0;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}