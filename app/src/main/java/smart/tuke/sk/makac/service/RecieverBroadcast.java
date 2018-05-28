package smart.tuke.sk.makac.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.Toast;

import smart.tuke.sk.makac.R;
import smart.tuke.sk.makac.activities.TrackerActivity;

public class RecieverBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        if (TrackerActivity.getInstance() != null){
            Button start_btn = (Button) TrackerActivity.getInstance().findViewById(R.id.start_button);
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                start_btn.setEnabled(true);
            }
            else {
                Toast.makeText(context, "GPS Disable", Toast.LENGTH_SHORT).show();
                start_btn.setEnabled(false);
            }
        }

    }
}

