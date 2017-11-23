package com.hisu.webbrowser.player;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
//import android.widget.RelativeLayout.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.hisu.webbrowser.util.BrowserMessage;
import com.hisu.webbrowser.util.CommonFunction;


public class VideoPlayer {
	static final String TAG = "WebPlayer";
	static final int inited = 0;
	static final int preparing = 1;
	static final int prepared = 2;
	static final int started = 3;
	static final int stopped = 4;
	static final int paused = 5;

	private Handler mHandler;
	private VideoView mVideoView;
	private RelativeLayout mVideoContainer;
	//private MediaController mMediaController;
	private int mWidth;
	private int mHeight;
	private int mState;
	private String mUrl;

	public VideoPlayer(RelativeLayout videoContainer, VideoView videoView, int width,
			int height) {
		// TODO Auto-generated constructor stub
		mVideoView = videoView;
		mVideoContainer = videoContainer;
		//mMediaController = new MediaController(context);
		//mVideoView.setMediaController(mMediaController);
		mState = inited;
	}

	public int start(String url) {
		Log.d(TAG,"start called... url = "+url );
		

		mUrl = url;
		handler.sendEmptyMessageDelayed(0, 1000);
		return 0;
	}
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				start();
				break;
			}
		}
	};
	
	private OnPreparedListener mPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			if(mState==inited){
				mVideoView.start();
				mState =started;
				mHandler.sendEmptyMessage(BrowserMessage.SW_CMD_PLAY_SUCCESS);
			}
		}
	};
	private OnErrorListener mErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			Log.d(TAG,"OnErrorListener onError called");
			// TODO Auto-generated method stub
			mVideoView.stopPlayback();
			if(mHandler!=null){
				mHandler.sendEmptyMessage(BrowserMessage.SW_CMD_PLAY_ERROR);
			}
			return false;
		}
	}; 

	private OnCompletionListener mCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer arg0) {
			Log.d(TAG,"OnCompletionListener onCompletion called");
			mVideoView.stopPlayback();
			if(mHandler!=null){
				mHandler.sendEmptyMessage(BrowserMessage.SW_CMD_PLAY_END);
			}
			
		}
	};
	
	private void start() {
		Log.d(TAG," "+ CommonFunction._FUNC_()+" called. mState = "+stateStr(mState));
		mVideoView.setOnPreparedListener(mPreparedListener);
		mVideoView.setOnCompletionListener(mCompletionListener);
		mVideoView.setOnErrorListener(mErrorListener);

		mVideoView.setVideoPath(mUrl);  
		mState = started;
	}

	/**
	 * @param flag clear last frame if flag is 0 , otherwise keep the last frame
	 */
	public void stop(int flag) {
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. mState = "+stateStr(mState)+",flag="+flag);
		mVideoView.stopPlayback();
		setVideoLayout(0,0,0,0);
		mState = inited;
	}

	public int getPlayerState() {
		return mState;
	}

	
	public int setVideoLayout(int x,int y ,int w,int h) {
		Log.d(TAG," "+CommonFunction._FUNC_()+" called.   x ="+x+" y = "+y +" w="+w +" h= "+h);
		
		if(x < 0 || y< 0 || w < 0 || h< 0 )
			return -1;
		if(w==0 && h==0){
			mVideoView.setVisibility(View.INVISIBLE);
			return 0;
		}else{
			mVideoView.setVisibility(View.VISIBLE);
		}
		if(x+w > mWidth)
			w = mWidth-x;
		if(y+h > mHeight)
			h = mHeight-y;

		MarginLayoutParams params = (MarginLayoutParams)mVideoContainer.getLayoutParams();
		params.leftMargin = x;
		params.topMargin = y;
		params.bottomMargin = mWidth-w-x;
		params.rightMargin = mHeight-y-h;
		
		//RelativeLayout.LayoutParams lp= new  RelativeLayout.LayoutParams(w,h);
		//lp.leftMargin = x;
		//lp.topMargin = y;
        //lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		mVideoContainer.setLayoutParams(params);
		Log.d(TAG," "+CommonFunction._FUNC_()+" end");
		return 0;
	}

	public String getCurrentTimePos() {
		// TODO Auto-generated method stub
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. mState = "+stateStr(mState));
		if (mState == started) {
			return "" + mVideoView.getCurrentPosition()/1000;
		} else {
			return "0";
		}
	}

	public int getDuration() {
		// TODO Auto-generated method stub
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. mState = "+stateStr(mState));
		if (mState == started) {
			return  mVideoView.getDuration()/1000;
		} else {
			return 0;
		}
	}

	public void pause() {
		// TODO Auto-generated method stub
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. mState = "+stateStr(mState));
		if(mState == started && mVideoView.canPause()){
			mState = paused;
			mVideoView.pause();
		}
	}

	public void resume() {
		// TODO Auto-generated method stub
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. mState= "+stateStr(mState));
		if(mState == paused){
			mState = started;
			mVideoView.resume();
		}
	}

	public void seek(int pos) {
		// TODO Auto-generated method stub
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. mState = "+stateStr(mState) +" pos = "+pos);
		if(mState == started && mVideoView.canSeekForward() && mVideoView.canSeekBackward()){
			mVideoView.seekTo(pos);
		}
	}
	
	private String stateStr(int state)
	{
		switch(state)
		{
		case started:
			return "started";
		case paused:
			return "paused";
		case inited:
			return "inited";
		default:
			break;	
		}
		return "undefined state ="+state;
	}
	
	public int getWidth()
	{
		return mWidth;
	}
	public int getHeight()
	{
		return mHeight;
	}
	public void setHandler(Handler h){
		mHandler = h;
	}
}
