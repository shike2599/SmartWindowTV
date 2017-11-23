package com.hisu.webbrowser.player;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
//import android.widget.RelativeLayout.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;

import com.hisu.webbrowser.util.BrowserMessage;
import com.hisu.webbrowser.util.CommonFunction;


public class WebPlayer {
	static final String TAG = "WebPlayer";
	static final int inited = 0;
	static final int preparing = 1;
	static final int prepared = 2;
	static final int started = 3;
	static final int stopped = 4;
	static final int paused = 5;

	private Handler mHandler;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private MediaPlayer mMediaPlayer;
	private int mWidth;
	private int mHeight;
	private int mState;
	private String mUrl;
	private boolean mSurfaceCreated = false;

	public WebPlayer( SurfaceView surfaceView, int width,
			int height) {
		// TODO Auto-generated constructor stub
		mSurfaceView = surfaceView;
		mWidth = width;
		mHeight = height;
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceView.setFocusable(false);
		//mSurfaceView.setZOrderOnTop(false);
		mSurfaceHolder.setSizeFromLayout();
		mSurfaceHolder.addCallback(mCallback);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		mState = inited;
	}

	public int start(String url) {
		Log.d(TAG,"start called... url = "+url +" mSurfaceCreated = "+mSurfaceCreated);
		

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
	
	private void start() {
		//mSurfaceView.setZOrderOnTop(false);
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		if (mSurfaceCreated) {
			try {
	
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setOnPreparedListener(mPreparedListener);
				mMediaPlayer.setOnCompletionListener(mCompletionListener);
				mMediaPlayer.setOnErrorListener(mErrorListener);
				mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mSurfaceHolder.setFixedSize(mWidth, mHeight);
				mMediaPlayer.setDisplay(mSurfaceHolder);
				mMediaPlayer.setDataSource(mUrl);
				mMediaPlayer.prepareAsync();
				
				mState = preparing;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param flag clear last frame if flag is 0 , otherwise keep the last frame
	 */
	public void stop(int flag) {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
			
		}
		//mSurfaceView.setZOrderOnTop(false);
		if(flag==0 && mSurfaceHolder!=null ){
			Canvas canvas = null;
            try {
				synchronized (mSurfaceHolder) {
					MarginLayoutParams params = (MarginLayoutParams)mSurfaceView.getLayoutParams();
					Rect rect = new Rect(0,0, params.width, params.height);
					Log.d(TAG,"params.width=="+params.width+",params.height=="+params.height);
					canvas = mSurfaceHolder.lockCanvas(rect);
					if(canvas!=null){
						canvas.drawColor(Color.BLACK);
					}else{
						Log.d(TAG," "+ CommonFunction._FUNC_()+" canvas is null!!!!");
					}
				}
			} catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (null != mSurfaceHolder && null != canvas) {
	            	mSurfaceHolder.unlockCanvasAndPost(canvas);
	            }
	        }
		}
		mSurfaceHolder.setFixedSize(0, 0);
		setVideoLayout(0,0,0,0);
		mState = inited;
	}

	public int getPlayerState() {
		return mState;
	}

	private Callback mCallback = new Callback() {
		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
			Log.d(TAG,"surfaceDestroyed called");
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			Log.d(TAG,"surfaceCreated called");
			mSurfaceHolder = holder;
			
			mSurfaceCreated = true;
		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,int arg3) {
			// TODO Auto-generated method stub
			Log.d(TAG,"surfaceChanged called");

		}
	};
	private OnPreparedListener mPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			if(mState==preparing){
				mMediaPlayer.start();
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
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
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
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
			if(mHandler!=null){
				mHandler.sendEmptyMessage(BrowserMessage.SW_CMD_PLAY_END);
			}
			
		}
	};

	private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			// TODO Auto-generated method stub
			Log.d(TAG," "+CommonFunction._FUNC_()+" called.   percent =="+percent+"%");
			/*if(mPrecent== 0 && percent>=100){
				return;
			}
			if(mPrecent == 0 && mMediaPrepared && percent>20){
				mPrecent = percent;
				resume();
				mHandler.sendEmptyMessage(BrowserMessage.SW_MEDIA_CMD_START);
			}
			if(percent>=100){
				mPrecent = 0;
			}*/
		}
	};
	
	public int setVideoLayout(int x,int y ,int w,int h) {
		Log.d(TAG," "+CommonFunction._FUNC_()+" called.   x ="+x+" y = "+y +" w="+w +" h= "+h);
		
		if(x < 0 || y< 0 || w < 0 || h< 0 )
			return -1;
		if(w==0 && h==0){
			mSurfaceView.setVisibility(View.INVISIBLE);
			return 0;
		}else{
			mSurfaceView.setVisibility(View.VISIBLE);
		}
		if(x+w > mWidth)
			w = mWidth-x;
		if(y+h > mHeight)
			h = mHeight-y;

		MarginLayoutParams params = (MarginLayoutParams)mSurfaceView.getLayoutParams();
		if(params!=null){
			params.leftMargin = x;
			params.topMargin = y;
			params.width = w;
			params.height = h;
			
			mSurfaceView.setLayoutParams(params);
		}
		Log.d(TAG," "+CommonFunction._FUNC_()+" end");
		return 0;
	}

	public int getCurrentTimePos() {
		// TODO Auto-generated method stub
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. mState = "+stateStr(mState));
		if (mState == started  || mState == paused) {
			return mMediaPlayer.getCurrentPosition()/1000;
		} else {
			return 0;
		}
	}

	public int getDuration() {
		// TODO Auto-generated method stub
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. mState = "+stateStr(mState));
		if (mState == started  || mState == paused) {
			return  mMediaPlayer.getDuration()/1000;
		} else {
			return 0;
		}
	}

	public void pause() {
		// TODO Auto-generated method stub
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. mState = "+stateStr(mState));
		if(mState == started  || mState == paused){
			mState = paused;
			mMediaPlayer.pause();
		}
	}

	public void resume() {
		// TODO Auto-generated method stub
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. mState= "+stateStr(mState));
		if(mState == paused  || mState == paused){
			mState = started;
			mMediaPlayer.start();
		}
	}

	public void seek(int pos) {
		// TODO Auto-generated method stub
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. mState = "+stateStr(mState) +" pos = "+pos);
		if(mState == started || mState == paused){
			mMediaPlayer.seekTo(pos);
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
