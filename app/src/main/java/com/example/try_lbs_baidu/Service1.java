package com.example.try_lbs_baidu;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder; 
import android.telephony.gsm.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* 自定义系统服务，当聆听到信息事件时，则广播自定义信息 */
public class Service1 extends Service
{
  /* Handler对象 */
  private Handler objHandler = new Handler();
  private int intCounter=0;
  /* 自定义广播识别ACTIOIN常数 */
  public static final String StartActivityBroadcast = "HIPPO_ON_SERVICE_001";
  /* 系统接收短信的广播ACTION常数 */
  private static final String SMS_Coming_Broadcast = "android.provider.Telephony.SMS_RECEIVED";
  /* 自定义短信mSMSReceiver对象 */
  private mSMSReceiver SMSReceiverObj;

  @Override
  public void onCreate() {
    /* 向系统注册receiver，聆听系统短信广播事件 */
    registMessageBroadcastReceiver();
    super.onCreate();
  }

  private void registMessageBroadcastReceiver() {
    IntentFilter SMSFilter;
    SMSFilter = new IntentFilter(SMS_Coming_Broadcast);
    SMSReceiverObj = new mSMSReceiver();
    registerReceiver(SMSReceiverObj, SMSFilter);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onDestroy() {
    //取消注册短信广播监听器
    unregisterReceiver(SMSReceiverObj);
    super.onDestroy();
  }
  
  /* 当收到系统短信广播事件后的事件处理 */
  public class mSMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      SmsMessage[] messages;
      /* 判断是否为系统广播的短信ACTION */
      if (intent.getAction().equals(SMS_Coming_Broadcast)) {
        //判断是否收到短息信息参数
        if ((messages = GetMessageInfo(intent)) != null) {
          /* 向系统广播自定义信息 */
          sendBroadcastInformatin(StartActivityBroadcast, "STR_PARAM01", buildSMSBroadcastString(messages));
        }
      }
    }

    private SmsMessage[] GetMessageInfo(Intent intent) {
      Bundle bundle = intent.getExtras();
      if (bundle != null) {
      /* 拆解与识别SMS短信 */
        Object[] myOBJpdus = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[myOBJpdus.length];
        for (int i = 0; i < myOBJpdus.length; i++) {
          messages[i] = SmsMessage.createFromPdu((byte[]) myOBJpdus[i]);
        }
        return messages;
      } else {
        return null;
      }
    }


    /* 拼接短信信息广播字符串 */
    private void sendBroadcastInformatin(String broadcasttag, String extraname, String extravalue) {
      Intent i = new Intent(broadcasttag);
      i.putExtra(extraname, extravalue);
          /* 以sendBroadcast送出广播信息 */
      sendBroadcast(i);
    }

    private String buildSMSBroadcastString(SmsMessage[] messages) {
      StringBuilder sb = new StringBuilder();
      for (SmsMessage currentMessage : messages) {
        sb.append(currentMessage.getDisplayOriginatingAddress());
      /* 在电话与SMS短信BODY之间，加上分隔TAG */
        sb.append(MainActivity.strDelimiter1);
        sb.append(currentMessage.getDisplayMessageBody());
      }
      return sb.toString();
    }

    boolean checkMessageKeyword(String Keyword, String msg) {
      String strPattern = "(?i)" + Keyword;
      Pattern p = Pattern.compile(strPattern);
      Matcher m = p.matcher(msg);
      return m.find();
    }
  }
}
