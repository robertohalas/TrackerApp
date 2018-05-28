package smart.tuke.sk.makac.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import smart.tuke.sk.makac.R;
import smart.tuke.sk.makac.data.ForecastItem;
import smart.tuke.sk.makac.data.LocationResult;
import smart.tuke.sk.makac.listener.GeocodingServiceListener;
import smart.tuke.sk.makac.listener.WeatherServiceListener;
import smart.tuke.sk.makac.other.UsersDatabase;
import smart.tuke.sk.makac.other.WeatherAdapter;
import smart.tuke.sk.makac.service.GoogleMapsGeocodingService;
import smart.tuke.sk.makac.service.WeatherCacheService;
import smart.tuke.sk.makac.service.YahooWeatherService;

public class WeatherActivity extends AppCompatActivity implements WeatherServiceListener, GeocodingServiceListener, LocationListener {

    private GoogleMapsGeocodingService geocodingService;
    private WeatherCacheService cacheService;
    private YahooWeatherService weatherService;
    private ArrayList<ForecastItem> forecastList = new ArrayList<>();
    private ListView listView;
    private UsersDatabase database;

    private ProgressDialog dialog;

    // counter for failed weather service attempts
    private int weatherServiceFailures = 0;

    private SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = (ListView) findViewById(R.id.listView);
        //listView.setAdapter(new WeatherAdapter(this, forecastList));

        weatherService = new YahooWeatherService(this);
        geocodingService = new GoogleMapsGeocodingService(this);
        cacheService = new WeatherCacheService(this);

        preferences = getPreferences(Context.MODE_PRIVATE);

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading));
        dialog.setCancelable(false);
        dialog.show();


        String cachedLocation = preferences.getString(getString(R.string.location), null);

        if (cachedLocation == null) {
            getWeatherFromCurrentLocation();
        } else {
            weatherService.refreshWeather(cachedLocation);
        }

    }

    private void getWeatherFromCurrentLocation() {
        // system's LocationManager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // medium accuracy for weather, good for 100 - 500 meters
        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_MEDIUM);

        String provider = locationManager.getBestProvider(locationCriteria, true);

        // single location update
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        locationManager.requestSingleUpdate(provider, this, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        database = new UsersDatabase(this);
        Cursor cursor = database.getData();
        cursor.moveToFirst();
        byte [] avatar = cursor.getBlob(cursor.getColumnIndex(database.CONTACTS_COLUMN_AVATAR));
        Bitmap image = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
        menu.getItem(0).setIcon(new BitmapDrawable(getResources(), image));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent myIntent = new Intent(getBaseContext(), TrackerActivity.class);
            startActivityForResult(myIntent, 0);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void serviceSuccess(ArrayList<ForecastItem> forecastList) {
        dialog.hide();
        this.forecastList = forecastList;
        listView.setAdapter(new WeatherAdapter(this, forecastList));
    }

    @Override
    public void serviceFailure(Exception exception) {
        // display error if this is the second failure
        if (weatherServiceFailures > 0) {
            dialog.hide();
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void geocodeSuccess(LocationResult location) {
        // completed geocoding successfully
        weatherService.refreshWeather(location.getAddress());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.location), location.getAddress());
        editor.commit();
    }

    @Override
    public void geocodeFailure(Exception exception) {
        // GeoCoding failedhe cache
        cacheService.load(this);
}

    @Override
    public void onLocationChanged(Location location) {
        geocodingService.refreshLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // OPTIONAL: implement your custom logic here
    }

    @Override
    public void onProviderEnabled(String s) {
        // OPTIONAL: implement your custom logic here
    }

    @Override
    public void onProviderDisabled(String s) {
        // OPTIONAL: implement your custom logic here
    }
}
