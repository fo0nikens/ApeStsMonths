package com.ape.stsmonths;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.wrapper.stk.HideMethod.TelephonyManager;

import java.util.Calendar;
import java.util.Map;

public class StsMonthsService extends Service {
    private static final int ACTION_SEND_BY_NET = 8193;
    private static final int ACTION_SEND_BY_SMS = 8192;
    private static final int ACTION_SEND_RST_BY_NET = 8194;
    private static final String CONFIG_CLIENT_NO = "client_no";
    private static final String CONFIG_HOST_URL = "host_url";
    public static int DEFAULT_SPACETIME = 60;
    public static int DEFAULT_STARTTIME = 180;
    public static final String KEY_DAY_TIME = "KEY_DAY_TIME";
    public static final String KEY_NOTIFY = "KEY_NOTIFY";
    public static final String KEY_OPEN_TIME = "KEY_OPEN_TIME";
    public static final String KEY_SELECT_SEND_TYPE = "KEY_SELECT_SEND_TYPE";
    public static final String KEY_SPACE_TIME = "KEY_SPACE_TIME";
    public static final String KEY_SWITCH_SENDTYPE = "KEY_SWITCH_SENDTYPE";
    private static final int MAX_SEND_CONUT_BY_NET = 2160;
    private static final int MAX_SEND_COUNT_BY_SMS = 3;
    private static final int MSG_SEND_BY_NET = 1;
    private static final int MSG_SEND_BY_SMS = 0;
    public static final String NULL_IMEI = "000000000000000";
    public static final String SEND_TO = "send_to";
    public static final String SEND_TO_CUSTOM = "send_to_custom";
    public static final String STSDATA_CONFIG = "STSDATA_CONFIG";
    public static final int STS_CONFIG_TYPE = 3;
    public static final int STS_JNI = 1;
    public static final int STS_NV = 2;
    public static final int STS_SP = 3;
    private static final String TAG = "StsMonths";
    public static int mDefaultSendType = 1;
    public static final String mVersion = "Version :  7.0.10.07";
    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCY4gRmZHQimOWRr99Yi64jGDGMJSa7Awx05J9gpJuQz9tZPrP6QCWFJNpBxBxS_UMg-36FjFl_l8qLBWl-q7pVlyc4qdxq4HGQKJfdBm8aOFQ3Ekaylm1p2s5YKxvYTHDydKG72EXDdvbea8ZvXA1rKP-MpOWKA7XmkLpChQqrsQIDAQAB";

    @SuppressLint("HandlerLeak")
    Handler MessageHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StsMonthsService.ACTION_SEND_BY_NET /*8193*/:
                    Log.d(StsMonthsService.TAG, "handleMessage()  send type is by NET");
                    mSendTimes = mSendTimes + 1;
                    writeSendTimes(mSendTimes);
                    writeLastSendTDate(Calendar.getInstance().getTime().toString());
                    if (isNetworkAvailable()) {
                        sendContentByNetwork();
                        break;
                    }
                    break;
                case 8194:
                    Log.d(StsMonthsService.TAG, "handleMessage()  ACTION_SEND_RST_BY_NET");
                    Boolean val = Boolean.valueOf(msg.getData().getBoolean("value", false));
                    Log.d(StsMonthsService.TAG, "handleMessage()  sended by net and  return  =" + val);
                    if (val.booleanValue()) {
                        Calendar cl2 = Calendar.getInstance();
                        int sDay = cl2.get(5);
                        int sMonth = cl2.get(2) + 1;
                        int sYear = cl2.get(1);
                        writeLastYear(sYear);
                        writeLastMonth(sMonth);
                        writeLastDay(sDay);
                        Log.d(StsMonthsService.TAG, "handleMessage()  :  iYear = " + sYear + " iMonth=" + sMonth + "  iDay=" + sDay);
                        mIsSendSuccess = true;
                        mIsSending = false;
                        writeSendingStatus(mIsSending);
                        mIsFirstTimeSending = false;
                        ((AlarmManager) mContext.getSystemService(ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("STS_MONTHS"), 134217728));
                        break;
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private String mClientNo = "0000000001";
    private Context mContext;
    private long mCurrHour = 0;
    private long mCurrMinute = 0;
    private String mHosturl = "http://eservice.tinno.com/eservice/stsReport?reptype=report";
    private boolean mIsFirstTimeSending = false;
    private boolean mIsSendSuccess = false;
    private boolean mIsSending = false;
    private boolean mIsTimeOK = false;
    private int mSendTimes = 0;
    public int mSpaceTime = DEFAULT_SPACETIME;
    private int mStartTime = DEFAULT_STARTTIME;
    StsMonthsConfigSP mStciSP = new StsMonthsConfigSP();
    public String mStrIMEI = NULL_IMEI;
    private final BroadcastReceiver mStsAirplanReceiver = new StsAirplanReceiver();
    private final BroadcastReceiver mStsMonthsReceiver = new StsMonthsReceiver();
    private final BroadcastReceiver mStsNetConnectReceiver = new StsNetConnectReceiver();
    private TelephonyManager mTm;

    private Runnable runnable = new Runnable() {
        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r11 = this;
            r10 = 8194; // 0x2002 float:1.1482E-41 double:4.0484E-320;
            r6 = 0;
            r5 = java.lang.Boolean.valueOf(r6);
            r6 = "StsMonths";
            r7 = "run()  Runnable-->start";
            android.util.Log.d(r6, r7);	 Catch:{ Exception -> 0x00b9 }
            r4 = new com.ape.stsmonths.HttpRequester;	 Catch:{ Exception -> 0x00b9 }
            r4.<init>();	 Catch:{ Exception -> 0x00b9 }
            r6 = com.ape.stsmonths.StsMonthsService.this;	 Catch:{ Exception -> 0x00b9 }
            r6 = r6.url;	 Catch:{ Exception -> 0x00b9 }
            r2 = r4.sendGet(r6);	 Catch:{ Exception -> 0x00b9 }
            r6 = r2.getContentCollection();	 Catch:{ Exception -> 0x00b9 }
            if (r6 == 0) goto L_0x0069;
        L_0x0023:
            r6 = r2.getContentCollection();	 Catch:{ Exception -> 0x00b9 }
            r7 = 0;
            r6 = r6.get(r7);	 Catch:{ Exception -> 0x00b9 }
            if (r6 == 0) goto L_0x0069;
        L_0x002e:
            r7 = "StsMonths";
            r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00b9 }
            r6.<init>();	 Catch:{ Exception -> 0x00b9 }
            r8 = "run()   hr.getContentCollection().get(0)";
            r8 = r6.append(r8);	 Catch:{ Exception -> 0x00b9 }
            r6 = r2.getContentCollection();	 Catch:{ Exception -> 0x00b9 }
            r9 = 0;
            r6 = r6.get(r9);	 Catch:{ Exception -> 0x00b9 }
            r6 = (java.lang.String) r6;	 Catch:{ Exception -> 0x00b9 }
            r6 = r8.append(r6);	 Catch:{ Exception -> 0x00b9 }
            r6 = r6.toString();	 Catch:{ Exception -> 0x00b9 }
            android.util.Log.d(r7, r6);	 Catch:{ Exception -> 0x00b9 }
            r6 = r2.getContentCollection();	 Catch:{ Exception -> 0x00b9 }
            r7 = 0;
            r6 = r6.get(r7);	 Catch:{ Exception -> 0x00b9 }
            r6 = (java.lang.String) r6;	 Catch:{ Exception -> 0x00b9 }
            r7 = "0";
            r6 = r6.equals(r7);	 Catch:{ Exception -> 0x00b9 }
            if (r6 == 0) goto L_0x0069;
        L_0x0064:
            r6 = 1;
            r5 = java.lang.Boolean.valueOf(r6);	 Catch:{ Exception -> 0x00b9 }
        L_0x0069:
            r6 = r2.getCode();	 Catch:{ Exception -> 0x00b9 }
            r7 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
            if (r6 == r7) goto L_0x0092;
        L_0x0071:
            r6 = "StsMonths";
            r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00b9 }
            r7.<init>();	 Catch:{ Exception -> 0x00b9 }
            r8 = "run()   Runnable--->hr.getCode() =";
            r7 = r7.append(r8);	 Catch:{ Exception -> 0x00b9 }
            r8 = r2.getCode();	 Catch:{ Exception -> 0x00b9 }
            r7 = r7.append(r8);	 Catch:{ Exception -> 0x00b9 }
            r7 = r7.toString();	 Catch:{ Exception -> 0x00b9 }
            android.util.Log.d(r6, r7);	 Catch:{ Exception -> 0x00b9 }
            r6 = 0;
            r5 = java.lang.Boolean.valueOf(r6);	 Catch:{ Exception -> 0x00b9 }
        L_0x0092:
            r6 = "StsMonths";
            r7 = "run()   Runnable--->result";
            android.util.Log.d(r6, r7);
            r3 = new android.os.Message;
            r3.<init>();
            r0 = new android.os.Bundle;
            r0.<init>();
            r6 = "value";
            r7 = r5.booleanValue();
            r0.putBoolean(r6, r7);
            r3.setData(r0);
            r3.what = r10;
            r6 = com.ape.stsmonths.StsMonthsService.this;
            r6 = r6.MessageHandler;
            r6.sendMessage(r3);
        L_0x00b8:
            return;
        L_0x00b9:
            r1 = move-exception;
            r6 = "StsMonths";
            r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0102 }
            r7.<init>();	 Catch:{ all -> 0x0102 }
            r8 = "run()  Exception";
            r7 = r7.append(r8);	 Catch:{ all -> 0x0102 }
            r8 = r1.toString();	 Catch:{ all -> 0x0102 }
            r7 = r7.append(r8);	 Catch:{ all -> 0x0102 }
            r7 = r7.toString();	 Catch:{ all -> 0x0102 }
            android.util.Log.d(r6, r7);	 Catch:{ all -> 0x0102 }
            r6 = 0;
            r5 = java.lang.Boolean.valueOf(r6);	 Catch:{ all -> 0x0102 }
            r6 = "StsMonths";
            r7 = "run()   Runnable--->result";
            android.util.Log.d(r6, r7);
            r3 = new android.os.Message;
            r3.<init>();
            r0 = new android.os.Bundle;
            r0.<init>();
            r6 = "value";
            r7 = r5.booleanValue();
            r0.putBoolean(r6, r7);
            r3.setData(r0);
            r3.what = r10;
            r6 = com.ape.stsmonths.StsMonthsService.this;
            r6 = r6.MessageHandler;
            r6.sendMessage(r3);
            goto L_0x00b8;
        L_0x0102:
            r6 = move-exception;
            r7 = "StsMonths";
            r8 = "run()   Runnable--->result";
            android.util.Log.d(r7, r8);
            r3 = new android.os.Message;
            r3.<init>();
            r0 = new android.os.Bundle;
            r0.<init>();
            r7 = "value";
            r8 = r5.booleanValue();
            r0.putBoolean(r7, r8);
            r3.setData(r0);
            r3.what = r10;
            r7 = com.ape.stsmonths.StsMonthsService.this;
            r7 = r7.MessageHandler;
            r7.sendMessage(r3);
            throw r6;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.ape.stsmonths.StsMonthsService.2.run():void");
        }
    };

    private String url;

    private class StsAirplanReceiver extends BroadcastReceiver {

        private StsAirplanReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Log.d(StsMonthsService.TAG, "StsAirplanReceiver()  onReceive start  intent=" + intent.getAction());
            if (!intent.getAction().equals("android.intent.action.AIRPLANE_MODE")) {
                return;
            }

            if (intent.getBooleanExtra("state", false)) {
                Log.d(StsMonthsService.TAG, "StsAirplanReceiver()  : ACTION_AIRPLANE_MODE_CHANGED in airplane mStsMonthsReceiver=" + mStsMonthsReceiver);
                try {
                    unregisterReceiver(mStsMonthsReceiver);
                    return;
                } catch (IllegalArgumentException e) {
                    Log.e(StsMonthsService.TAG, "StsAirplanReceiver()   registerReceiverSafe(), FAIL!");
                    return;
                }
            }
            Log.d(StsMonthsService.TAG, "StsAirplanReceiver() : ACTION_AIRPLANE_MODE_CHANGED out airplane");
            registerReceiver(mStsMonthsReceiver, new IntentFilter("STS_MONTHS"));
            registerReceiver(mStsMonthsReceiver, new IntentFilter("android.intent.action.DATE_CHANGED"));
        }
    }

    private class StsMonthsReceiver extends BroadcastReceiver {

        private StsMonthsReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Log.d(StsMonthsService.TAG, "StsMonthsReceiver() onReceive start  mIsSending=" + mIsSending);
            if (intent.getAction().equals("android.intent.action.DATE_CHANGED")) {
                Log.d(StsMonthsService.TAG, "StsMonthsReceiver()  : ACTION_DATE_CHANGED");
                if (mIsFirstTimeSending || mIsSending) {
                    Log.d(StsMonthsService.TAG, "StsMonthsReceiver()  **************: mIsSending = " +
                            mIsSending + "  mIsFirstTimeSending=" + mIsFirstTimeSending);
                    return;
                }

                Calendar cl = Calendar.getInstance();
                Log.d(StsMonthsService.TAG, "StsMonthsReceiver()  : cl.getTime() = " + cl.getTime());

                int sDay = cl.get(Calendar.DAY_OF_MONTH);
                int sMonth = cl.get(Calendar.MONTH) + 1;
                int sYear = cl.get(Calendar.YEAR);
                int iDay = readLastDay();
                int iLastMonth = readLastMonth();
                int iLastYear = readLastYear();
                int iSendTimes = readSendTimes();

                if (sMonth != iLastMonth) {
                    if (sDay >= iDay) {
                        mIsSending = true;
                        writeSendingStatus(mIsSending);
                    } else if (sMonth != 2 || iDay < 28) {
                        if (iDay == 31 && sDay == 30) {
                            mIsSending = true;
                            writeSendingStatus(mIsSending);
                        }
                    } else if (sDay >= 28) {
                        mIsSending = true;
                        writeSendingStatus(mIsSending);
                    }
                }

                if (mIsSending) {
                    if (isTestSwitchON()) {
                        mSpaceTime = getSpaceTimeOnlyForTest();
                    } else {
                        mSpaceTime = StsMonthsService.DEFAULT_SPACETIME;
                    }
                    AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(ALARM_SERVICE);
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, new Intent("STS_MONTHS"), 134217728);
                    am.cancel(alarmIntent);
                    am.setRepeating(2, 0, (long) ((mSpaceTime * 60) * 1000), alarmIntent);
                }
                Log.d(StsMonthsService.TAG, "StsMonthsReceiver()  :  iYear = " + sYear + " iMonth=" + sMonth + "  iDay=" + sDay + " iLastYear=" + iLastYear + " iLastMonth=" + iLastMonth + " iSendTimes=" + iSendTimes);
            } else if (intent.getAction().equals("STS_MONTHS")) {
                long sPassedMinute = (SystemClock.elapsedRealtime() / 1000) / 60;
                if (isTestSwitchON()) {
                    mSpaceTime = getSpaceTimeOnlyForTest();
                    mStartTime = getStartTimeOnlyForTest();
                } else {
                    mSpaceTime = StsMonthsService.DEFAULT_SPACETIME;
                    mStartTime = StsMonthsService.DEFAULT_STARTTIME;
                }
                Log.d(StsMonthsService.TAG, "StsMonthsReceiver()   sPassedMinute= " + sPassedMinute + ",mCurrMinute= " + mCurrMinute + "  mSpaceTime=" + mSpaceTime + "  mStartTime=" + mStartTime);
                sPassedMinute += (long) (StsMonthsService.DEFAULT_STARTTIME - mStartTime);
                if (sPassedMinute > (mCurrMinute + ((long) mSpaceTime)) - 1) {
                    StsMonthsService stsMonthsService = StsMonthsService.this;
                    boolean z = sPassedMinute >= ((long) mStartTime) || mSendTimes > 0;
                    stsMonthsService.mIsTimeOK = z;
                    if (mStartTime % mSpaceTime == 0) {
                        mCurrMinute = sPassedMinute - (sPassedMinute % ((long) mSpaceTime));
                    } else {
                        mCurrMinute = sPassedMinute;
                    }
                    Log.d(StsMonthsService.TAG, "StsMonthsReceiver()  sPassedMinute % mSpaceTime= " + (sPassedMinute % ((long) mSpaceTime)) + "  mCurrMinute: " + mCurrMinute);
                } else {
                    mIsTimeOK = false;
                }
                if (mIsSending && mIsTimeOK) {
                    Message m = new Message();
                    m.what = StsMonthsService.ACTION_SEND_BY_NET;
                    MessageHandler.sendMessage(m);
                }
            }
        }
    }

    private class StsNetConnectReceiver extends BroadcastReceiver {

        private StsNetConnectReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Log.d(StsMonthsService.TAG, "StsNetConnectReceiver()  onReceive start  intent=" + intent.getAction());
            if (!intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                return;
            }

            if (!mIsSending || mSendTimes <= 0) {
                Log.d(StsMonthsService.TAG, "StsNetConnectReceiver()  net was connected, but mIsSending == false or mSendTimes ==0");
                return;
            }

            Log.d(StsMonthsService.TAG, "StsNetConnectReceiver()  net was connected and start to send ");
            Message m = new Message();
            m.what = StsMonthsService.ACTION_SEND_BY_NET;
            MessageHandler.sendMessage(m);
        }
    }

    public void onCreate() {
        super.onCreate();
        init();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy() unregisterReceiver");

        if (!false) {
            try {
                unregisterReceiver(mStsMonthsReceiver);
            } catch (Exception e) {
                Log.e(TAG, "onDestroy() Exception" + e.getMessage());
            }
        }

        try {
            unregisterReceiver(mStsAirplanReceiver);
        } catch (Exception e2) {
            Log.e(TAG, "onDestroy() Exception" + e2.getMessage());
        }

        try {
            unregisterReceiver(mStsNetConnectReceiver);
        } catch (Exception e22) {
            Log.e(TAG, "onDestroy() Exception" + e22.getMessage());
        }

        ((AlarmManager) mContext.getSystemService(ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(
                getApplicationContext(), 0, new Intent("STS_MONTHS"), 134217728));
    }

    private void init() {
        Log.d(TAG, "init()   start              Version :  7.0.10.07");
        mContext = getBaseContext();
        mStciSP.init(mContext);

        mCurrHour = 0;
        mCurrMinute = 0;

        pickCountryConfigs();

        mIsSending = readSendingStatus();
        mIsFirstTimeSending = false;
        mSendTimes = readSendTimes();
        mTm = TelephonyManager.getDefault();
        if (mTm == null) {
            Log.d(TAG, "init()   ********error******** TelephonyManager.getDefault() = null ********error********");
            return;
        }

        registerReceiver(mStsAirplanReceiver, new IntentFilter("android.intent.action.AIRPLANE_MODE"));
        if (!false) {
            Log.d(TAG, "init()   registerReceiver mStsMonthsReceiver");
            registerReceiver(mStsMonthsReceiver, new IntentFilter("STS_MONTHS"));
            registerReceiver(mStsMonthsReceiver, new IntentFilter("android.intent.action.DATE_CHANGED"));
            registerReceiver(mStsNetConnectReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }

        mIsFirstTimeSending = isFirstTimeSending();
        if (mIsFirstTimeSending || mIsSending) {
            startSendingAlarm(mContext);
        }
        Log.d(TAG, "init()   end    mSendTimes=" + mSendTimes + "  readLastSendTDate = " + readLastSendTDate());
    }

    private boolean isFirstTimeSending() {
        boolean bRet = false;
        int iLastMonth = readLastMonth();
        int iLastYear = readLastYear();
        int iLastDay = readLastDay();
        if (iLastYear == 0 && iLastDay == 0 && iLastMonth == 0) {
            bRet = true;
        }
        Log.d(TAG, "isFirstTimeSending()  :  iLastYear=" + iLastYear + " iLastMonth=" + iLastMonth + " iLastDay=" + iLastDay + "  mIsFirstTimeSending = " + bRet);

        return bRet;
    }

    private void startSendingAlarm(Context context) {
        Log.d(TAG, "startSendingAlarm()  : start ");
        SharedPreferences pre = getSharedPreferences(STSDATA_CONFIG, 0);
        if (pre.getBoolean(KEY_SWITCH_SENDTYPE, false)) {
            mSpaceTime = pre.getInt(KEY_SPACE_TIME, DEFAULT_SPACETIME);
        } else {
            mSpaceTime = DEFAULT_SPACETIME;
        }

        AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, new Intent("STS_MONTHS"), 134217728);
        am.cancel(alarmIntent);
        am.setRepeating(2, 0, (long) ((mSpaceTime * 60) * 1000), alarmIntent);

        mIsSending = true;
        writeSendingStatus(mIsSending);

        Log.d(TAG, "startSendingAlarm()  : end  mSpaceTime=" + mSpaceTime);
    }

    private boolean isTestSwitchON() {
        return getSharedPreferences(STSDATA_CONFIG, 0).getBoolean(KEY_SWITCH_SENDTYPE, false);
    }

    private int getSpaceTimeOnlyForTest() {
        return getSharedPreferences(STSDATA_CONFIG, 0).getInt(KEY_SPACE_TIME, DEFAULT_SPACETIME);
    }

    private int getStartTimeOnlyForTest() {
        return getSharedPreferences(STSDATA_CONFIG, 0).getInt(KEY_OPEN_TIME, DEFAULT_STARTTIME);
    }

    private void sendContentByNetwork() {
        String msg_contents = setSendContent();
        if ("".equals(msg_contents)) {
            Log.e(TAG, "sendContentByNetwork()  sendContentByNetwork--> send_sms GET msg_contents  faile");
            return;
        }

        try {
            Log.d(TAG, "sendContentByNetwork()   mSendTimes = " + mSendTimes);
            int msgid = mSendTimes;
            String encryptContents = RSAHelper.encrypt(publicKey, msg_contents);
            url = mHosturl + "&msgid=" + msgid + "&repinfo=" + encryptContents;
            Log.e(TAG, "sendContentByNetwork()   encryptContents = " + encryptContents);
            Log.e(TAG, "sendContentByNetwork()    url = " + url);

            new Thread(runnable).start();
        } catch (Exception e) {
            Log.d(TAG, "sendContentByNetwork()  **************** err****************");
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo state : info) {
                    if (state.getState() == State.CONNECTED) {
                        Log.d(TAG, "isNetworkAvailable()   return true");
                        return true;
                    }
                }
            }
        }
        Log.d(TAG, "isNetworkAvailable() return false");

        return false;
    }

    public String getIMEI() {
        String deviceId = mTm.getDeviceId(0, mContext);
        Log.d(TAG, "getIMEI()   imei=" + deviceId);
        if (deviceId == null || deviceId.isEmpty()) {
            return new String(NULL_IMEI);
        }
        return deviceId;
    }

    public String setSendContent() {
        StringBuffer smsContent = new StringBuffer();
        mStrIMEI = getIMEI();
        if (NULL_IMEI.compareTo(mStrIMEI) == 0) {
            Log.d(TAG, "init()    ********error********getIMEI() = null ***********error******");
            return "";
        }

        StringBuffer REG = new StringBuffer("TN:IMEI1,");
        REG.append(mStrIMEI);

        StringBuffer SAP_NO = new StringBuffer(",");
        SAP_NO.append(mClientNo);

        StringBuffer PRODUCT_NO = new StringBuffer(",");
        PRODUCT_NO.append(Build.MODEL);

        StringBuffer CELL_ID = new StringBuffer(",CID:");
        GsmCellLocation loc = (GsmCellLocation) mTm.getCellLocation();
        Log.d(TAG, "setSendContent()  loc= " + loc);
        int cellId = 0;
        if (loc != null) {
            cellId = loc.getCid();
        }
        if (cellId == -1) {
            cellId = 0;
        }
        CELL_ID.append(Integer.toHexString(cellId).toUpperCase());

        StringBuffer SN_NO = new StringBuffer(",");
        SN_NO.append(Build.SERIAL);

        StringBuffer SOFTWARE_NO = new StringBuffer(",");
        SOFTWARE_NO.append(SystemProperties.get("ro.custom.build.version"));
        smsContent.append(REG).append(SAP_NO).append(PRODUCT_NO).append(CELL_ID).append(SOFTWARE_NO).append(SN_NO);
        Log.d(TAG, "setSendContent() SendString=" + smsContent.toString());

        return smsContent.toString();
    }

    private void pickCountryConfigs() {
        Log.d(TAG, "pickCountryConfigs: ");
        String projectName = SystemProperties.get("ro.project", "trunk");
        Map<String, String> configMap = StsMonthsUti.readSendParamFromXml(getApplicationContext());
        if (configMap != null) {
            mClientNo = configMap.get(CONFIG_CLIENT_NO);
            mHosturl = configMap.get(CONFIG_HOST_URL);
            Log.w(TAG, " pickCountryConfigs: projectName = " + projectName + "\n   mClientNo =" + mClientNo + "\n   mHosturl =" + mHosturl);
            return;
        }
        Log.d(TAG, " pickCountryConfigs: config doesn't exist");
    }

    private int readLastYear() {
        return mStciSP.readLastYear();
    }

    private int readLastMonth() {
        return mStciSP.readLastMonth();
    }

    private int readLastDay() {
        return mStciSP.readLastDay();
    }

    private Boolean writeLastDay(int data) {
        return mStciSP.writeLastDay(data);
    }

    private Boolean writeLastYear(int data) {
        return mStciSP.writeLastYear(data);
    }

    private Boolean writeLastMonth(int data) {
        return mStciSP.writeLastMonth(data);
    }

    private int readSendTimes() {
        return mStciSP.readSendTimes();
    }

    private Boolean writeSendTimes(int data) {
        if (data < 0) {
            return mStciSP.writeSendTimes(0);
        }
        return mStciSP.writeSendTimes(data);
    }

    private String readLastSendTDate() {
        return mStciSP.readLastSendTDate();
    }

    private Boolean writeLastSendTDate(String str) {
        return mStciSP.writeLastSendTDate(str);
    }

    private Boolean readSendingStatus() {
        return mStciSP.readSendingStatus();
    }

    private Boolean writeSendingStatus(boolean bSendingStatus) {
        return mStciSP.writeSendingStatus(bSendingStatus);
    }
}
