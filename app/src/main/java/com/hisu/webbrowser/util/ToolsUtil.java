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
	 * 创建文件
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
	 * hash加密
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
	 * 判断是否有网络连接
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
	 * 判断WIFI网络是否连接
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
	 * 判断MOBILE网络是否可用
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
	 * 播号码
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
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
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
	 * 对字符串进行md5加密
	 * 
	 * @param md5str
	 *            待加密的字符串
	 * @return 加密后的32位字符串
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
			File photoFile = new File(path, photoName); // 在指定路径下创建文件
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
	 * 获取文件内容
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
	 * 创建快捷方式
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
		// 不允许重复创建
		shortcutintent.putExtra("duplicate", false);
		// 需要现实的名称
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				act.getString(appnameResId));
		// 快捷图片
		Parcelable icon = Intent.ShortcutIconResource.fromContext(
				act.getApplicationContext(), iconResId);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// 点击快捷图片，运行的程序主入口
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
				new Intent(act.getApplicationContext(), act.getClass()));
		// 发送广播
		act.sendBroadcast(shortcutintent);
	}

	/**
	 * 
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */

	public static int dip2px(Context context, float dpValue) {

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dpValue * scale + 0.5f);

	}

	/**
	 * 
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */

	public static int px2dip(Context context, float pxValue) {

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (pxValue / scale + 0.5f);

	}
	
	/**
	 * 判断横竖屏
	 * @param activity
	 * @return 1：竖 | 0：横
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
}