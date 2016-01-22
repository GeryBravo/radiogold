package app.radiogold.radiogold;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import app.radiogold.radiogold.helpers.Actions;
import app.radiogold.radiogold.services.StreamPlayerService;

public class MainActivity extends AppCompatActivity {

    private ToggleButton buttonStartStream;
    private Boolean stopService = false;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity","onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();

        buttonStartStream.setChecked(isMyServiceRunning(StreamPlayerService.class));

        buttonStartStream.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("MainActivity", "setOnCheckedChangeListener called");
                if(stopService) return;
                if (isChecked) {
                    if (!isOnline()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                        buttonStartStream.setChecked(false);
                        buttonStartStream.setBackgroundResource(R.drawable.play128);
                        return;
                        //If we have internet connection, we start the service
                    } else {
                        if(!isMyServiceRunning(StreamPlayerService.class)) {
                            Intent startIntent = new Intent(MainActivity.this, StreamPlayerService.class);
                            startIntent.setAction(Actions.START_SERVICE);
                            startService(startIntent);
                        } else {
                            Intent newIntent = new Intent(MainActivity.this, StreamPlayerService.class);
                            newIntent.setAction(Actions.PLAY_STREAM);
                            startService(newIntent);
                        }
                        buttonStartStream.setBackgroundResource(R.drawable.pause52);
                    }
                }
                //If the button is already checked, so we are streaming. We stop the service.
                else {
                    Intent stopIntent = new Intent(MainActivity.this, StreamPlayerService.class);
                    stopIntent.setAction(Actions.PLAY_STREAM);
                    startService(stopIntent);
                    buttonStartStream.setBackgroundResource(R.drawable.play128);
                }
            }

        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning(StreamPlayerService.class)) {
                    Intent stopIntent = new Intent(MainActivity.this, StreamPlayerService.class);
                    stopIntent.setAction(Actions.STOP_SERVICE);
                    startService(stopIntent);

                }
                buttonStartStream.setBackgroundResource(R.drawable.play128);
                stopService = true;
                buttonStartStream.setChecked(false);
                stopService = false;
            }
        });
    }

    private void initializeUI() {
        buttonStartStream = (ToggleButton) findViewById(R.id.buttonStartStream);
        stopButton = (Button) findViewById(R.id.buttonStop);
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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}
