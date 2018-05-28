package smart.tuke.sk.makac.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import smart.tuke.sk.makac.R;
import smart.tuke.sk.makac.other.UsersDatabase;

public class SummaryActivity extends AppCompatActivity {

    private TextView duration_summary;
    private TextView distance_summary;
    private TextView calories_summary;
    private UsersDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        duration_summary = (TextView) findViewById(R.id.duration_summary);
        distance_summary = (TextView) findViewById(R.id.distance_summary);
        calories_summary = (TextView) findViewById(R.id.calories_summary);
        Bundle extras = getIntent().getExtras();
        String durationString_summary = extras.getString("duration_summary");
        String distanceString_summary = extras.getString("distance_summary");
        String caloriesString_summary = extras.getString("calories_summary");
        duration_summary.setText(durationString_summary);
        distance_summary.setText(distanceString_summary);
        calories_summary.setText(caloriesString_summary);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_summary, menu);
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

    public void sendEmail(View view){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        email.setType("vnd.android.cursor.item/email");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"xxx@ooo.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        email.putExtra(Intent.EXTRA_TEXT, "TEXT...");
        startActivity(Intent.createChooser(email, "Send mail using..."));
    }
}
