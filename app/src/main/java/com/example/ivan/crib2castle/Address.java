package com.example.ivan.crib2castle;

/**
 * Created by Ivan on 2/24/18.
 */

public class Address {

    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;


    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String toString() {
        if(address2 != null)
            return address1+", "+address2+", "+city+", "+state+" "+zip;
        else
            return address1+", "+city+", "+state+" "+zip;
    }
}
