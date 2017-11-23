package com.hisu.webbrowser.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class ToolsUtil {
	private static final String TAG = "ToolsUtil";
	public static File updateDir = null;
	public static File updateFile = null;

	public static void log(String content) {
		Log.d(TAG, content);
	}
	public static void error(String content) {
		Log.e(TAG, content);
	}

	/***
	 * 鍒涘缓鏂囦欢
	 */
	public static void createFile(String name) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory()
					.toString());
			updateFile = new File(updateDir + "/" + name + ".apk");

			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/***
	 * hash鍔犲瘑
	 * 
	 * @param str
	 * @return
	 */
	public static String sha1(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}

		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };

		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
			mdTemp.update(str.getBytes());

			byte[] md = mdTemp.digest();
			int j = md.length;
			char buf[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
				buf[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(buf);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 鍒ゆ柇鏄惁鏈夌綉缁滆繛鎺�
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 鍒ゆ柇WIFI缃戠粶鏄惁杩炴帴
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo.isConnected()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 鍒ゆ柇MOBILE缃戠粶鏄惁鍙敤
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 鎾彿鐮�
	 * 
	 * @param context
	 * @param number
	 */
	public static void dialNumber(Context context, String number) {
		System.out.println(number);
		Intent myIntentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ number));
		context.startActivity(myIntentDial);
	}

	/**
	 * 杞崲鍥剧墖鎴愬渾褰�
	 * 
	 * @param bitmap
	 *            浼犲叆Bitmap瀵硅薄
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	/**
	 * 瀵瑰瓧绗︿覆杩涜md5鍔犲瘑
	 * 
	 * @param md5str
	 *            寰呭姞瀵嗙殑瀛楃涓�
	 * @return 鍔犲瘑鍚庣殑32浣嶅瓧绗︿覆
	 */
	public static String md5(String md5str) {
		try {
			MessageDigest md = MessageDigest
					.getInstance("MD5");
			byte[] array = md.digest(md5str.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}



	public static boolean checkSdCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}



	/** Save image to the SD card **/
	public static File savePhotoToSDCard(Bitmap photoBitmap, String path,
			String photoName) {
		if (checkSdCard()) {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File photoFile = new File(path, photoName); // 鍦ㄦ寚瀹氳矾寰勪笅鍒涘缓鏂囦欢
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
							fileOutputStream)) {
						fileOutputStream.flush();
						return photoFile;
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
				return null;
			} finally {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			return null;
		}
		return null;
	}

	/**
	 * 鑾峰彇鏂囦欢鍐呭
	 * 
	 * @param context
	 * @param fileName
	 * @param encode
	 * @return
	 */
	public static String getAssetFileContent(Context context, String fileName,
			String encode) {
		AssetManager am = null;
		BufferedReader buf = null;
		InputStreamReader isr = null;
		InputStream is = null;
		StringBuffer sb = new StringBuffer();
		am = context.getAssets();
		try {
			is = am.open(fileName);
			isr = new InputStreamReader(is, encode); //
			buf = new BufferedReader(isr);
			String line = null;

			while ((line = buf.readLine()) != null) {
				//log(line.toString());
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				buf.close();
				isr.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 鍒涘缓蹇嵎鏂瑰紡
	 * 
	 * @param context
	 * @param fileName
	 * @param encode
	 * @return
	 */
	public static void createShortCut(Activity act, int iconResId,
			int appnameResId) {

		// com.android.launcher.permission.INSTALL_SHORTCUT

		Intent shortcutintent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 涓嶅厑璁搁噸澶嶅垱寤�
		shortcutintent.putExtra("duplicate", false);
		// 闇�瑕佺幇瀹炵殑鍚嶇О
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				act.getString(appnameResId));
		// 蹇嵎鍥剧墖
		Parcelable icon = Intent.ShortcutIconResource.fromContext(
				act.getApplicationContext(), iconResId);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// 鐐瑰嚮蹇嵎鍥剧墖锛岃繍琛岀殑绋嬪簭涓诲叆鍙�
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
				new Intent(act.getApplicationContext(), act.getClass()));
		// 鍙戦�佸箍鎾�
		act.sendBroadcast(shortcutintent);
	}

	/**
	 * 
	 * 鏍规嵁鎵嬫満鐨勫垎杈ㄧ巼浠� dp 鐨勫崟浣� 杞垚涓� px(鍍忕礌)
	 */

	public static int dip2px(Context context, float dpValue) {

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dpValue * scale + 0.5f);

	}

	/**
	 * 
	 * 鏍规嵁鎵嬫満鐨勫垎杈ㄧ巼浠� px(鍍忕礌) 鐨勫崟浣� 杞垚涓� dp
	 */

	public static int px2dip(Context context, float pxValue) {

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (pxValue / scale + 0.5f);

	}
	
	/**
	 * 鍒ゆ柇妯珫灞�
	 * @param activity
	 * @return 1锛氱珫 | 0锛氭í
	 */
	public static int ScreenOrient(Activity activity)
	{
		int orient = activity.getRequestedOrientation(); 
		if(orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			WindowManager windowManager = activity.getWindowManager();  
			Display display = windowManager.getDefaultDisplay();  
			int screenWidth  = display.getWidth();  
			int screenHeight = display.getHeight();  
			orient = screenWidth < screenHeight ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		}
		return orient;
	}
	
	public static String getLocalIpAddress() {
		try {
			String ipv4;
			List<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface ni : nilist) {
				List<InetAddress> ialist = Collections.list(ni.getInetAddresses());
				for (InetAddress address : ialist) {
					if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = address.getHostAddress())) {
						return ipv4;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, ex.toString());
		}
		return null;
	}

	public static String getLocalMacAddressFromIp(Context context) {
		String mac_s = "";
		try {
			byte[] mac;
			NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
			mac = ne.getHardwareAddress();
			if(mac!=null){
				mac_s = formatMac(byte2hex(mac));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mac_s;
//		return "a8-bd-3a-55-00-1d";
	}

	public static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer(b.length);
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1) {
				hs = hs.append("0").append(stmp);
			} else {
				hs = hs.append(stmp);
			}
		}
		return String.valueOf(hs);
	}
	public static String formatMac(String mac){
		return formatMac(mac,2,"-");
	}
	public static String formatMac(String mac,int num,String split){
		if(TextUtils.isEmpty(split)){
			split = ":";
		}
		int len = mac.length();
		StringBuffer sb = new StringBuffer();
		
		for(int i = 0; i < len; i++){
			sb.append(mac.charAt(i));
			if(i%num==1 && i!=len-1){
				sb.append(split);
			}
		}
		return sb.toString();
	}
	/*
	 * MAC 有线
	 * */
	public static String getLocalEthernetMacAddress() {  
        String mac=null;  
        try {  
            Enumeration localEnumeration=NetworkInterface.getNetworkInterfaces();  
  
            while (localEnumeration.hasMoreElements()) {  
                NetworkInterface localNetworkInterface=(NetworkInterface) localEnumeration.nextElement();  
                String interfaceName=localNetworkInterface.getDisplayName();  
  
                if (interfaceName==null) {  
                    continue;  
                }  
  
                if (interfaceName.equals("eth0")) {  
                    mac=convertToMac(localNetworkInterface.getHardwareAddress());  
                    if (mac!=null&&mac.startsWith("0:")) {  
                        mac="0"+mac;  
                    }  
                    break;  
                }  
  
            }  
        } catch (SocketException e) {  
            e.printStackTrace();  
        }  
        return mac;  
    }  
  
    private static String convertToMac(byte[] mac) {  
        StringBuilder sb=new StringBuilder();  
        for (int i=0; i<mac.length; i++) {  
            byte b=mac[i];  
            int value=0;  
            if (b>=0&&b<=16) {  
                value=b;  
                sb.append("0"+Integer.toHexString(value));  
            } else if (b>16) {  
                value=b;  
                sb.append(Integer.toHexString(value));  
            } else {  
                value=256+b;  
                sb.append(Integer.toHexString(value));  
            }  
            if (i!=mac.length-1) {  
                sb.append(":");  
            }  
        }  
        return sb.toString();  
    }  
    /*
     * MAC 无线
     * */
    public static String getWifiMacAddr(Context context, String macAddr) {  
        WifiManager wifi=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
        WifiInfo info=wifi.getConnectionInfo();  
        if (null!=info) {  
            String addr=info.getMacAddress();  
            if (null!=addr) {  
                macAddr=addr;  
            }  
        }  
        return macAddr;  
    }  
}