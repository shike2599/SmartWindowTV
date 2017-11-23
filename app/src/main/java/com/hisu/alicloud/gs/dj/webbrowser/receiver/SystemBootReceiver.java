package com.hisu.alicloud.gs.dj.webbrowser.receiver;

import java.util.List;



import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.hisu.alicloud.gs.dj.webbrowser.MainActivity;

public class SystemBootReceiver extends BroadcastReceiver {
	/* Ҫ���յ�intentԴ */
	private final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		Log.d("SystemBootReceiver", action);
		
		if (ACTION_BOOT_COMPLETED.equals(action)) {
			if (!isActivityRunning(context, "com.webbrowser")) {
				Log.d("SystemBootReceiver","SystemBootReceiver Activity has started!");
				context.startActivity(new Intent(context, MainActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
				Toast.makeText(context, "请稍候......", 3000).show();
			}
		}
	}
	/**
	 * 用来判断服务是否运行.
	 * 
	 * @param context
	 * @param className
	 *            判断的服务名字
	 * @return true true 在运行 false 不在运行
	 */
	public boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processesList = activityManager.getRunningAppProcesses();
		if (processesList==null || processesList.size() < 1 ) {
			return false;
		}
		for (int i = 0; i < processesList.size(); i++) {
			Log.d("isServiceRunning", processesList.get(i).processName);
			if (processesList.get(i).processName.equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
	
	public boolean isActivityRunning(Context mContext, String packageName){
		ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		for (RunningTaskInfo info : list) {
			Log.d("isServiceRunning", info.topActivity.getPackageName());
			Log.d("baseActivity", info.baseActivity.getPackageName());
		    if (info.topActivity.getPackageName().equals(packageName) || info.baseActivity.getPackageName().equals(packageName)) {
		        isAppRunning = true;
		        Log.i("isActivityRunning",info.topActivity.getPackageName() + " info.baseActivity.getPackageName()="+info.baseActivity.getPackageName());
		        break;
		    }
		}
		return isAppRunning;
	}
}
