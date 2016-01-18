package app.radiogold.radiogold.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.renderscript.RenderScript;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import app.radiogold.radiogold.MainActivity;
import app.radiogold.radiogold.R;
import app.radiogold.radiogold.helpers.Actions;

/**
 * Created by gerybravo on 2016.01.18..
 */
public class StreamPlayerService extends Service {

    private static final String LOG_TAG = "StreamPlayerService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //GET FROM ACTIVITY
        if(intent.getAction().equals(Actions.START_SERVICE))
        {
            Log.d(LOG_TAG, "Actions.START_SERVICE received");
            createNotification();
            initializeMediaPlayer();
        }
        else if(intent.getAction().equals(Actions.STOP_SERVICE))
        {
            Log.d(LOG_TAG,"Actions.STOP_SERVICE received");
            stopForeground(true);
            stopSelf();
        }
        //GET FROM NOTIFICATION
        else if(intent.getAction().equals(Actions.PLAY_STREAM))
        {
            Log.d(LOG_TAG,"Actions.PLAY_STREAM received");
        }
        else if(intent.getAction().equals(Actions.PREV_STREAM))
        {
            Log.d(LOG_TAG,"Actions.PREV_STREAM received");
        }
        else if(intent.getAction().equals(Actions.NEXT_STREAM))
        {
            Log.d(LOG_TAG,"Actions.NEXT_STREAM received");
        }
        return START_STICKY;
    }

    private void initializeMediaPlayer() {
        //TODO PLAYING STREAM
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

    private void createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Actions.NOTI);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        Intent playIntent = new Intent(this, StreamPlayerService.class);
        playIntent.setAction(Actions.PLAY_STREAM);
        PendingIntent pendingPreviousIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent previousIntent = new Intent(this, StreamPlayerService.class);
        previousIntent.setAction(Actions.PREV_STREAM);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        Intent nextIntent = new Intent(this, StreamPlayerService.class);
        nextIntent.setAction(Actions.NEXT_STREAM);
        PendingIntent pendingNextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Radio Gold")
                .setSmallIcon(R.drawable.icon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("- stream neve -"))
                .addAction(android.R.drawable.ic_media_previous, "Prev", pendingPreviousIntent)
                .addAction(android.R.drawable.ic_media_play, "Play", pendingPlayIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", pendingNextIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(Actions.NOTIFICATION_ID,notification);
    }
}
