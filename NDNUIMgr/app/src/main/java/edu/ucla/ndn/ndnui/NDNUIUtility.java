package edu.ucla.ndn.ndnui;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by SMehrotra on 1/10/2015.
 */
public class NDNUIUtility {
    public static void updateStatus(View view, int status) {

        TextView t = (TextView) view.findViewById(R.id.textView2);
        t.setTextColor(Color.parseColor("#FF0000"));
        // t.setBackgroundColor(Color.parseColor("#0000FF"));
        t.setText(status);
    }

    public static void updateSTButton(View view) {

        Button t = (Button) view.findViewById(R.id.button);
        t.setTextColor(Color.parseColor("#FF0000"));
        t.setEnabled(false);
        t = (Button) view.findViewById(R.id.button2);
        t.setTextColor(Color.parseColor("#FFFFFF"));
        t.setEnabled(true);
    }

    public static void updateSPButton(View view) {

        Button t = (Button) view.findViewById(R.id.button2);
        t.setTextColor(Color.parseColor("#FF0000"));
        t.setEnabled(false);
        t = (Button) view.findViewById(R.id.button);
        t.setTextColor(Color.parseColor("#FFFFFF"));
        t.setEnabled(true);
    }
}
