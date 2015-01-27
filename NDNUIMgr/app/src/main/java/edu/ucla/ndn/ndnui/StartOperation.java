package edu.ucla.ndn.ndnui;

import android.os.AsyncTask;
import android.util.Log;

import edu.ucla.ndn.util.Shell;

/**
 * Created by SMehrotra on 1/11/2015.
 */
public class StartOperation extends AsyncTask<String, Void, String> {
    private static final String CLASS_TAG = "StartOperation";
    NDNActivity ndnActivity;
    boolean success;

    public StartOperation(NDNActivity activity) {
        this.ndnActivity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(CLASS_TAG, "On doInBackground...");
        success = Shell.SU.available();
        if (success) {
            //Shell.SU.run(CommonUtil.NFDStartCMD1);
            try {
                CommonUtil.execCommand(CommonUtil.NFDStartCMD1);
            } catch (Exception e) {
                Log.e(CLASS_TAG, "On doInBackground..." + e.getMessage());
                success = false;
            }
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("PostExecute", result);
        if (success) {
            NDNUIUtility.updateStatus(this.ndnActivity.findViewById(R.id.textView2), R.string.ndn_status_run);
            NDNUIUtility.updateSTButton(this.ndnActivity.mMainView);
        } else
            NDNUIUtility.updateStatus(this.ndnActivity.findViewById(R.id.textView2), R.string.ndn_status_start_err);
    }

    @Override
    protected void onPreExecute() {

        if (success) {
            NDNUIUtility.updateStatus(this.ndnActivity.findViewById(R.id.textView2), R.string.ndn_status_pre);
            NDNUIUtility.updateSTButton(this.ndnActivity.mMainView);
        } else
            NDNUIUtility.updateStatus(this.ndnActivity.findViewById(R.id.textView2), R.string.ndn_status_start_err);


    }

    @Override
    protected void onProgressUpdate(Void... values) {
        if (success)
            NDNUIUtility.updateStatus(this.ndnActivity.findViewById(R.id.textView2), R.string.ndn_status_start_prog);
        else
            NDNUIUtility.updateStatus(this.ndnActivity.findViewById(R.id.textView2), R.string.ndn_status_start_err);
    }
}