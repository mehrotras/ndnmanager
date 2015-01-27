package edu.ucla.ndn.ndnui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by SMehrotra on 1/7/2015.
 */
public class NDNReceiver extends BroadcastReceiver {
    private static final String CLASS_TAG = "NDNReceiver";

    // Prevents instantiation
    public NDNReceiver() {
    }

    // Called when the BroadcastReceiver gets an Intent it's registered to receive
    //@
    public void onReceive(Context context, Intent intent) {
        Log.d(CLASS_TAG, "onReceive:   " + context.toString());
        /*
         * Handle Intents here.
         */

    }
}
