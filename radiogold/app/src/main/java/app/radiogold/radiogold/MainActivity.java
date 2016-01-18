package app.radiogold.radiogold;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private ToggleButton buttonStartStream;
    private Boolean streaming = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getUI();

        buttonStartStream.setActivated(true);

        buttonStartStream.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if (!isOnline()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                        streaming = false;
                        buttonStartStream.setChecked(false);
                        return;
                    } else {
                        Intent startIntent = new Intent(MainActivity.this, StreamPlayerService.class);
                        startIntent.setAction("app.radiogold.streamplayerservice.action.startforeground");
                        startService(startIntent);
                        streaming = true;
                    }
                }
                //If the button is already checked, so we are streaming
                else {
                    Intent stopIntent = new Intent(MainActivity.this, StreamPlayerService.class);
                    stopIntent.setAction("app.radiogold.streamplayerservice.action.stopforeground");
                    startService(stopIntent);
                    streaming = false;
                }
            }

        });
    }

    private void getUI() {
        buttonStartStream = (ToggleButton) findViewById(R.id.buttonStartStream);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
