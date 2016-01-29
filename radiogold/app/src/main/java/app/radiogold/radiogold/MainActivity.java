package app.radiogold.radiogold;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import app.radiogold.radiogold.helpers.Actions;
import app.radiogold.radiogold.services.StreamPlayerService;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "MainActivity";

    private ImageButton startButton;
    private ImageButton stopButton;
    private ImageButton linkButton;
    private IntentFilter mIntentFilter;

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Actions.BROADCAST_NO_NET)) {
                Toast.makeText(getApplicationContext(), "Megszakadt az internetkapcsolat!", Toast.LENGTH_SHORT);
            }
            if (intent.getAction().equals(Actions.BROADCAST_NET_AGAIN)) {
                Toast.makeText(getApplicationContext(), "Helyreállt az internetkapcsolat!", Toast.LENGTH_SHORT);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Actions.BROADCAST_NO_NET);
        mIntentFilter.addAction(Actions.BROADCAST_NET_AGAIN);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "onClick event");
                if (!isOnline()) {
                    Toast.makeText(getApplicationContext(), "Nincs internetkapcsolat!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isMyServiceRunning(StreamPlayerService.class)) {
                        Intent startService = new Intent(MainActivity.this, StreamPlayerService.class);
                        startService.setAction(Actions.START_SERVICE);
                        startService(startService);
                    } else {
                        Intent playStream = new Intent(MainActivity.this, StreamPlayerService.class);
                        playStream.setAction(Actions.PLAY_STREAM);
                        startService(playStream);
                    }
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning(StreamPlayerService.class)) {
                    Intent stopStream = new Intent(MainActivity.this, StreamPlayerService.class);
                    stopStream.setAction(Actions.PAUSE_STREAM);
                    startService(stopStream);
                }
            }
        });

        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://goldmusic.hu"));
                startActivity(browserIntent);
            }
        });
    }

    private void initializeUI() {
        startButton = (ImageButton) findViewById(R.id.buttonStart);
        stopButton = (ImageButton) findViewById(R.id.buttonStop);
        linkButton = (ImageButton) findViewById(R.id.buttonLink);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        Intent stopService = new Intent(this,StreamPlayerService.class);
        stopService.setAction(Actions.STOP_SERVICE);
        startService(stopService);
    }
}
