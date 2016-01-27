package app.radiogold.radiogold.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
public class StreamPlayerService extends Service implements MediaPlayer.OnErrorListener {

    private static final String LOG_TAG = "StreamPlayerService";
    private static final String URL = "http://37.221.209.146:6200/live.mp3";
    private static final String CATEGORY = "android.intent.category.LAUNCHER";
    private static final String WIFI_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    MediaPlayer mediaPlayer = null;
    private Notification notification;
    private NotificationManager notificationManager;
    private BroadcastReceiver wifiState;

    @Override
    public void onCreate() {
        super.onCreate();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WIFI_ACTION);
        wifiState = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();
                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    Log.d("WifiReceiver", "Have Wifi Connection");
                    //initializeMediaPlayer();
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d("WifiReceiver", "Don't have Wifi Connection");
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        //mediaPlayer.reset();
                    }
                }
            }
        };
        this.registerReceiver(wifiState,filter);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        initializeMediaPlayer();
        //streamData = new ParsingHeaderData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //GET FROM ACTIVITY
        if(intent.getAction().equals(Actions.START_SERVICE))
        {
            Log.d(LOG_TAG, "Actions.START_SERVICE received");
            startPlaying();
            buildNotification(android.R.drawable.ic_media_pause, "Pause", Actions.PAUSE_STREAM, "");
            startForeground(Actions.NOTIFICATION_ID, notification);
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
            if(!mediaPlayer.isPlaying())
            {
                startPlaying();
                buildNotification(android.R.drawable.ic_media_pause,"Pause",Actions.PAUSE_STREAM,"");
                notificationManager.notify(Actions.NOTIFICATION_ID, notification);
            }
        }
        else if(intent.getAction().equals(Actions.PAUSE_STREAM))
        {
            Log.d(LOG_TAG, "Actions.PAUSE_STREAM received");
            if(mediaPlayer.isPlaying())
            {
                buildNotification(android.R.drawable.ic_media_play,"Play", Actions.PLAY_STREAM,"");
                notificationManager.notify(Actions.NOTIFICATION_ID,notification);
                pausePlaying();
            }
        }
        return START_STICKY;
    }

    private void stopPlaying() {
        Log.d(LOG_TAG,"stopPlaying");
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private void startPlaying() {
        Log.d(LOG_TAG,"startPlaying");
        mediaPlayer.start();
    }

    private void pausePlaying() {
        Log.d(LOG_TAG, "pausePlaying");
        mediaPlayer.pause();
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
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
        this.unregisterReceiver(wifiState);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    private void buildNotification(int icon, String text, String action, String songData) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Actions.NOTI);
        notificationIntent.addCategory(CATEGORY);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, StreamPlayerService.class);
        playIntent.setAction(action);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        notification = new NotificationCompat.Builder(this)
                .setContentTitle("Radio Gold")
                .setSmallIcon(R.drawable.icon_rg)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(songData))
                .addAction(icon, text, pendingPlayIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(LOG_TAG, "onError");
        return false;
    }
}
