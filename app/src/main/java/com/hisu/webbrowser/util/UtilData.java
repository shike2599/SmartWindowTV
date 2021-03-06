package com.hisu.webbrowser.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.text.format.Time;
import android.util.Log;

public class UtilData {

	public static String surl;

	/*
	 * 首页LOGO
	 */
	public static String Logo1 = "";
	public static String Logo2 = "";

	/*
	 * 绑定酒店的ID
	 */
	public static String IP = "";
	public static String hotelId = "";

	/*
	 * 模版 子项 选择 手机端为PHONE，win8 为 WINDOWS
	 */
	public static String UImodel = "WINDOWS";

	// 从零开始 现有 45678 5中可选
	public static String UI_model_item = "0,1,2,3,4,5,6,7";

	/*
	 * 通过ping百度判断网络是否正常
	 * */
	public static final boolean ping() {

		String result = null;

		try {

			String ip = "www.baidu.com";// 除非百度挂了，否则用这个应该没问题~

			Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping3次

			// 读取ping的内容，可不加。

			InputStream input = p.getInputStream();

			BufferedReader in = new BufferedReader(new InputStreamReader(input));

			StringBuffer stringBuffer = new StringBuffer();

			String content = "";

			while ((content = in.readLine()) != null) {

				stringBuffer.append(content);

			}

			Log.i("TTT", "result content : " + stringBuffer.toString());

			// PING的状态

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

			Log.i("TTT", "result = " + result);

		}

		return false;

	}

	/**
	 * 获取当前时间
	 *
	 * @return 时间字符串 24小时制
	 * @author drowtram
	 */
	public static String getStringTime(String type) {
		Time t = new Time();
		t.setToNow(); // 取得系统时间。
		String hour = t.hour < 10 ? "0" + (t.hour) : t.hour + ""; // 默认24小时制
		String minute = t.minute < 10 ? "0" + (t.minute) : t.minute + "";
		return hour + type + minute;
	}

	/**
	 * 获取当前日期，包含星期几
	 *
	 * @return 日期字符串 xx月xx号 星期x
	 * @author drowtram
	 */
	public static String getStringData() {
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "日";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}
		return mMonth + "月" + mDay + "日\n" + "星期" + mWay;
	}

	public static String getIpUrl = "http://www.cz88.net/ip/viewip778.aspx";

	public static void getWebIp() {
		new Thread() {
			public void run() {
				String strForeignIP = "";
				try {
					URL url = new URL(getIpUrl);

					BufferedReader br = new BufferedReader(
							new InputStreamReader(url.openStream()));

					String s = "";
					StringBuffer sb = new StringBuffer("");
					while ((s = br.readLine()) != null) {
						sb.append(s + "\r\n");
					}
					br.close();

					String webContent = "";
					webContent = sb.toString();
					String flagofForeignIPString = "IPMessage";
					int startIP = webContent.indexOf(flagofForeignIPString)
							+ flagofForeignIPString.length() + 2;
					int endIP = webContent.indexOf("</span>", startIP);
					strForeignIP = webContent.substring(startIP, endIP);

				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();

	}

	public static final String sGetAddrUrl = "http://ip-api.com/json/";

	public static void locateCityName(final String foreignIPString) {
		new Thread() {
			public void run() {
				try {
					HttpClient httpClient = new DefaultHttpClient();
					String requestStr = sGetAddrUrl + foreignIPString;
					HttpGet request = new HttpGet(requestStr);
					HttpResponse response = httpClient.execute(request);
					if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
						String cityName = EntityUtils.toString(response
								.getEntity());
						Log.e("cityName", cityName);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}
}
