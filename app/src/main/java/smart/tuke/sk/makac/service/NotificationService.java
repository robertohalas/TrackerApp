package smart.tuke.sk.makac.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import smart.tuke.sk.makac.R;

public class NotificationService extends IntentService{

    public NotificationService (){
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String text = extras.getString("Subtext");
        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.icon_makac)
                        .setContentTitle("Makač")
                        .setContentText(text);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification.build());
    }

}
