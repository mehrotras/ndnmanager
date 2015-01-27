package edu.ucla.ndn.ndnui;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by SMehrotra on 1/6/2015.
 * The IntentService class provides a straightforward structure for running an operation on a single
 * background thread. This allows it to handle long-running operations without affecting your user
 * interface's responsiveness. Also, an IntentService isn't affected by most user interface lifecycle
 * events, so it continues to run in circumstances that would shut down an AsyncTask
 */
public class NDNPullService extends IntentService {

    private static final String CLASS_TAG = "NDNService";

    public NDNPullService() {
        super(CLASS_TAG);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        Log.d(CLASS_TAG, "onHandleIntent:   " + dataString);

    }
}
