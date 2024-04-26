package com.example.mylibraryRegion;

import android.location.Location;

import java.io.Serializable;

public class RestrictedRegion extends Region implements Serializable {
    private Region mainRegion;
    private boolean restricted;

    public RestrictedRegion(String name, Location location, Region mainRegion, boolean restricted) {
        super(name, location);
        this.mainRegion = mainRegion;
        this.restricted = restricted;
    }


    public boolean isProximateTo(Region subRegion) {
        Location currentLocation = new Location("");
        currentLocation.setLatitude(this.getLatitude());
        currentLocation.setLongitude(this.getLongitude());

        Location regionLocation = new Location("");
        regionLocation.setLatitude(subRegion.getLatitude());
        regionLocation.setLongitude(subRegion.getLongitude());

        return calcDist(currentLocation, regionLocation) < 5;
    }
}