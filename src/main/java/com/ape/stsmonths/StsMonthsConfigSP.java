package com.ape.stsmonths;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class StsMonthsConfigSP {
    public static String DATA_LAST_DAY = "DATA_LAST_DAY";
    public static String DATA_LAST_MONTH = "DATA_LAST_MONTH";
    public static String DATA_LAST_SEND_DATE = "DATA_LAST_SEND_DATE";
    public static String DATA_LAST_YEAR = "DATA_LAST_YEAR";
    public static String DATA_SENDING_STATUS = "DATA_SENDING_STATUS";
    public static String DATA_SEND_TIMES = "DATA_SEND_TIMES";
    private static final String TAG = "StsMonths";
    Context mContext;

    public void init(Context context) {
        this.mContext = context;
    }

    public int readLastDay() {
        Context context = this.mContext;
        String str = StsMonthsService.STSDATA_CONFIG;
        Context context2 = this.mContext;
        return context.getSharedPreferences(str, 0).getInt(DATA_LAST_DAY, 0);
    }

    public boolean writeLastDay(int data) {
        Context context = this.mContext;
        String str = StsMonthsService.STSDATA_CONFIG;
        Context context2 = this.mContext;
        Editor ed = context.getSharedPreferences(str, 0).edit();
        ed.putInt(DATA_LAST_DAY, data);
        ed.commit();
        return true;
    }

    public int readLastMonth() {
        Context context = this.mContext;
        String str = StsMonthsService.STSDATA_CONFIG;
        Context context2 = this.mContext;
        return context.getSharedPreferences(str, 0).getInt(DATA_LAST_MONTH, 0);
    }

    public int readLastYear() {
        Context context = this.mContext;
        String str = StsMonthsService.STSDATA_CONFIG;
        Context context2 = this.mContext;
        return context.getSharedPreferences(str, 0).getInt(DATA_LAST_YEAR, 0);
    }

    public boolean writeLastMonth(int data) {
        Context context = this.mContext;
        String str = StsMonthsService.STSDATA_CONFIG;
        Context context2 = this.mContext;
        Editor ed = context.getSharedPreferences(str, 0).edit();
        ed.putInt(DATA_LAST_MONTH, data);
        ed.commit();
        return true;
    }

    public boolean writeLastYear(int data) {
        Context context = this.mContext;
        String str = StsMonthsService.STSDATA_CONFIG;
        Context context2 = this.mContext;
        Editor ed = context.getSharedPreferences(str, 0).edit();
        ed.putInt(DATA_LAST_YEAR, data);
        ed.commit();
        return true;
    }

    public int readSendTimes() {
        Context context = this.mContext;
        String str = StsMonthsService.STSDATA_CONFIG;
        Context context2 = this.mContext;
        return context.getSharedPreferences(str, 0).getInt(DATA_SEND_TIMES, 0);
    }

    public boolean writeSendTimes(int data) {
        Context context = this.mContext;
        String str = StsMonthsService.STSDATA_CONFIG;
        Context context2 = this.mContext;
        Editor ed = context.getSharedPreferences(str, 0).edit();
        ed.putInt(DATA_SEND_TIMES, data);
        ed.commit();
        Log.d(TAG, "writeSendTimes()  end times=" + data);
        return true;
    }

    public String readLastSendTDate() {
        String str = "";
        Context context = this.mContext;
        String str2 = StsMonthsService.STSDATA_CONFIG;
        Context context2 = this.mContext;
        return context.getSharedPreferences(str2, 0).getString(DATA_LAST_SEND_DATE, "");
    }

    public boolean writeLastSendTDate(String str) {
        Context context = this.mContext;
        String str2 = StsMonthsService.STSDATA_CONFIG;
        Context context2 = this.mContext;
        Editor ed = context.getSharedPreferences(str2, 0).edit();
        ed.putString(DATA_LAST_SEND_DATE, str);
        ed.commit();
        Log.d(TAG, "writeLastSendTDate()  end data=" + str);
        return true;
    }

    public boolean readSendingStatus() {
        Context context = this.mContext;
        String str = StsMonthsService.STSDATA_CONFIG;
        Context context2 = this.mContext;
        boolean bRet = context.getSharedPreferences(str, 0).getBoolean(DATA_SENDING_STATUS, false);
        Log.d(TAG, "readSendingStatus()  end bSendingStatus=" + bRet);
        return bRet;
    }

    public boolean writeSendingStatus(boolean bSendingStatus) {
        Context context = this.mContext;
        String str = StsMonthsService.STSDATA_CONFIG;
        Context context2 = this.mContext;
        Editor ed = context.getSharedPreferences(str, 0).edit();
        ed.putBoolean(DATA_SENDING_STATUS, bSendingStatus);
        ed.commit();
        Log.d(TAG, "writeSendingState()  end bSendingStatus=" + bSendingStatus);
        return true;
    }
}
