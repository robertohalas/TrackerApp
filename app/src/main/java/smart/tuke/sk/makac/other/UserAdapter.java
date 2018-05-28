package smart.tuke.sk.makac.other;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import smart.tuke.sk.makac.R;
import smart.tuke.sk.makac.activities.LoginActivity;
import smart.tuke.sk.makac.activities.TrackerActivity;
import smart.tuke.sk.makac.activities.UsersActivity;

public class UserAdapter extends ArrayAdapter<UserData> {

    public UserAdapter(Context context, ArrayList<UserData> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        UserData userData = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.nameText);
        ImageView avatar = (ImageView) convertView.findViewById(R.id.avatar);
        Button edit = (Button) convertView.findViewById(R.id.edit);
        Button delete = (Button) convertView.findViewById(R.id.delete);
        Button change = (Button) convertView.findViewById(R.id.change);
        // Populate the data into the template view using the data object
        name.setText(userData.getName());
        avatar.setImageBitmap(userData.getAvatar());
        edit.setOnClickListener(new ClickHandler(userData.getId(), 1));
        delete.setOnClickListener( new ClickHandler(userData.getId(), 2));
        change.setOnClickListener( new ClickHandler(userData.getId(), 3));
        // Return the completed view to render on screen
        return convertView;
    }

    public class ClickHandler implements View.OnClickListener {

        private int id;
        private int typ;

        public ClickHandler (int id, int typ){
            this.id = id;
            this.typ = typ;
        }

        @Override
        public void onClick(View v) {
            UsersDatabase database = new UsersDatabase(v.getContext());

            if (typ == 1) {
                Cursor cursor = database.getDataId(id);
                cursor.moveToFirst();
                Intent myIntent = new Intent(v.getContext(), LoginActivity.class);
                myIntent.putExtra("Id", cursor.getInt(cursor.getColumnIndex(database.CONTACTS_COLUMN_ID)));
                myIntent.putExtra("Avatar", cursor.getBlob(cursor.getColumnIndex(database.CONTACTS_COLUMN_AVATAR)));
                myIntent.putExtra("Name", cursor.getString(cursor.getColumnIndex(database.CONTACTS_COLUMN_NAME)));
                myIntent.putExtra("Weight", cursor.getString(cursor.getColumnIndex(database.CONTACTS_COLUMN_WEIGHT)));
                ((Activity) v.getContext()).startActivityForResult(myIntent, 0);
            } else if (typ == 2) {
                database.deleteUser(id);
                if (database.numberOfRows() == 0) {
                    Intent myIntent = new Intent(v.getContext(), LoginActivity.class);
                    v.getContext().startActivity(myIntent);
                    ((Activity) v.getContext()).finish();
                } else {
                    Intent myIntent = new Intent(v.getContext(), UsersActivity.class);
                    v.getContext().startActivity(myIntent);
                    ((Activity) v.getContext()).finish();
                }
            } else if (typ == 3) {
                database.changeCurrent(id);
                    Intent myIntent = new Intent(v.getContext(), TrackerActivity.class);
                    v.getContext().startActivity(myIntent);
                    ((Activity) v.getContext()).finish();
            }
        }
    }
}
