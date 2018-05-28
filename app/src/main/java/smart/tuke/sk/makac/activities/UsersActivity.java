package smart.tuke.sk.makac.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import smart.tuke.sk.makac.R;
import smart.tuke.sk.makac.other.UserAdapter;
import smart.tuke.sk.makac.other.UserData;
import smart.tuke.sk.makac.other.UsersDatabase;

public class UsersActivity extends AppCompatActivity {

    private ArrayList<UserData> list = new ArrayList<>();
    private UsersDatabase database;
    private ImageButton add;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        database = new UsersDatabase(this);
        add = (ImageButton) findViewById(R.id.add);
                Cursor cursor = database.getUsers();
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false){
            byte [] avatar = cursor.getBlob(cursor.getColumnIndex(database.CONTACTS_COLUMN_AVATAR));
            Bitmap icon = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
            UserData userData = new UserData(cursor.getInt(cursor.getColumnIndex(database.CONTACTS_COLUMN_ID)),
                    icon,
                    cursor.getString(cursor.getColumnIndex(database.CONTACTS_COLUMN_NAME)),1);
            list.add(userData);
            cursor.moveToNext();
        }
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new UserAdapter(this ,list));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_users, menu);
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

    public void onAddClicked(View view){
        Intent myIntent = new Intent(getBaseContext(), LoginActivity.class);
        myIntent.putExtra("New", true);
        startActivityForResult(myIntent, 0);
        finish();
    }

}

