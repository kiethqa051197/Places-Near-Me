package com.example.placesnearme.Remote;

import android.location.Location;

import com.example.placesnearme.Model.Firebase.DiaDiem;
import com.example.placesnearme.View.MainActivity;

import java.util.Comparator;

public class SortDiaDiem implements Comparator<DiaDiem> {
    @Override
    public int compare(DiaDiem diaDiem1, DiaDiem diaDiem2) {
        android.location.Location locationCurrent = new android.location.Location("Location Current");
        locationCurrent.setLatitude(MainActivity.latitude);
        locationCurrent.setLongitude(MainActivity.longtitude);

        android.location.Location location1 = new android.location.Location("Location Selected");
        location1.setLatitude(diaDiem1.getLocation().getLatitude());
        location1.setLongitude(diaDiem1.getLocation().getLongitude());

        double distance1 = locationCurrent.distanceTo(location1) / 1000;

        android.location.Location location2 = new Location("Location Selected");
        location2.setLatitude(diaDiem2.getLocation().getLatitude());
        location2.setLongitude(diaDiem2.getLocation().getLongitude());

        double distance2 = locationCurrent.distanceTo(location2) / 1000;

        if (distance1 == distance2)
            return 0;
        else if (distance1 > distance2)
            return 1;
        else
            return -1;
    }
}
