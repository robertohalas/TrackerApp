package smart.tuke.sk.makac.activities;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import smart.tuke.sk.makac.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat,lon;
    private ArrayList<Location> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Bundle extras = getIntent().getExtras();
        list = (ArrayList<Location>)getIntent().getExtras().getSerializable("list");
        lat =  extras.getDouble("Latitude");
        lon =  extras.getDouble("Longitude");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        if (list.size() > 0) {
            LatLng start = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
            mMap.addMarker(new MarkerOptions().position(start).title("Start"));

            PolylineOptions options = new PolylineOptions();
            for (Location location : list) {
                options.add(new LatLng(location.getLatitude(), location.getLongitude()))
                        .color(Color.BLUE)
                        .width(5);
            }
            mMap.addPolyline(options);

            LatLng finish = new LatLng(list.get(list.size() - 1).getLatitude(), list.get(list.size() - 1).getLongitude());
            mMap.addMarker(new MarkerOptions().position(finish).title("Finish"));

            CameraPosition camPos = new CameraPosition.Builder()
                    .target(finish)
                    .zoom(15)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
        }
        else Toast.makeText(this, R.string.not_found_location , Toast.LENGTH_SHORT).show();
    }
}
