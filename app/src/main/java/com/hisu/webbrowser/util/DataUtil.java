package com.hisu.webbrowser.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

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
	
	/*
	 * ͨ��ping�ٶ��ж������Ƿ�����
	 */
	public boolean ping(String ip) {

		String result = null;

		try {
			Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping3��

			// ��ȡping�����ݣ��ɲ��ӡ�

			InputStream input = p.getInputStream();

			BufferedReader in = new BufferedReader(new InputStreamReader(input));

			StringBuffer stringBuffer = new StringBuffer();

			String content = "";

			while ((content = in.readLine()) != null) {

				stringBuffer.append(content);

			}

			Log.e("TTT", "result content : " + stringBuffer.toString());

			// PING��״̬

			int status = p.waitFor();

			if (status == 0) {

				result = "successful~";

				return true;

			} else {

				result = "failed~ cannot reach the IP address";

			}

		} catch (IOException e) {

			result = "failed~ IOException";

		} catch (InterruptedException e) {

			result = "failed~ InterruptedException";

		} finally {

			Log.e("TTT", "result = " + result);

		}

		return false;

	}
}
