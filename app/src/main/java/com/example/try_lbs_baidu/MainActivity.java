package com.example.try_lbs_baidu;

/**
 * 尝试百度定位SDK
 * @author harvic
 * @date 2013-12-28
 */

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver; 
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {
	private String PHONE_NUMBER1="13654904623";
	private String PHONE_NUMBER2="15040351909";
	public LocationClient mLocationClient = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	public static String TAG = "msg";
	private String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
	private String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";
	/* 创建两个mServiceReceiver对象，作为类成员变量 */
	private mServiceReceiver mReceiver01, mReceiver02;
	private Context mContext;
	private Intent mIntent;
	private Button btnCountdown;
	// 广播接收者
	public BroadcastReceiver netConnect;
	public BroadcastReceiver Location;
	public BroadcastReceiver SendSMS;
	public BroadcastReceiver Success;
	public TextView text1;
	public TextView text2;
	public TextView text3;
	public TextView text4;
private String TelNumber;


	/* 双向短信识别关键字 */
	private static String SMS_INFO_EXTRA_NAME="STR_PARAM01";
	public String LocationMessage="NO";
	public String LocationMessage2="NO";
	/* 广播信息加Delimiter上返回识别TAG */
	public static String strDelimiter1="<delimiter1>";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startService();
		String strParam01;
		if ((strParam01 = getSMSInfo(SMS_INFO_EXTRA_NAME)) != null) {
			String[] aryTemp01 = null;
            /* 发现为来自广播的信息参数，表示来自自己的广播事件 */
			if (isCommandSMS(strDelimiter1, strParam01)) {
                /* 判断strDelimiter，并以数组存放 */
				aryTemp01 = strParam01.split(strDelimiter1);
                 /* 对短信关键字进行检查 */
				if ( aryTemp01.length == 2) {
                        /* 原发送短信User的电话，亦是返回短信的电话号码 */
					 TelNumber = aryTemp01[0];
					OpenVoice();
					StartLocation("13654904623");
				}
			}
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	private void startService(){
		Intent a=new Intent();
		a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		a.setClass(this,SMSListenerService.class);
		startService(a);
	}

	private void OpenVoice(){
		 AudioManager audioMa;
		audioMa = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		audioMa.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		audioMa.setStreamVolume(AudioManager.STREAM_MUSIC,audioMa.getStreamMaxVolume
				(AudioManager.STREAM_MUSIC),AudioManager.FLAG_SHOW_UI);
	}
	private void StartSetLocation() {
		mLocationClient = new LocationClient( this );
		mLocationClient.registerLocationListener( myListener );
		setLocationOption();//设定定位参数
		mLocationClient.start();//开始定位
	}

	//设置相关参数
	private void setLocationOption(){
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);//禁止启用缓存定位
		option.setPoiNumber(5);    //最多返回POI个数   
		option.setPoiDistance(1000); //poi查询距离        
		option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息        
		mLocationClient.setLocOption(option);
		
	}
	//发送短信
	private void sendSMS(String strDestAddress, String strMessage){
		android.telephony.gsm.SmsManager smsManager = android.telephony.gsm.SmsManager.getDefault();
    /* 创建自定义Action常数的Intent(给PendingIntent参数之用) */
		Intent itSend = new Intent(SMS_SEND_ACTIOIN);
		Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);
          /* sentIntent参数为传送后接受的广播信息PendingIntent */
		PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, 0);
          /* deliveryIntent参数为送达后接受的广播信息PendingIntent */
		PendingIntent mDeliverPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itDeliver, 0);
          /* 发送SMS短信，注意倒数的两个PendingIntent参数 */
		smsManager.sendTextMessage(strDestAddress, null, strMessage, mSendPI, mDeliverPI);
	}
	private void registSMSSendReveiver() {
    /* 自定义IntentFilter为SENT_SMS_ACTIOIN Receiver */
		IntentFilter mFilter01;
		mFilter01 = new IntentFilter(SMS_SEND_ACTIOIN);
		mReceiver01 = new mServiceReceiver();
		registerReceiver(mReceiver01, mFilter01);

    /* 自定义IntentFilter为DELIVERED_SMS_ACTION Receiver */
		mFilter01 = new IntentFilter(SMS_DELIVERED_ACTION);
		mReceiver02 = new mServiceReceiver();
		registerReceiver(mReceiver02, mFilter01);
	}
	public void unregisterSMSSendReceiver() {
		unregisterReceiver(mReceiver01);
		unregisterReceiver(mReceiver02);
	}
	private void StartLocation(String TelNumber) {
		mContext = this;
		Log.e(TAG, "onStart 方法调用");
		text1=(TextView)findViewById(R.id.text_text1);
		text2=(TextView)findViewById(R.id.text_text2);
		text3=(TextView)findViewById(R.id.text_text3);
		text4=(TextView)findViewById(R.id.text_text4);
		StartNetConnectCountDown(text1);
	}

	private void StartNetConnectCountDown(TextView text ) {
		setMobileNetEnable();
		netConnect = new NetConnectBroadcastRecevere("正在打开网络","网络已打开",text,this,netConnect);
		// 注册广播
		registerReceiver(netConnect, updateIntentFilter());
		//代开广播服务
		mIntent = new Intent(mContext, BroadcastCountDownService.class);
		startService(mIntent);

	}
	public void StartLocationCountDown(TextView text) {
		StartSetLocation();
		Location = new LocationRecever("正在定位","定位完成",text,this,Location);
		// 注册广播
		registerReceiver(Location, updateIntentFilter());
		//代开广播服务
		mIntent = new Intent(mContext, BroadcastCountDownService.class);
		startService(mIntent);
	}
	public void StartSendSMSCountDown(TextView text) {
		Log.i("msg","StartSendSMS:"+TelNumber+LocationMessage);
		registSMSSendReveiver();
		sendSMS(PHONE_NUMBER1,LocationMessage);
		sendSMS(PHONE_NUMBER1,LocationMessage2);
		sendSMS(PHONE_NUMBER2,LocationMessage);
		sendSMS(PHONE_NUMBER2,LocationMessage2);
		SendSMS = new SendSMSReveiver("正在发送定位短信","短信发送完成",text,this,SendSMS);
		// 注册广播
		registerReceiver(SendSMS, updateIntentFilter());
		//代开广播服务
		mIntent = new Intent(mContext, BroadcastCountDownService.class);
		startService(mIntent);
	}
	public void StartSUCCESSCountDown(TextView text) {
		setMobileNetUnable();
		Success = new SuccessReveiver("正在关闭网络","正在退出",text,this,Success);
		// 注册广播
		registerReceiver(Success, updateIntentFilter());
		//代开广播服务
		mIntent = new Intent(mContext, BroadcastCountDownService.class);
		startService(mIntent);
		mLocationClient.stop();
	}
	//提取广播传入的参数
	private String getSMSInfo(String refName){
		String strParam=null;
           /* 创建Bundle对象，判断是否有传入封装参数 */
		Bundle mBundle01 = this.getIntent().getExtras();
		if (mBundle01 != null) {
			strParam = mBundle01.getString(refName);
			return strParam;
		}else{
			return null;
		}
	}
	/* 判断接收的短信是否为有关键字的短信 */
	public static boolean isCommandSMS(String strPat, String strSMS) {
		String strPattern = "(?i)"+strPat;
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strSMS);
		return m.find();
	}
	// 注册广播
	private IntentFilter updateIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BroadcastCountDownService.IN_RUNNING);
		intentFilter.addAction(BroadcastCountDownService.END_RUNNING);
		return intentFilter;
	}
	//打开3G网络
	public final void setMobileNetEnable(){
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		Object[] arg = null;
		try {
			boolean isMobileDataEnable = invokeMethod("getMobileDataEnabled", arg);
			if(!isMobileDataEnable){
				invokeBooleanArgMethod("setMobileDataEnabled", true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//关闭3G网络
	public final void setMobileNetUnable(){
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		Object[] arg = null;
		try {
			boolean isMobileDataEnable = invokeMethod("getMobileDataEnabled", arg);
			if(isMobileDataEnable){
				invokeBooleanArgMethod("setMobileDataEnabled", false);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//判断3G网络是否
	public boolean invokeMethod(String methodName, Object[]  arg) throws Exception {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		Class ownerClass = mConnectivityManager.getClass();
		Class[]  argsClass = null;
		if (arg != null) {
			argsClass = new Class[1];
			argsClass[0] = arg.getClass();
		}
		Method method = ownerClass.getMethod(methodName, argsClass);
		Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
		return isOpen;
	}
	//判断3G网络是否打开
	public Object invokeBooleanArgMethod(String methodName, boolean value) throws Exception {

		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		Class ownerClass = mConnectivityManager.getClass();

		Class[]  argsClass = new Class[1];
		argsClass[0] = boolean.class;

		Method method = ownerClass.getMethod(methodName,argsClass);

		return method.invoke(mConnectivityManager, value);
	}

	public class MyLocationListenner implements BDLocationListener {
		@Override
		//接收位置信息
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			StringBuffer sb = new StringBuffer(256);
			StringBuffer sb2 = new StringBuffer(256);
//			sb.append("time : ");
//			sb.append(location.getTime());
//			sb.append("\nerror code : ");
		//	sb.append(location.getLocType());
			sb2.append("latitude : ");
			sb2.append(location.getLatitude());
			sb2.append("lontitude : ");
			sb2.append(location.getLongitude());
			sb2.append("radius : ");
			sb2.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("speed : ");
				sb.append(location.getSpeed());
				sb.append("satellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				/**
				 * 格式化显示地址信息
				 */
				sb.append("addr : ");
				sb.append(location.getAddrStr());
			}
//			sb.append("\nsdk version : ");
//			sb.append(mLocationClient.getVersion());
//			sb.append("\nisCellChangeFlag : ");
//			sb.append(location.isCellChangeFlag());
			LocationMessage=sb.toString();
			LocationMessage2=sb2.toString();
			Log.i(TAG, sb.toString());
			Log.i(TAG, sb2.toString());
		}
		//接收POI信息函数，我不需要POI，所以我没有做处理
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	public class mServiceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try {
        /* android.content.BroadcastReceiver.getResultCode()方法 */
				switch(getResultCode()) {
					case Activity.RESULT_OK: {
						mMakeTextToast("短信发送成功", true);
						Log.i("msg","短信发送成功");
						break;
					}
					case android.telephony.gsm.SmsManager.RESULT_ERROR_GENERIC_FAILURE: {
						mMakeTextToast("短信发送失败", true);
						Log.i("msg","短信发送失败");
						break;
					}
					case android.telephony.gsm.SmsManager.RESULT_ERROR_RADIO_OFF:
						break;
					case android.telephony.gsm.SmsManager.RESULT_ERROR_NULL_PDU:
						break;
				}
			}
			catch(Exception e) {
				e.getStackTrace();
			}
		}
	}
	public void mMakeTextToast(String str, boolean isLong) {
		if(isLong==true) {
			Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		}
	}

}