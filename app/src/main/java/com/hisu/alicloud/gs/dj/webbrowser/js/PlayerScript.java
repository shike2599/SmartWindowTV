package com.hisu.alicloud.gs.dj.webbrowser.js;


import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.hisu.alicloud.gs.dj.webbrowser.player.WebPlayer;
import com.hisu.alicloud.gs.dj.webbrowser.util.BrowserMessage;
import com.hisu.alicloud.gs.dj.webbrowser.util.CommonFunction;

//import com.arisci.util.SWMessage;

public class PlayerScript {
	private static final String TAG = "PlayerScript";
	private Handler mHandler = null;
	private WebPlayer mSWPlayer;
	private Context mContext;
	private AudioManager mAudioManage = null;
	private int mMaxVolume = 0;
	private int mCurrentVolume = 0;

	// constructed function
	
	public PlayerScript(Handler handler, WebPlayer player,Context context) {
		// TODO Auto-generated constructor stub
		mHandler = handler;
		mSWPlayer = player;
		mContext = context;
	}
	
	@JavascriptInterface
	public int play(String url){
		Log.d(TAG,"openUrl url = "+url);
		play(url,0,0,mSWPlayer.getWidth(),mSWPlayer.getHeight());
		return 0;
	}
	@JavascriptInterface
	public int play(String url,int x,int y,int w,int h){
		Log.d(TAG,"openUrl url = "+url + " x ="+x+" y = "+y +" w="+w +" h= "+h);
		if (null == mHandler) {
			return -1;
		}
		Message msg = new Message();
		msg.what = BrowserMessage.SW_MEDIA_CMD_PLAY;
		msg.getData().putString("URL", url);
		mHandler.sendMessage(msg);
		
		setLocation(x,y,w,h);
		return 0;
	}
	@JavascriptInterface
	public int setLocation(int x,int y,int w,int h){
		Log.d(TAG,"setLocation x ="+x+" y = "+y +" w="+w +" h= "+h);

		if (null == mHandler) {
			return -1;
		}
		Message msg = new Message();
		msg.what = BrowserMessage.SW_MEDIA_CMD_SET_LOCATION;
		msg.getData().putInt("x",x);
		msg.getData().putInt("y",y);
		msg.getData().putInt("w",w);
		msg.getData().putInt("h",h);
		mHandler.sendMessage(msg);
		return 0;
	}
	@JavascriptInterface
	public int pause(){
		Log.d(TAG," "+ CommonFunction._FUNC_()+" called.");
		if (null == mHandler) {
			return -1;
		}
		new Message();
		mSWPlayer.pause();
		return 0;
	}
	@JavascriptInterface
	public int resume(){
		Log.d(TAG," "+CommonFunction._FUNC_()+" called.");
		if (null == mHandler) {
			return -1;
		}
		Message msg = new Message();
		msg.what = BrowserMessage.SW_MEDIA_CMD_RESUME;
		mHandler.sendMessage(msg);
		return 0;
	}
	@JavascriptInterface
	public int seek(String pos)
	{
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. pos = "+pos);
		if (null == mHandler) {
			return -1;
		}
		Message msg = new Message();
		msg.what = BrowserMessage.SW_MEDIA_CMD_SEEK;
		msg.getData().putInt("pos",Integer.parseInt(pos));
		mHandler.sendMessage(msg);
		return 0;
	}
	@JavascriptInterface
	public int stop(){
		return stop(null);
	}
	@JavascriptInterface
	public int stop(String flag)
	{
		Log.d(TAG," "+CommonFunction._FUNC_()+" called.");
		
		if (null == mHandler) {
			return -1;
		}
		Message msg = new Message();
		msg.what = BrowserMessage.SW_MEDIA_CMD_STOP;
		if(!TextUtils.isEmpty(flag)){
			msg.getData().putInt("flag",Integer.parseInt(flag));
		}		
		mHandler.sendMessage(msg);
		return 0;
	}
	@JavascriptInterface
	public int getDuration(){
		Log.d(TAG," "+CommonFunction._FUNC_()+" called.");
		if (null == mHandler) {
			return 0;
		}
		int duration = mSWPlayer.getDuration();
		Log.d(TAG," duration == "+duration);
		return duration;
	}
	@JavascriptInterface
	public int getCurrentPosition(){
		Log.d(TAG," "+CommonFunction._FUNC_()+" called.");
		if (null == mHandler) {
			return 0;
		}
		int currentPos = mSWPlayer.getCurrentTimePos();
		
		//****�޸�*****
//		int durationTotal = mSWPlayer.getDuration();
//		if(currentPos == durationTotal){
//			return durationTotal;
//		}
		
		Log.d(TAG," currentPos == "+currentPos);
		return currentPos;
	}
	@JavascriptInterface
	private AudioManager getAudioManage(){
		if(mAudioManage==null){
			mAudioManage = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
			mMaxVolume = mAudioManage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  //��ȡϵͳ������� 
			int curr = mAudioManage.getStreamVolume(AudioManager.STREAM_MUSIC);//��ȡ��ǰ����
			mCurrentVolume = curr*100/mMaxVolume;
		}
		return mAudioManage;
	}
	@JavascriptInterface
	public int getVolume(){
		Log.d(TAG," "+CommonFunction._FUNC_()+" called.");
		int curr = getAudioManage().getStreamVolume(AudioManager.STREAM_MUSIC);//��ȡ��ǰ����
		if(mCurrentVolume*mMaxVolume/100!=curr){
			mCurrentVolume = curr*100/mMaxVolume;
		}
		Log.d(TAG,"currentVolume=="+mCurrentVolume);
		return mCurrentVolume;
	}
	@JavascriptInterface
	public void setVolume(int num){
		Log.d(TAG," "+CommonFunction._FUNC_()+" called.");
		if(num<0)num = 0;
		if(num>100)num = 100;
		mCurrentVolume = num;
		int setVolume = mMaxVolume*mCurrentVolume/100;
		getAudioManage().setStreamVolume(AudioManager.STREAM_MUSIC, setVolume,AudioManager.FX_FOCUS_NAVIGATION_UP  | 0 );//
	}
}
