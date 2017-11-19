package com.ape.stsmonths;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.PhoneConstants;
import com.wrapper.stk.HideMethod.TelephonyManager;

public class StsMonthsActivity extends Activity {
    public static String TAG = "StsMonths";
    public Editor ed;
    Context mContext;
    private CheckBox mSwitchWhole;
    public EditText opentime;
    public SharedPreferences pre;
    public EditText spacetime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.mContext = getBaseContext();
        Log.d(TAG, "onCreate  SaleTrackerActivity");
        this.opentime = (EditText) findViewById(R.id.editopentime);
        this.spacetime = (EditText) findViewById(R.id.spacetime);
        this.pre = getSharedPreferences(StsMonthsService.STSDATA_CONFIG, 0);
        this.ed = this.pre.edit();
        this.opentime.setText("" + this.pre.getInt(StsMonthsService.KEY_OPEN_TIME, 180));
        this.spacetime.setText("" + this.pre.getInt(StsMonthsService.KEY_SPACE_TIME, 60));
        this.mSwitchWhole = (CheckBox) findViewById(R.id.switchSendType);
        this.mSwitchWhole.setChecked(this.pre.getBoolean(StsMonthsService.KEY_SWITCH_SENDTYPE, false));
        this.mSwitchWhole.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                StsMonthsActivity.this.ed.putBoolean(StsMonthsService.KEY_SWITCH_SENDTYPE, StsMonthsActivity.this.mSwitchWhole.isChecked());
                StsMonthsActivity.this.ed.commit();
                StsMonthsActivity.this.getStatus();
            }
        });
        ((TextView) findViewById(R.id.tvShowVersion)).setText(StsMonthsService.mVersion);
        ((Button) findViewById(R.id.btnSave)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PhoneConstants.CFU_QUERY_TYPE_DEF_VALUE.equals(StsMonthsActivity.this.opentime.getText().toString()) || PhoneConstants.CFU_QUERY_TYPE_DEF_VALUE.equals(StsMonthsActivity.this.spacetime.getText().toString()) || "".equals(StsMonthsActivity.this.opentime.getText().toString()) || "".equals(StsMonthsActivity.this.spacetime.getText().toString())) {
                    StsMonthsActivity.this.showToast(StsMonthsActivity.this.getResources().getString(R.string.sts_invalid_value));
                    return;
                }
                StsMonthsActivity.this.ed.putInt(StsMonthsService.KEY_SPACE_TIME, Integer.parseInt(StsMonthsActivity.this.spacetime.getText().toString(), 10));
                StsMonthsActivity.this.ed.putInt(StsMonthsService.KEY_OPEN_TIME, Integer.parseInt(StsMonthsActivity.this.opentime.getText().toString(), 10));
                StsMonthsActivity.this.ed.commit();
                StsMonthsActivity.this.showToast("Save successful");
            }
        });
        ((Button) findViewById(R.id.btnclear)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(StsMonthsActivity.TAG, "setOnClickListener");
                StsMonthsConfigSP stciSP = new StsMonthsConfigSP();
                stciSP.init(StsMonthsActivity.this.mContext);
                stciSP.writeLastDay(0);
                stciSP.writeLastMonth(0);
                stciSP.writeLastYear(0);
                stciSP.writeSendTimes(0);
                stciSP.writeLastSendTDate("");
                StsMonthsActivity.this.getStatus();
                StsMonthsActivity.this.showToast("Clear successful");
            }
        });
    }

    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, 0).show();
    }

    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    protected void onResume() {
        getStatus();
        Log.d(TAG, "onResume");
        super.onResume();
        this.opentime.setText("" + this.pre.getInt(StsMonthsService.KEY_OPEN_TIME, 180));
        this.spacetime.setText("" + this.pre.getInt(StsMonthsService.KEY_SPACE_TIME, 60));
    }

    private void getStatus() {
        StsMonthsConfigSP stciSP = new StsMonthsConfigSP();
        stciSP.init(this.mContext);
        TextView showLastSendTime = (TextView) findViewById(R.id.tvShowSendResult);
        TextView sendTimes = (TextView) findViewById(R.id.tvShowSendType);
        ((TextView) findViewById(R.id.tvShowOpenFile)).setText("\n IMEI1 :  " + TelephonyManager.getDefault().getDeviceId(0, this.mContext) + "\n");
        sendTimes.setText("发送总次数: " + stciSP.readSendTimes() + "\n" + "\n" + "最后一次成功发送时间:  " + stciSP.readLastYear() + "年" + stciSP.readLastMonth() + "月" + stciSP.readLastDay() + "日\n");
        showLastSendTime.setText("最后发送时间:  " + stciSP.readLastSendTDate() + "\n");
    }
}
