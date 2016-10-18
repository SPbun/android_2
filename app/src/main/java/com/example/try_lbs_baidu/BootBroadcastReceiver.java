package com.example.try_lbs_baidu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ZZQ on 2016/9/1.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String action_boot="android.intent.action.BOOT_COMPLETED";
 
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(action_boot)){
            startMessagListeningeService(context);
        }
    }

    private void startMessagListeningeService(Context context) {
        Intent a=new Intent();
        a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        a.setClass(context,SMSListenerService.class);
        context.startService(a);
    }
}
