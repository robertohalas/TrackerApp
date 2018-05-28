package smart.tuke.sk.makac.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import smart.tuke.sk.makac.R;
import smart.tuke.sk.makac.other.CircularProgressView;
import smart.tuke.sk.makac.other.GPSTracker;
import smart.tuke.sk.makac.other.UsersDatabase;
import smart.tuke.sk.makac.service.NotificationService;
import smart.tuke.sk.makac.service.RecieverBroadcast;

public class TrackerActivity extends AppCompatActivity implements Runnable {

    private Handler mHandler = new Handler();
    private long start;
    private long pause;
    private static TrackerActivity ins;
    private Button start_btn;
    private Button pause_btn;
    private Button resume_btn;
    private Button reset_btn;
    private Runnable m_handlerTask ;
    private TextView duration;
    private TextView pace;
    private TextView distance;
    private TextView calories;
    private GPSTracker gpsTracker;
    private double distanceNumb = 0;
    private CircularProgressView progressView;
    private Thread updateThread;
    private UsersDatabase database;
    private int weight;
    private ArrayList<Location> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        gpsTracker = new GPSTracker(this);
        gpsTracker.getLocation();
        database = new UsersDatabase(this);
        Cursor rs = database.getData();
        rs.moveToFirst();
        weight = Integer.parseInt(rs.getString(rs.getColumnIndex(UsersDatabase.CONTACTS_COLUMN_WEIGHT)));
        setContentView(R.layout.activity_tracker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        start_btn = (Button) findViewById(R.id.start_button);
        pause_btn = (Button) findViewById(R.id.pause_button);
        resume_btn = (Button) findViewById(R.id.resume_button);
        reset_btn = (Button) findViewById(R.id.reset_button);
        duration = (TextView) findViewById(R.id.duration);
        distance = (TextView) findViewById(R.id.distance);
        pace = (TextView) findViewById(R.id.pace);
        calories = (TextView) findViewById(R.id.calories);
        progressView = (CircularProgressView) findViewById(R.id.progress_view);
        ins = this;
        getApplicationContext().registerReceiver(new RecieverBroadcast(), new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        Intent intent = new Intent();
        intent.setAction("smart.tuke.sk.makac.GPS_CHECK");
        sendBroadcast(intent);
    }

    public static TrackerActivity  getInstance(){
        return ins;
    }

    private void startAnimationThreadStuff(long delay) {
        if (updateThread != null && updateThread.isAlive())
            updateThread.interrupt();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!progressView.isIndeterminate()) {
                    progressView.setProgress(0f);
                    updateThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (progressView.getProgress() < progressView.getMaxProgress() && !Thread.interrupted()) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressView.setProgress(progressView.getProgress() + 10);
                                    }
                                });
                                SystemClock.sleep(250);
                            }
                        }
                    });
                    updateThread.start();
                }
                progressView.startAnimation();
            }
        }, delay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tracker, menu);
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
        if (id == R.id.id_database) {
            Intent myIntent = new Intent(getBaseContext(), UsersActivity.class);
            startActivityForResult(myIntent, 0);
            mHandler.removeCallbacks(m_handlerTask);
            finish();
        }
        if (id == R.id.id_weather) {
            Intent myIntent = new Intent(getBaseContext(), WeatherActivity.class);
            startActivityForResult(myIntent, 0);
            mHandler.removeCallbacks(m_handlerTask);
            finish();
        }
        if (id == R.id.map) {
            onMapsClicked();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public String format(long milliseconds) {
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        milliseconds -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds);
        if (hours < 1) {
            return String.format("%02d:%02d.%d", (int) minutes, (int) seconds, (int) milliseconds / 100);
        } else {
            return String.format("%02d:%02d:%d", (int) hours, (int) minutes, (int) seconds);
        }
    }

    @Override
    public void run() {
        mHandler.post(m_handlerTask = new Runnable() {
            @Override
            public void run() {
                double temp = distanceNumb+gpsTracker.getDistance();
                Location loc = gpsTracker.getLocation();
                if (list.size() == 0 && loc != null){
                    list.add(loc);
                }
                if (temp > distanceNumb){
                    distanceNumb = temp;
                    list.add(gpsTracker.getLocation());
                    Intent service = new Intent(ins, NotificationService.class);
                    service.putExtra("Subtext", String.format("Duration: %s\nDistance: %f\n ", format((System.currentTimeMillis() - start) + pause), distanceNumb));
                    ins.startService(service);
                    distance.setText((String.format("%.2f", distanceNumb/1000)).replace(",", "."));
                    calories.setText((String.format("%.0f", weight * 2.235 * (distanceNumb / 1000))).replace(",", "."));
               }
                pace.setText((String.format("%.1f", gpsTracker.getSpeed())).replace(",", "."));
                duration.setText(format((System.currentTimeMillis() - start) + pause));
                mHandler.postDelayed(this, 100);
            }
        });
    }

    public void onMapsClicked(){
        Intent myIntent = new Intent(getBaseContext(), MapsActivity.class);
        myIntent.putExtra("Latitude", gpsTracker.getLatitude());
        myIntent.putExtra("Longitude", gpsTracker.getLongitude());
        myIntent.putExtra("list", list);
        startActivityForResult(myIntent, 0);
    }

    public void onStartClicked(View view){
        start_btn.setVisibility(View.INVISIBLE);
        pause_btn.setVisibility(View.VISIBLE);
        resume_btn.setVisibility(View.INVISIBLE);
        reset_btn.setVisibility(View.INVISIBLE);
        progressView.setVisibility(View.VISIBLE);
        start = System.currentTimeMillis();
        run();
        gpsTracker.setLocationA();
        startAnimationThreadStuff(0);
    }

    public void onPauseClicked(View view){
        start_btn.setVisibility(View.INVISIBLE);
        pause_btn.setVisibility(View.INVISIBLE);
        resume_btn.setVisibility(View.VISIBLE);
        reset_btn.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.INVISIBLE);
        mHandler.removeCallbacks(m_handlerTask);
        pause = pause + (System.currentTimeMillis()-start);
        start =System.currentTimeMillis();
    }

    public void onResumeClicked(View view){
        start_btn.setVisibility(View.INVISIBLE);
        pause_btn.setVisibility(View.VISIBLE);
        resume_btn.setVisibility(View.INVISIBLE);
        reset_btn.setVisibility(View.INVISIBLE);
        progressView.setVisibility(View.VISIBLE);
        start = System.currentTimeMillis();
        run();
    }

    public void onResetClicked(View view){
        start_btn.setVisibility(View.VISIBLE);
        pause_btn.setVisibility(View.INVISIBLE);
        resume_btn.setVisibility(View.INVISIBLE);
        reset_btn.setVisibility(View.INVISIBLE);
        progressView.setVisibility(View.INVISIBLE);
        mHandler.removeCallbacks(m_handlerTask);
        start=0;
        pause=0;
        Intent myIntent = new Intent(view.getContext(), SummaryActivity.class);
        String durationString = duration.getText().toString();
        String distanceString = distance.getText().toString();
        String caloriesString = calories.getText().toString();
        myIntent.putExtra("duration_summary",durationString);
        myIntent.putExtra("distance_summary", distanceString);
        myIntent.putExtra("calories_summary", caloriesString);
        startActivityForResult(myIntent, 0);
        mHandler.removeCallbacks(m_handlerTask);
        finish();
    }
}
