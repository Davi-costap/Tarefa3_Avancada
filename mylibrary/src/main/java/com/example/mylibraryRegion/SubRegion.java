package com.example.mylibraryRegion;

import android.location.Location;

import java.io.Serializable;

public class SubRegion extends Region implements Serializable {
    private Region mainRegion;

    public SubRegion(String name, Location location, Region mainRegion) {
        super(name, location);
        this.mainRegion = mainRegion;
    }



    public boolean isProximateTo(Region restrictedRegion) {
        Location currentLocation = new Location("");
        currentLocation.setLatitude(this.getLatitude());
        currentLocation.setLongitude(this.getLongitude());

        Location regionLocation = new Location("");
        regionLocation.setLatitude(restrictedRegion.getLatitude());
        regionLocation.setLongitude(restrictedRegion.getLongitude());

        return calcDist(currentLocation, regionLocation) < 5;
    }
}
