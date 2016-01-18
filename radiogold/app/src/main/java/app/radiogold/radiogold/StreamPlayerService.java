package app.radiogold.radiogold;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.util.Log;

import app.radiogold.radiogold.helpers.Actions;

/**
 * Created by gerybravo on 2016.01.18..
 */
public class StreamPlayerService extends Service {

    private static final String LOG_TAG = "StreamPlayerService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getAction())
        {
            case Actions.START_SERVICE:

                break;
        }
        return 1;
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
