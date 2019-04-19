package com.hisu.webbrowser.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hisu.webbrowser.MainActivity;
import com.hisu.webbrowser.mode.Constants;
import com.hisu.webbrowser.util.DataUtil;
import com.hisu.webbrowser.util.ToolsUtil;

public class NetworkConnectChangedReceiver extends BroadcastReceiver {

    DataUtil dataUtil;
    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            //获取联网状态的NetworkInfo对象
            if (activeNetwork != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == activeNetwork.getState() && activeNetwork.isAvailable()) {
                    if(ToolsUtil.isNetworkConnected(context)){
                        dataUtil = new DataUtil(context);
                        String first_time = dataUtil.getsharepreferences("LIVE_TIME");
                        int start_time = 3 * 6 * 10 *1000;
                        if(first_time!=null){
                            // 有网络时的时间小于3分钟就进入酒店APK
                            if(System.currentTimeMillis() - Long.parseLong(first_time) < start_time){
                                Intent intent_n = new Intent(context, MainActivity.class);
                                intent_n.setAction("android.intent.action.MAIN");
                                intent_n.addCategory("android.intent.category.LAUNCHER");
                                intent_n.addCategory("android.intent.category.HOME");
                                intent_n.addCategory("android.intent.category.DEFAULT");
                                intent_n.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent_n);
                                Constants.isHaveNet = true;
                            }
                        }

                    }
                } else {
                    Constants.isHaveNet = false;
                }
            }
        }
    }
}
