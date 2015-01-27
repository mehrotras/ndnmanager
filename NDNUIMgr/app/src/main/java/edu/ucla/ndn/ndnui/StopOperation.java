package edu.ucla.ndn.ndnui;

/**
 * Created by SMehrotra on 1/11/2015.
 */

import android.os.AsyncTask;
import android.util.Log;

import edu.ucla.ndn.util.Shell;

public class StopOperation extends AsyncTask<String, Void, String> {
    private static final String CLASS_TAG = "StopOperation";
    NDNActivity ndnActivity;
    boolean success;

    public StopOperation(NDNActivity activity) {
        this.ndnActivity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(CLASS_TAG, "On doInBackground...");

        if (Shell.SU.available()) {
            Shell.SU.run(CommonUtil.NFDStopCMD);
            success = true;
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("PostExecute", result);
        if (success) {
            NDNUIUtility.updateStatus(this.ndnActivity.findViewById(R.id.textView2), R.string.ndn_status_stop);
            NDNUIUtility.updateSPButton(this.ndnActivity.mMainView);
        } else
            NDNUIUtility.updateStatus(this.ndnActivity.findViewById(R.id.textView2), R.string.ndn_status_stop_err);
    }

    @Override
    protected void onPreExecute() {
        Log.d("PreExceute", "On pre Exceute......");
        if (success) {
            NDNUIUtility.updateStatus(this.ndnActivity.findViewById(R.id.textView2), R.string.ndn_status_pre);
            NDNUIUtility.updateSPButton(this.ndnActivity.mMainView);
        } else
            NDNUIUtility.updateStatus(this.ndnActivity.findViewById(R.id.textView2), R.string.ndn_status_stop_err);


    }

    @Override
    protected void onProgressUpdate(Void... values) {
        //    if (success)
        NDNUIUtility.updateStatus(this.ndnActivity.findViewById(R.id.textView2), R.string.ndn_status_stop_prog);
        //   else
        //      NDNUIUtility.updateStatus(this.ndnActivity.findViewById(R.id.textView2), R.string.ndn_status_stop_err);
    }
}