package com.hisu.alicloud.gs.dj.webbrowser.receiver;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.hisu.alicloud.gs.dj.webbrowser.MainActivity;
import com.hisu.alicloud.gs.dj.webbrowser.util.BrowserUtil;
import com.hisu.alicloud.gs.dj.webbrowser.util.DataUtil;

public class AutoStartBroadcast extends BroadcastReceiver {

	private static final String TAG = "BootComplete";
	private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
	private DataUtil util;
	public void onReceive(Context context, Intent intent) {
		initu(intent,context);
		// ָ��Ҫ������Activity��Service
		Intent auto_activity = new Intent(context, MainActivity.class);
		 auto_activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
		context.startActivity(auto_activity);
	}

	/*
	 * U�� ·��  MEDIA_MOUNTED
	 * */
	
	private void initu( Intent intent,Context context){
		if (intent.getAction().equals("Android.intent.action.MEDIA_EJECT")
				|| intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTED")) {
				BrowserUtil.U_PATH = "";
				}else if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")) {
				String path = intent.getDataString();
				String pathString = path.split("file://")[1];
				util = new DataUtil(context);
				// �洢U��·��
				if(util.getsharepreferences("u_path") == null)
				new DataUtil(context).setsharepreferences("u_path", pathString);
				Log.e("path", pathString);
				}
	}
}
