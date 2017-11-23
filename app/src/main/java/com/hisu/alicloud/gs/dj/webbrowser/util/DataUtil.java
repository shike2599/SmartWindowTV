package com.hisu.alicloud.gs.dj.webbrowser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataUtil {

	SharedPreferences share;
	Context content;
	
	public DataUtil(Context content){
		this.content = content;
		if(share == null){
			share = content.getSharedPreferences("HISU", 0);
		}
	}
	
	public void setsharepreferences(String key,String value){
		Editor editor = share.edit();
		editor.putString(key, value);
		editor.commit();
	}
	public String getsharepreferences(String key){
		return share.getString(key, null);
	}
}
