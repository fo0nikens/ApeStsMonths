package com.ape.stsmonths;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.internal.telephony.TelephonyIntents;

public class StsMonthsSecretCode extends BroadcastReceiver {
    private static final String TAG = "StsMonths";

    public void onReceive(Context arg0, Intent arg1) {
        if (arg1.getAction().equals(TelephonyIntents.SECRET_CODE_ACTION)) {
            Log.d(TAG, "StsMonthsSecretCode start");
            try {
                Intent intent = new Intent();
                intent.setClassName(BuildConfig.APPLICATION_ID, "com.ape.stsmonths.StsMonthsActivity");
                intent.setFlags(268435456);
                arg0.startActivity(intent);
            } catch (Exception e) {
            }
        }
    }
}
