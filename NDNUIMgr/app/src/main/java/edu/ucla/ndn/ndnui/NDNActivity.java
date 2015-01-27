package edu.ucla.ndn.ndnui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import edu.ucla.ndn.ndnui.preference.Preferences;


public class NDNActivity extends ActionBarActivity {
    private static final String CLASS_TAG = "NDNActivity";
    // Tracks whether the app is in full-screen mode
    boolean mFullScreen;
    // A handle to the main screen view
    View mMainView;
    // An instance of the status broadcast receiver
    NDNReceiver mNDNReceiver;
    Intent mServiceIntent;


    /*
     * This callback is invoked by the system when the Activity is being killed
     * It saves the full screen status, so it can be restored when the Activity is restored
     *
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constants.EXTENDED_FULLSCREEN, mFullScreen);
        super.onSaveInstanceState(outState);
    }

    /*
    * This callback is invoked when the Activity is first created. It sets up the Activity's
    * window and initializes the Fragments associated with the Activity
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Sets fullscreen-related flags for the display
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean(Preferences.PREF_HIDEAPPBAR, true))
            requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (settings.getBoolean(Preferences.PREF_STATUSBAR, false))
            if (NDNService.getInstance() == null)
                startService(new Intent(this, NDNService.class));

        super.onCreate(savedInstanceState);
        // Inflates the main View, which will be the host View for the fragments
        mMainView = getLayoutInflater().inflate(R.layout.activity_ndn, null);

        // Sets the content view for the Activity
        setContentView(mMainView);

        // load settings
        // detect screen size
        CommonUtil.detectScreen(this);


        // set title
        this.setTitle(R.string.app_name);
        this.setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);


        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);

        // Adds a data filter for the HTTP scheme
        mStatusIntentFilter.addDataScheme("http");
        mNDNReceiver = new NDNReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mNDNReceiver, mStatusIntentFilter);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        Log.d(this.CLASS_TAG, "Version --> " + CommonUtil.getSDKVersion());
    }

    /*
         * This callback is invoked when the system is about to destroy the Activity.
         */
    @Override
    public void onDestroy() {

        // If the DownloadStateReceiver still exists, unregister it and set it to null
        if (mNDNReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mNDNReceiver);
            mNDNReceiver = null;
        }


        // Sets the main View to null
        mMainView = null;

        // Must always call the super method at the end.
        super.onDestroy();
    }

    /*
     * This callback is invoked when the system is stopping the Activity. It stops
     * background threads.
     */
    @Override
    protected void onStop() {

        // Cancel all the running threads managed by the NDNManager
        super.onStop();
    }

    public void startNdn(View view) {
        Log.d(CLASS_TAG, "Button clicked : Starting NDN  ");
//        view.animate().start();

        new StartOperation(this).execute();
        mServiceIntent = new Intent(this, NDNPullService.class);
        //start the ndn service
        mServiceIntent.setData(Uri.parse("starting ndn thread..."));
        // new NDNTask(this).execute(new URL("//"), url2, url3);
        this.startService(mServiceIntent);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("NDN notification")
                        .setContentText("NDN started !");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, NDNActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


// Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }

    public void stopNdn(View view) {
        Log.d(CLASS_TAG, "Button clicked : Stopping NDN  ");
        //  view.animate().start();

        new StopOperation(this).execute();
        mServiceIntent = new Intent(this, NDNPullService.class);
        //start the ndn service
        mServiceIntent.setData(Uri.parse("stopping ndn thread ..."));
        this.startService(mServiceIntent);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("NDN notification")
                        .setContentText("NDN Stopped !");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, NDNActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


// Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ndn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setFullScreen(boolean fullscreen) {
        // If full screen is set, sets the fullscreen flag in the Window manager
        getWindow().setFlags(
                fullscreen ? WindowManager.LayoutParams.FLAG_FULLSCREEN : 0,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Sets the global fullscreen flag to the current setting
        mFullScreen = fullscreen;

        // If the platform version is Android 3.0 (Honeycomb) or above
        if (Build.VERSION.SDK_INT >= 11) {

            // Sets the View to be "low profile". Status and navigation bar icons will be dimmed
            int flag = fullscreen ? View.SYSTEM_UI_FLAG_LOW_PROFILE : 0;

            // If the platform version is Android 4.0 (ICS) or above
            if (Build.VERSION.SDK_INT >= 14 && fullscreen) {

                // Hides all of the navigation icons
                flag |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            // Applies the settings to the screen View
            mMainView.setSystemUiVisibility(flag);

            // If the user requests a full-screen view, hides the Action Bar.
            if (fullscreen) {
                this.getActionBar().hide();
            } else {
                this.getActionBar().show();
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        Intent mServiceIntent;

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle stateBundle) {

            super.onCreate(stateBundle);
            Log.d(CLASS_TAG, "onCreate");

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ndn, container, false);
            Log.d(CLASS_TAG, "onCreateView");
            mServiceIntent = new Intent(getActivity(), NDNPullService.class);
            //start the ndn service
            mServiceIntent.setData(Uri.parse("Testing Service.."));
            getActivity().startService(mServiceIntent);
            return rootView;
        }


    }


}
