package com.ape.stsmonths;

import android.content.Context;
import android.util.Log;

import com.ape.util.ApeConfigParser;

import java.util.HashMap;
import java.util.Map;

public class StsMonthsUti {
    private static final String CLASS_NAME = "StsMonths---->";
    private static final String CONFIG_CLIENT_NO = "client_no";
    private static final String CONFIG_HOST_URL = "host_url";
    public static final String FEATURE_FILE_PATRH = "/etc/apps/ApeStsMonths/config.xml";
    private static final String TAG = "StsMonths";
    private static Map<String, String> configMap = new HashMap();
    private static ApeConfigParser mApeConfigParser = null;

    private static void initFeatureConfigIfNull(Context context) {
        if (mApeConfigParser == null) {
            mApeConfigParser = new ApeConfigParser(context, FEATURE_FILE_PATRH);
        }
    }

    public static Map<String, String> readSendParamFromXml(Context context) {
        initFeatureConfigIfNull(context);
        if (configMap != null) {
            configMap.clear();
        }
        String client_no = mApeConfigParser.getString(CONFIG_CLIENT_NO, "");
        configMap.put(CONFIG_CLIENT_NO, client_no);
        String host_url = mApeConfigParser.getString(CONFIG_HOST_URL, "http://eservice.tinno.com/eservice/stsReport?reptype=report");
        configMap.put(CONFIG_HOST_URL, host_url);
        Log.d(TAG, "StsMonths---->readSendParamFromXml: \n client_no = " + client_no + "\n host_url = " + host_url);
        return configMap;
    }
}
