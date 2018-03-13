package com.example.ivan.crib2castle;

import java.io.Serializable;

/**
 * Created by Ivan on 2/24/18.
 */

public class Home implements Serializable {
    private double price;
    private double bedrooms;
    private double bathrooms;
    private Address address;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(double bedrooms) {
        this.bedrooms = bedrooms;
    }

    public double getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(double bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
