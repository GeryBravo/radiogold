package app.radiogold.radiogold.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

import app.radiogold.radiogold.MainActivity;
import app.radiogold.radiogold.R;
import app.radiogold.radiogold.helpers.Actions;

/**
 * Created by gerybravo on 2016.01.18..
 */
public class StreamPlayerService extends Service {

    private static final String LOG_TAG = "StreamPlayerService";
    private static final String URL = "http://37.221.209.146:6200/live.mp3";

    private MediaPlayer mediaPlayer;
    private Notification notification;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //GET FROM ACTIVITY
        if(intent.getAction().equals(Actions.START_SERVICE))
        {
            Log.d(LOG_TAG, "Actions.START_SERVICE received");
            buildNotification(android.R.drawable.ic_media_pause, "Pause");
            startForeground(Actions.NOTIFICATION_ID,notification);
            initializeMediaPlayer();
            startPlaying();
        }
        else if(intent.getAction().equals(Actions.STOP_SERVICE))
        {
            Log.d(LOG_TAG,"Actions.STOP_SERVICE received");
            stopPlaying();
            stopForeground(true);
            stopSelf();
        }
        //GET FROM NOTIFICATION
        else if(intent.getAction().equals(Actions.PLAY_STREAM))
        {
            Log.d(LOG_TAG,"Actions.PLAY_STREAM received");
            if(mediaPlayer.isPlaying())
            {
                buildNotification(android.R.drawable.ic_media_play,"Play");
                notificationManager.notify(Actions.NOTIFICATION_ID,notification);
                stopPlaying();
            }
            else
            {
                buildNotification(android.R.drawable.ic_media_pause,"Pause");
                notificationManager.notify(Actions.NOTIFICATION_ID,notification);
                startPlaying();
            }
        }
        return START_STICKY;
    }

    private void startPlaying() {
        Log.d(LOG_TAG,"startPlaying");
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlaying()
    {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            initializeMediaPlayer();
        }
    }

    private void initializeMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(URL);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void buildNotification(int icon, String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Actions.NOTI);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent playIntent = new Intent(this, StreamPlayerService.class);
        playIntent.setAction(Actions.PLAY_STREAM);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        notification = new NotificationCompat.Builder(this)
                .setContentTitle("Radio Gold")
                .setSmallIcon(R.drawable.icon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("- stream neve -"))
                .addAction(icon, text, pendingPlayIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .build();
    }
}
