package com.hisu.webbrowser.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
//import android.view.IWindowManager;
//import android.os.ServiceManager;
//import android.os.RemoteException;
import android.view.KeyEvent;

public class BrowserUtil {
	private static final String TAG = "BrowserUtil";
	public static  String U_PATH = "";

	
	/*
	 * 判断网络是否正常
	 * 
	 * */
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
	public static String getMIMEType(File f){
		String type = "";
		String fName = f.getName();
		
		String end = fName.substring(fName.lastIndexOf(".")+1,
										fName.length()).toLowerCase();
		
		if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
				end.equals("xmf")||end.equals("ogg")||end.equals("wav")){
			type = "audio";
		}else if(end.equals("3gp")||end.equals("mp4")||end.equals("mpg")||
				end.equals("rmvb")||end.equals("avi")||end.equals("ts")){
			type = "video";
		}else if(end.equals("jpeg")||end.equals("bmp")||end.equals("gif")||
				end.equals("png")){
			type = "image";
		}else if(end.equals("apk")){
			//android.permission.INSTALL_PACKAGES
			type = "application/vnd.android.package-archive";
		}else{
			type = "*";
		}
		return type;
	}
	
	public static Process executeCmd(final String cmd) throws IOException{
		Log.i(TAG, "shell do command: " + cmd);
		return Runtime.getRuntime().exec(cmd);
	}
	
	public static boolean isNumeric(String str)
	{
		if(str == null || str.length() <= 0)
			return false;
		
		char[] ch = new char[str.length()];
		ch = str.toCharArray();
		for(int i=0;i<ch.length;i++){
			if(ch[i]<48 || ch[i]>57){
				return false;
			}
		}
		return true;
	}
	
	public static boolean isSerialValid(String serial){
		return true;
	}
	
//	public static boolean isNetworkAvailable( Context context ) { 
//	    ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//	    if (connectivity == null) {    
//	      return false;
//	    } else {  
//	        NetworkInfo[] info = connectivity.getAllNetworkInfo();    
//	        if (info != null) {
//	            for (int i = 0; i < info.length; i++) {           
//	                if (info[i].getState() == NetworkInfo.State.CONNECTED) {            
//	                    return true; 
//	                }        
//	            }     
//	        } 
//	    }   
//	    return false;
//	}
	
	
	@SuppressWarnings("finally")
	public static  ArrayList<String>  executeCommand(final String command,final String filter,
    		final long timeout) throws IOException, InterruptedException, TimeoutException {
    	Log.d(TAG,"executeCommand 111");
    	ArrayList<String> result = new ArrayList<String>();
		String line = "";
    	Process process = Runtime.getRuntime().exec(command);
    	
        Worker worker = new Worker(process);
        worker.start();
        Log.d(TAG,"executeCommand 2222");
        try {
            worker.join(timeout);
            if (worker.exit != null){
            	Log.d(TAG,"executeCommand 333");
            	InputStreamReader is = new InputStreamReader(process.getInputStream());
    			BufferedReader br = new BufferedReader (is);
    			
    			int maxLine = 100;
    			while ((line = br.readLine ()) != null  && maxLine-- > 0 ) {
    				if(line.contains(filter))
    					result.add(line);
    				Log.i(TAG,"line: "+line);
    			}
    			
            } else{
                throw new TimeoutException();
            }
        } catch (InterruptedException ex) {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw ex;
        } finally {
            process.destroy();
            return result;
        }
    }
   

    private static class Worker extends Thread {
        private final Process process;
        private Integer exit;

        private Worker(Process process) {
            this.process = process;
        }

        public void run() {
            try {
                exit = process.waitFor();
            } catch (InterruptedException ignore) {
                return;
            }
        }
    }
    
	public static ArrayList<String> callCmd(String cmd,String filter) { 
		ArrayList<String> result = new ArrayList<String>();
		String line = "";
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader (is);
			
			int maxLine = 100;
			while ((line = br.readLine ()) != null  && maxLine-- > 0 ) {
				if(line.contains(filter))
					result.add(line);
				Log.i(TAG,"line: "+line);
			}
			proc.waitFor();
			proc.exitValue();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static int irKey2BrowserKey(int irKeyCode){
		
		Log.d(TAG,"irKey2BrowserKey irKeyCode = "+irKeyCode);
		int ret = 0;
		switch (irKeyCode) {			
		case KeyEvent.KEYCODE_BACK:
			ret =  BrowserEvent.KeyEvent.KEY_BACK;
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			ret =  BrowserEvent.KeyEvent.KEY_SELECT;
			break;
			
		case KeyEvent.KEYCODE_DPAD_LEFT:
			ret =  BrowserEvent.KeyEvent.KEY_LEFT;
			break;
			
		case KeyEvent.KEYCODE_DPAD_UP:
			ret =  BrowserEvent.KeyEvent.KEY_UP;
			break;
			
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			ret =  BrowserEvent.KeyEvent.KEY_RIGHT;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
				ret =  BrowserEvent.KeyEvent.KEY_DOWN;
				break;
			
		case KeyEvent.KEYCODE_POWER:
		case KeyEvent.KEYCODE_STB_POWER:
			ret =  BrowserEvent.KeyEvent.KEY_STANDBY;
			break;

		case KeyEvent.KEYCODE_PAGE_UP:
			ret =  BrowserEvent.KeyEvent.KEY_PAGE_UP;
			break;
		case KeyEvent.KEYCODE_PAGE_DOWN:
			ret =  BrowserEvent.KeyEvent.KEY_PAGE_DOWN;
			break;	
		

		case KeyEvent.KEYCODE_MENU:
			ret =  BrowserEvent.KeyEvent.KEY_MENU;
			break;
			
		case KeyEvent.KEYCODE_CHANNEL_UP:
			ret =  BrowserEvent.KeyEvent.KEY_CHANNEL_UP;
			break;
		case KeyEvent.KEYCODE_CHANNEL_DOWN:
			ret =  BrowserEvent.KeyEvent.KEY_CHANNEL_DOWN;
			break;
			
		case KeyEvent.KEYCODE_VOLUME_UP:
			ret =  BrowserEvent.KeyEvent.KEY_VOLUME_UP;
			break;
			
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			ret =  BrowserEvent.KeyEvent.KEY_VOLUME_DOWN;
			break;
			
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			ret =  BrowserEvent.KeyEvent.KEY_MUTE;
			break;	
			
		case KeyEvent.KEYCODE_F1:
			ret =  BrowserEvent.KeyEvent.KEY_RED;
			break;
		case KeyEvent.KEYCODE_F2:
			ret =  BrowserEvent.KeyEvent.KEY_GREEN;
			break;
		case KeyEvent.KEYCODE_F3:
			ret =  BrowserEvent.KeyEvent.KEY_YELLOW;
			break;
		case KeyEvent.KEYCODE_F4:
			ret =  BrowserEvent.KeyEvent.KEY_BLUE;
			break;
		case KeyEvent.KEYCODE_CALL:	
			ret =  BrowserEvent.KeyEvent.KEY_CALL;
			break;
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
		case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
		case KeyEvent.KEYCODE_MEDIA_REWIND:
		case KeyEvent.KEYCODE_MEDIA_STOP:
		case KeyEvent.KEYCODE_POUND:
		default:
			Log.d(TAG,"irKey2BrowserKey irKeyCode = "+irKeyCode +" not defined.....");
			break;
		}
		Log.d(TAG,"irKey2BrowserKey irKeyCode = "+irKeyCode +" trans to ret = "+ret);
		return ret;
	}
	
	public static int browserKey2irKey(int keyCode){
		Log.d(TAG,"browserKey2irKey keyCode = "+keyCode);
		int ret = 0;
		switch (keyCode) {
		case  BrowserEvent.KeyEvent.KEY_HOMEPAGE:
			ret = KeyEvent.KEYCODE_HOME;
			break;
		case BrowserEvent.KeyEvent.KEY_BACK:
			ret =  KeyEvent.KEYCODE_BACK;
			break;
		case BrowserEvent.KeyEvent.KEY_SELECT:
			ret =  KeyEvent.KEYCODE_DPAD_CENTER;
			break;
		case BrowserEvent.KeyEvent.KEY_CALL:
			ret =  KeyEvent.KEYCODE_CALL;
			break;
			
		case BrowserEvent.KeyEvent.KEY_PAGE_UP:
			ret =  KeyEvent.KEYCODE_PAGE_UP;
			break;
		case BrowserEvent.KeyEvent.KEY_PAGE_DOWN:
			ret =  KeyEvent.KEYCODE_PAGE_DOWN;
			break;
		
		case BrowserEvent.KeyEvent.KEY_LEFT:
			ret =  KeyEvent.KEYCODE_DPAD_LEFT;
			break;
			
		case BrowserEvent.KeyEvent.KEY_UP:
			ret =  KeyEvent.KEYCODE_DPAD_UP;
			break;
			
		case BrowserEvent.KeyEvent.KEY_RIGHT:
			ret =  KeyEvent.KEYCODE_DPAD_RIGHT;
			break;
			
		case BrowserEvent.KeyEvent.KEY_DOWN:
			ret =  KeyEvent.KEYCODE_DPAD_DOWN;
			break;
			
			
		case BrowserEvent.KeyEvent.KEY_VOLUME_UP:
			ret =  KeyEvent.KEYCODE_VOLUME_UP;
			break;
			
		case BrowserEvent.KeyEvent.KEY_VOLUME_DOWN:
			ret =  KeyEvent.KEYCODE_VOLUME_DOWN;
			break;
		case BrowserEvent.KeyEvent.KEY_MUTE:
			ret =  KeyEvent.KEYCODE_VOLUME_MUTE;
			break;
			
		case BrowserEvent.KeyEvent.KEY_RED:
			ret =  KeyEvent.KEYCODE_F1;
			break;
		case BrowserEvent.KeyEvent.KEY_GREEN:
			ret =  KeyEvent.KEYCODE_F2;
			break;
		case BrowserEvent.KeyEvent.KEY_YELLOW:
			ret =  KeyEvent.KEYCODE_F3;
			break;
		case BrowserEvent.KeyEvent.KEY_BLUE:
			ret =  KeyEvent.KEYCODE_F4;
			break;
		case BrowserEvent.KeyEvent.KEY_MENU:
			ret =  KeyEvent.KEYCODE_MENU;
			break;	
		default:
			Log.d(TAG,"browserKey2irKey keyCode = "+keyCode +" not defined.....");
			break;
		}
		Log.d(TAG,"browserKey2irKey keyCode = "+keyCode +" trans to ret = "+ret);
		return ret;
	}
	

}
