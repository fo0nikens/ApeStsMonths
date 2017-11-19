package com.ape.stsmonths;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StsMonthsBootReceiver extends BroadcastReceiver {
    private static final String TAG = "StsMonths";
    private int spacetime = 60;

    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d(TAG, "ACTION_BOOT_COMPLETED");
            Intent newIntent = new Intent(context, StsMonthsService.class);
            newIntent.putExtra(StsMonthsService.SEND_TO, StsMonthsService.SEND_TO_CUSTOM);
            context.startService(newIntent);
        }
    }
}
