package smart.tuke.sk.makac.other;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public final class GPSTracker implements LocationListener {

    // Declaring a my context
    private final Context myContext;

    // Declaring a locationA
    private Location locationA = new Location("LocationA");

    // flag for GPS status
    private boolean isGPSEnabled = false;

    // Declaring a location
    private Location location;

    // Declaring a latitude
    private double latitude;

    // Declaring a longitude
    private double longitude;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 100; // 1 minute

    // Declaring a my location manager
    private LocationManager myLocationManager;

    /**
     * Constructor GPSTracker
     **/
    public GPSTracker(Context context) {
        this.myContext = context;
        getLocation();
    }

    /**
     * Function to get the user's current location
     * @return location
     **/
    public Location getLocation() {
        try {
            myLocationManager = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // if GPS is Disable show settings alert
            if (isGPSEnabled == false) {
            } else {

                // if GPS is Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    // check self manifest permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (myContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && myContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return null;
                        }
                    }
                    if (location == null) {
                        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (myLocationManager != null) {
                            location = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(myContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return location;
    }

    /**
     * Function to set locationA
     **/
    public void setLocationA (){
        locationA = getLocation();
    }

    public double getSpeed(){
        if (getLocation()!=null) {
            return getLocation().getSpeed();
        }
        return 0;
    }
    /**
     * Function to get distance
     **/
    public float getDistance(){
        Location locationB = getLocation();
        if(getLatitude() == 0 || getLongitude() == 0) {
            return 0;
        }
        float distance = locationA.distanceTo(locationB);
        locationA = locationB;
        return distance;
    }

    /**
     * Function to get latitude
     **/
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    /**
     * Function to get longitude
     **/
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onProviderDisabled(String provider) {
        String msg = "GPS Disabled";
        Toast.makeText(myContext, msg , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        String msg = "GPS Enabled";
        Toast.makeText(myContext, msg , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}