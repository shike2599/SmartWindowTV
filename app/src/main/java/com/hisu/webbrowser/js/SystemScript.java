  package com.hisu.webbrowser.js;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.hisu.webbrowser.MainActivity;
import com.hisu.webbrowser.activity.UpathActivity;
import com.hisu.webbrowser.activity.VideoVLCActivity;
import com.hisu.webbrowser.activity.VideoViewActivity;
import com.hisu.webbrowser.manager.UpdateManager;
import com.hisu.webbrowser.mode.Constants;
import com.hisu.webbrowser.util.BrowserMessage;
import com.hisu.webbrowser.util.BrowserUtil;
import com.hisu.webbrowser.util.CommonFunction;
import com.hisu.webbrowser.util.DataUtil;
import com.hisu.webbrowser.util.ToolsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

  public class SystemScript {
	private final String TAG = "SystemScript";
	private final String MEMEORY_STORE_KEY = "chinahisu_memory_store";
	private final String LOCAL_STORE_KEY = "chinahisu_local_store";
	private Handler mHandler;
	private Context mContext;
	private Activity ac;
	private static SharedPreferences sSp;
	private List<Integer> preventDefaultKeyList = new ArrayList<Integer>();
	public static int LOAD_TYPE = -1;

	  public SystemScript(Handler handler, Context context,Activity ac) {
		// TODO Auto-generated constructor stub
		mHandler = handler;
		mContext = context;
		this.ac = ac;
	}
	public SystemScript(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	@JavascriptInterface
	private SharedPreferences getSharePreferences(Context context){
		if(sSp==null){
			sSp = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return sSp;
	}
	@JavascriptInterface
	public void setGlobalVar(String key, String value) {
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called.");
		SharedPreferences sp = getSharePreferences(mContext);
		String localStr = sp.getString(MEMEORY_STORE_KEY, "{}");
		try {
			JSONObject localJson = new JSONObject(localStr);
			localJson.put(key, value);
			sp.edit().putString(MEMEORY_STORE_KEY, localJson.toString()).commit();
			Log.d(TAG,  CommonFunction._FUNC_()+" key= " + key + " value ="+value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@JavascriptInterface
	public String getGlobalVar(String key) {
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called.");
		String ret = "";
		String localStr = getSharePreferences(mContext).getString(MEMEORY_STORE_KEY, "{}");
		try {
			JSONObject localJson = new JSONObject(localStr);
			if(localJson.has(key)){
				ret = localJson.getString(key);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, CommonFunction._FUNC_()+" key= " + key + " value =" + ret);
		return ret;
	}
	@JavascriptInterface
	public void removeGlobalVar(String key) {
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called. key=="+key);
		SharedPreferences sp = getSharePreferences(mContext);
		String localStr = sp.getString(MEMEORY_STORE_KEY, "{}");
		try {
			JSONObject localJson = new JSONObject(localStr);
			if(localJson.has(key)){
				localJson.remove(key);
				sp.edit().putString(MEMEORY_STORE_KEY, localJson.toString()).commit();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@JavascriptInterface
	public void clearGlobalVar() {
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called");
		getSharePreferences(mContext).edit().remove(MEMEORY_STORE_KEY).commit();
	}
	@JavascriptInterface
	public void setLocalVar(String key, String value) {
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called.");
		SharedPreferences sp = getSharePreferences(mContext);
		String localStr = sp.getString(LOCAL_STORE_KEY, "{}");
		try {
			JSONObject localJson = new JSONObject(localStr);
			localJson.put(key, value);
			sp.edit().putString(LOCAL_STORE_KEY, localJson.toString()).commit();
			Log.d(TAG,  CommonFunction._FUNC_()+" key= " + key + " value ="+value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	@JavascriptInterface
	public String getLocalVar(String key) {
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called.");
		String ret = "";
		String localStr = getSharePreferences(mContext).getString(LOCAL_STORE_KEY, "{}");
		try {
			JSONObject localJson = new JSONObject(localStr);
			if(localJson.has(key)){
				ret = localJson.getString(key);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG,  CommonFunction._FUNC_()+" key= " + key + " value =" + ret);
		return ret;
	}
	@JavascriptInterface
	public void removeLocalVar(String key) {
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called.key==="+key);
		SharedPreferences sp = getSharePreferences(mContext);
		String localStr = sp.getString(LOCAL_STORE_KEY, "{}");
		try {
			JSONObject localJson = new JSONObject(localStr);
			if(localJson.has(key)){
				localJson.remove(key);
				sp.edit().putString(LOCAL_STORE_KEY, localJson.toString()).commit();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@JavascriptInterface
	public void clearLocalVar() {
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called");
		getSharePreferences(mContext).edit().remove(LOCAL_STORE_KEY).commit();
	}
	public void log(String msg) {
		Log.d(TAG, msg);
	}

	public int exit() {
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called.");
		if (null == mHandler) {
			return -1;
		}
		Message msg = new Message();
		msg.what = BrowserMessage.SW_CMD_EXIT;
		mHandler.sendMessage(msg);
		return 0;
	}
	@JavascriptInterface
	public int loadPage(String url) {
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called. url = " + url);
		if (null == mHandler) {
			return -1;
		}
		Message msg = new Message();
		msg.what = BrowserMessage.SW_OPEN_URL;
		msg.getData().putString("URL", url);
		mHandler.sendMessage(msg);
		return 0;
	}
	@JavascriptInterface
	public int toast(String message) {
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called. message = " + message);
		if (null == mHandler) {
			return -1;
		}
		Message msg = new Message();
		msg.what = BrowserMessage.SW_OPEN_URL;
		msg.getData().putString("message", message);
		mHandler.sendMessage(msg);
		return 0;
	}
	@JavascriptInterface
	public void exitApp() {
		Log.i(TAG,"=====exitApp====" );
		exit();
//		StringBuffer sb = new StringBuffer();
//		sb.append("确定要退出" + mContext.getString(R.string.app_name) + "?");
//		Dialog dialog = new AlertDialog.Builder(mContext).setTitle("友情提示").setMessage(sb.toString())
//				.setPositiveButton("确   定", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						exit();
//					}
//				}).setNegativeButton("取    消", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//
//						dialog.dismiss();
//					}
//				}).create();
//		dialog.show();
	}

	  /*
       * home键
       * */
	  @JavascriptInterface
	  public void backHome(){
		  MainActivity main = (MainActivity) ac;
		  main.BrowserDestroy();
		  android.os.Process.killProcess(android.os.Process.myPid());

	  }
	  /*
       * back键
       * */
	  @JavascriptInterface
	  public void Keyback(){
		  MainActivity main = (MainActivity) ac;
		  main.finish();
	  }
	  /*
       * back键
       * */
	  @JavascriptInterface
	  public int ApkType(){
		  Log.i(TAG, "load_type====>>>" + LOAD_TYPE);
		  return LOAD_TYPE;
	  }

	  /*
       * http 视频播放
       * */
	  @JavascriptInterface
	  public void StartVideo(String path){
		  Intent intent = new Intent(ac,VideoViewActivity.class);
		  intent.putExtra("path", path);
		  ac.startActivity(intent);
	  }

	  /*
       * 直播
       * */
	  @JavascriptInterface
	  public void openVLC(String path){
		  if(path == null || "".equals(path)){
			  Toast.makeText(ac, "视频地址错误", Toast.LENGTH_SHORT).show();;
			  return;
		  }
		  Intent intent = new Intent();
		  intent.setClass(ac, VideoVLCActivity.class);
//		intent.putExtra("path", "rtsp://61.185.20.133:554/pag://10.63.32.53:7302:00000000001310010176:0:SUB:TCP?cnid=100001&pnid=1&auth=0");
		  intent.putExtra("path",path);
		  Log.e("直播视频地址", path);
		  ac.startActivity(intent);
	  }
	@JavascriptInterface
	public void openVLC(String path,int time){
		if(path == null || "".equals(path)){
			Toast.makeText(ac, "视频地址错误", Toast.LENGTH_SHORT).show();;
			return;
		}
		Intent intent = new Intent();
		intent.setClass(ac, VideoVLCActivity.class);
//		intent.putExtra("path", "rtsp://61.185.20.133:554/pag://10.63.32.53:7302:00000000001310010176:0:SUB:TCP?cnid=100001&pnid=1&auth=0");
		intent.putExtra("path",path);
		intent.putExtra("videotime",time);
		Log.e("直播视频地址", path);
		ac.startActivity(intent);
	}
	
	@JavascriptInterface
	public int openApp(String packageName, String activityName, String arguments) {
		Log.d(TAG, " " + CommonFunction._FUNC_());
//		if (null == mHandler) {
//			return -1;
//		}
		if(packageName == null)
		{
			Log.e(TAG,"SW_OPEN_APP packageName error");
			return -1;
		}
		Log.d(TAG,"SW_OPEN_APP packageName =="+packageName);
		Log.d(TAG,"SW_OPEN_APP activityName =="+activityName);
		Log.d(TAG,"SW_OPEN_APP arguments =="+arguments);
		
		Intent intent = null;
		if(TextUtils.isEmpty(activityName)){
			intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
			
		}else{
			ComponentName localComponentName = new ComponentName(
					packageName, activityName);
			intent = new Intent();
            
			intent.setComponent(localComponentName);
		}
		if(intent !=null){
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 
			if(!TextUtils.isEmpty(arguments)){
				try {
					JSONArray paramsArr = new JSONArray(arguments);
				
	            	for(int i=0;i<paramsArr.length();i++){
	            		JSONObject arg = paramsArr.getJSONObject(i);
	            		Log.d(TAG,"args["+i+"]==="+arg.toString());
	            		if(!arg.has("key") || !arg.has("value")){
	            			Toast.makeText(mContext, "参数错误，请检查", Toast.LENGTH_LONG).show();
	            			continue;
	            		}
	            		if(!arg.has("type")){
	            			intent.putExtra(arg.getString("key"), arg.getString("value"));
	            		}else{
	            			String key = arg.getString("key"),
	            					type = arg.getString("type").toLowerCase();
	            			if("int".equals(type)){
		            			intent.putExtra(key, arg.getInt("value"));
	            			}else if("long".equals(type)){
		            			intent.putExtra(key, arg.getLong("value"));
	            			}else if("double".equals(type)){
		            			intent.putExtra(key, arg.getDouble("value"));
	            			}else if("boolean".equals(type)){
		            			intent.putExtra(key, arg.getBoolean("value"));
	            			}else{
		            			intent.putExtra(key, arg.getString("value"));
	            			}
	            		}
	            		
	                }
            	} catch (JSONException e) {
					// TODO Auto-generated catch block
            		Toast.makeText(mContext, "参数错误，请检查", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
			try{
				mContext.startActivity(intent);
			}catch(ActivityNotFoundException ane){
				Log.d(TAG,"SW_OPEN_APP ============ActivityNotFoundException========");
				return -2;
			}
		}else{
			Log.d(TAG,"============intent========"+intent);
			return -1;
		}
		return 0;
	}

	  /*
       * U盘播放视频
       * */
	  @JavascriptInterface
	  public  void openUdiskVideo() {
		  String path = new DataUtil(mContext).getsharepreferences("u_path")+"/hisu";
		  Log.e("path", path);
		  if( new File(path).exists()){
			  Intent intent = new Intent(mContext, UpathActivity.class);
			  mContext.startActivity(intent);
		  }else{
			  Toast.makeText(mContext, "U盘不存在",Toast.LENGTH_LONG).show();
		  }
	  }

	@JavascriptInterface
	public int openApp(String packageName, String activityName) {
		return openApp(packageName,activityName,null);
	}
	@JavascriptInterface
	public int openApp(String packageName) {
		return openApp(packageName,null);
	}
	@JavascriptInterface
	public void downloadApk(String downloadUrl) {
		Log.d(TAG, " " + CommonFunction._FUNC_());
		new UpdateManager(mContext).downloadApk(downloadUrl);
	}
	@JavascriptInterface
	public int uninstallApp(String packageName){
		try{
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
			String jsonStr = sp.getString(Constants.APK_FILE_PATH_INFO, "{}");
			System.out.println("jsonStr==========="+jsonStr);
			JSONObject jsonObj= new JSONObject(jsonStr);
			if(jsonObj.has(packageName)){
				String filePath = jsonObj.getString(packageName);
				File file = new File(filePath);
				if(file.exists()){
					file.delete();
				}
				jsonObj.remove(packageName);
				sp.edit().putString(Constants.APK_FILE_PATH_INFO, jsonObj.toString()).commit();
			};
			Uri packageURI = Uri.parse("package:"+packageName);
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);   
			mContext.startActivity(uninstallIntent);
			return 1;
		}catch(NullPointerException npe){
			npe.printStackTrace();
		}catch(ActivityNotFoundException anfe){
			anfe.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	@JavascriptInterface
	public String getModel() {
		Log.d(TAG, " " + CommonFunction._FUNC_());
		return Build.MODEL;
	}
	@JavascriptInterface
	public String getSDK() {
		Log.d(TAG, " " + CommonFunction._FUNC_());
		return Build.VERSION.SDK;
	}
	@JavascriptInterface
	public String getRelease() {
		Log.d(TAG, " " + CommonFunction._FUNC_());
		return Build.VERSION.RELEASE;
	}
	@JavascriptInterface
	public String getMacAddress() {
		Log.e(TAG, " " + CommonFunction._FUNC_());
		String mac = ToolsUtil.getLocalMacAddressFromIp(mContext);
		Log.e(TAG, "mac ==" + mac);
		return mac;
	}
	  /*湖北盒子CA卡*/
	@JavascriptInterface
	public String getCaCardAddress(){

		try {
			Class<?> caPro = Class.forName("android.os.SystemProperties");
			Method method = caPro.getMethod("get", String.class);
			String caAddr = (String) method.invoke(null, "persist.sys.mmcp.smartcard"); //数码盒子ca
//			String caAddr = (String) method.invoke(null, "persist.sys.ca.card_id"); //茁壮盒子ca
			return caAddr;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@JavascriptInterface
	public String getIpAddress() {
		Log.d(TAG, " " + CommonFunction._FUNC_());
		String mac = ToolsUtil.getLocalIpAddress();
		Log.d(TAG, "mac ==" + mac);
		return mac;
	}
	@JavascriptInterface
	public  void clearPreventDefalutKeyList(){
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called.");
		preventDefaultKeyList.clear();
	}
	@JavascriptInterface
	public  boolean isPreventDefault(int keyCode){
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called.");
		boolean ret = preventDefaultKeyList.contains(keyCode);
		Log.d(TAG, " " + keyCode + "=" + ret);
		return ret;
	}
	@JavascriptInterface
	public void preventDefault(int keyCode){
		Log.d(TAG, " " + CommonFunction._FUNC_() + " called.");
		
		int irkeyCode = BrowserUtil.browserKey2irKey(keyCode);
		Log.d(TAG, " keyCode=" + keyCode + " irkeyCode="+irkeyCode);
		if(irkeyCode>0)preventDefaultKeyList.add(irkeyCode);
	}

      public static String group_ID = null;
	  @JavascriptInterface
	  public String getGroupID(){
		  Log.d(TAG, "GroupID---- " + group_ID);
		  if(group_ID != null && group_ID.equals(null)){
			  return group_ID;
		  }else{
		  	return null;
		  }
	  }
}
