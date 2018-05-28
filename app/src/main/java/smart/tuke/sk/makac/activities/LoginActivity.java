package smart.tuke.sk.makac.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import smart.tuke.sk.makac.R;
import smart.tuke.sk.makac.other.UsersDatabase;

public class LoginActivity extends AppCompatActivity {

    private UsersDatabase database;
    private EditText weight;
    private EditText name;
    private Button login_btn;
    private boolean edit = false;
    private int id;
    private ImageView view;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = (EditText) findViewById(R.id.name);
        weight = (EditText) findViewById(R.id.weight);
        login_btn = (Button) findViewById(R.id.login_button);
        view = (ImageView) findViewById(R.id.imageView8);
        database = new UsersDatabase(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.getBoolean("New") == false){
                id = extras.getInt("Id");
                byte[] avatar = extras.getByteArray("Avatar");
                image = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                String name2 = extras.getString("Name");
                String weight2 = extras.getString("Weight");
                login_btn.setText(getString(R.string.save_btn));
                edit = true;
                view.setImageBitmap(image);
                name.setText(name2);
                weight.setText(weight2);
            }
        }
        else if (database.numberOfRows() > 0){
            Intent myIntent = new Intent(getBaseContext(), TrackerActivity.class);
            startActivityForResult(myIntent, 0);
            finish();
        }

    }


    public void onSaveClicked(View view){
            if(name.getText().toString() != null && name.getText().toString().length() >0 ){
                if(weight.getText().toString() != null && weight.getText().toString().length() >0 ){
                    if (image != null){
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 0, stream);
                        if (edit){
                            database.updateUser(id, name.getText().toString(), weight.getText().toString(), stream.toByteArray());
                            Intent myIntent = new Intent(getBaseContext(), TrackerActivity.class);
                            startActivityForResult(myIntent, 0);
                            finish();
                            Toast.makeText(getApplicationContext(), R.string.update_data , Toast.LENGTH_LONG).show();
                        } else {
                            database.insertUser(name.getText().toString(), weight.getText().toString(), stream.toByteArray());
                            Intent myIntent = new Intent(getBaseContext(), TrackerActivity.class);
                            startActivityForResult(myIntent, 0);
                            finish();
                            Toast.makeText(getApplicationContext(), R.string.saved_to_database , Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), R.string.make_photo , Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), R.string.enter_weight , Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), R.string.enter_name , Toast.LENGTH_LONG).show();
            }
    }

    public void onPhotoClicked(View view){
        Intent camera_intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera_intent, 0);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelable("BitmapImage", image);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        image = savedInstanceState.getParcelable("BitmapImage");
        view.setImageBitmap(image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 0:
                if(resultCode==RESULT_OK){
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    this.image = thumbnail;
                    view.setImageBitmap(this.image);
                }
        }
    }


}
