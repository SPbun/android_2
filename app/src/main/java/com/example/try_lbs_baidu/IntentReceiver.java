package com.example.try_lbs_baidu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/* 锟皆讹拷锟斤拷坛锟斤拷锟紹roadcastReceiver锟斤拷
 * 锟斤拷锟斤拷锟皆讹拷锟斤拷系统锟斤拷锟斤拷悴ワ拷锟斤拷锟较?*/
public class IntentReceiver extends BroadcastReceiver
{
  /* 锟皆讹拷锟斤拷锟斤拷锟斤拷为Intent Filter锟斤拷ACTION锟斤拷息 */
  public static final String HIPPO_SERVICE_IDENTIFIER =
          "HIPPO_ON_SERVICE_001";

  @Override
  public void onReceive(Context context, Intent intent) 
  {
    // TODO Auto-generated method stub
    if(intent.getAction().toString().equals(HIPPO_SERVICE_IDENTIFIER))
    {
      /* 获取参数 */
      Bundle mBundle01 = intent.getExtras();
      String strParam1="";
      if (mBundle01 != null)
      {
        strParam1 = mBundle01.getString("STR_PARAM01");
      }

      //启动Activity，并添加参数
      if(strParam1!="")
      {
        Intent mRunPackageIntent = new Intent(context,MainActivity.class);
        mRunPackageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mRunPackageIntent.putExtra("STR_PARAM01", strParam1);
        context.startActivity(mRunPackageIntent);
      }


    }
  }
}
