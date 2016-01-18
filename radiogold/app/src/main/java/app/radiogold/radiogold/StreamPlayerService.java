package app.radiogold.radiogold;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import app.radiogold.radiogold.helpers.Actions;

/**
 * Created by gerybravo on 2016.01.18..
 */
public class StreamPlayerService extends Service {

    private static final String LOG_TAG = "StreamPlayerService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //GET FROM ACTIVITY
        if(intent.getAction().equals(Actions.START_SERVICE))
        {
            Log.d(LOG_TAG, "Actions.START_SERVICE received");

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Actions.NOTI);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Radio Gold")
                    .setTicker("Radio Gold")
                    .setContentText("My Stream")
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();

            startForeground(Actions.NOTIFICATION_ID,notification);
        }
        else if(intent.getAction().equals(Actions.STOP_SERVICE))
        {
            Log.d(LOG_TAG,"Actions.STOP_SERVICE received");
        }
        //GET FROM NOTIFICATION
        else if(intent.getAction().equals(Actions.PLAY_STREAM))
        {
            Log.d(LOG_TAG,"Actions.PLAY_STREAM received");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }
}
