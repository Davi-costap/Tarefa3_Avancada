package com.example.mylibraryRegion;

import android.location.Location;

import java.io.Serializable;

// Classe
public class Region implements Serializable {
    private String name;
    private double latitude;
    private double longitude;
    private int user;
    private long timestamp;
    private static final int RADIUS = 6371; // Raio da Terra em quilômetros

    public Region() {
        // Construtor vazio
    }
    // Construtor
    public Region(String name, Location location) {
        this.name = name;
        this.latitude =  location.getLatitude();
        this.longitude = location.getLongitude();
        this.user = 1;
        this.timestamp = System.nanoTime();
    }

    // Construtor com valores explícitos
    public Region(String name, double latitude, double longitude, int user, long timestamp) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
        this.timestamp = timestamp;
    }

    // Getters e setters
    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }


    public double getLatitude() {

        return latitude;
    }

    public void setLatitude(double latitude) {

        this.latitude = latitude;
    }

    public double getLongitude() {

        return longitude;
    }

    public void setLongitude(double longitude) {

        this.longitude = longitude;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {

        this.user = user;
    }

    public long getTimestamp() {

        return timestamp;
    }

    public static double calcDist(Location currentLocation, Location regionLocation) {
        double lat1 = currentLocation.getLatitude();
        double long1 = currentLocation.getLongitude();
        double lat2 = regionLocation.getLatitude();
        double long2 = regionLocation.getLongitude();

        // Fórmula de Haversine
        double dLat  = Math.toRadians((lat2 - lat1));
        double dLong = Math.toRadians((long2 - long1));
        lat1 = Math.toRadians(lat1);
        lat2   = Math.toRadians(lat2);
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLong / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIUS * c * 1000;
    }

}
