package com.example.try_lbs_baidu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView; 

/**
 * Created by ZZQ on 2016/9/2.
 */
public class SendSMSReveiver extends BroadcastReceiver {
    String doingMsg;
    String endMsg;
    TextView text;
    MainActivity m;
    BroadcastReceiver b;
    public  SendSMSReveiver (String doingmsg, String endmsg, TextView text, MainActivity m, BroadcastReceiver b){
        super();
        this.doingMsg=doingmsg;
        this.endMsg=endmsg;
        this.text=text;
        this.m=m;
    this.b=b;}
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(BroadcastCountDownService.IN_RUNNING)) {

            // 正在倒计时
            text.setText(doingMsg + "(" + intent.getStringExtra("time") + ")");
            Log.i("msg", doingMsg + "倒计时中(" + intent.getStringExtra("time") + ")");
        }else if(action.equals(BroadcastCountDownService.END_RUNNING)) {

            // 完成倒计时
            text.setText(endMsg);
            m.unregisterReceiver(this);
            m.unregisterSMSSendReceiver();
            m.StartSUCCESSCountDown(m.text4);

        }
    }
}
