package edu.radford.cerj.ketofinder;

import java.io.Serializable;
import com.google.firebase.database.IgnoreExtraProperties;

public class Place implements Serializable {
    public String name;
    public String city;
    public String country;
    public double lat;
    public double lng;
    public String state;
    public String street;
    public String zip;
    public String id;

    public Place(String _name, String _city, String _country, double _lat, double _lng, String _state, String _street, String _zip, String _id) {
        this.name = _name;
        this.city = _city;
        country = _country;
        lat = _lat;
        lng = _lng;
        state = _state;
        street = _street;
        zip = _zip;
        id = _id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getState() {
        return state;
    }

    public String getStreet() {
        return street;
    }

    public String getZip() {
        return zip;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setId(String id) {
        this.id = id;
    }

}


