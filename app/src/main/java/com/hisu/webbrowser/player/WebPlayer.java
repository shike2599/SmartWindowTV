package com.hisu.webbrowser.player;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.appwidget.TeeveeWidgetHost;
import android.appwidget.TeeveeWidgetHostView;
import android.ccdt.dvb.service.DvbPlayerListener;
import android.ccdt.dvb.service.DvbPlayerService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.telecast.NetworkManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
//import android.widget.RelativeLayout.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;

import android.widget.RelativeLayout;
import com.hisu.webbrowser.R;
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
	private RelativeLayout mDvbPlayLayout;

	private TeeveeWidgetHost mAppWidgetHost;
	private AppWidgetManager mAppWidgetManager;
	private NetworkManager mNetworkManager;
	private AppWidgetProviderInfo mAppWidgetInfo;
	private TeeveeWidgetHostView mHostView;

	private int appWidgetId;
	static final int APPWIDGET_HOST_ID = 0x100;
	static final String ID = "284c19b9-39aa-45e4-9956-9e15aa6e9168";//陕西
	private String mDvbUrl;
	private DvbPlayerService mService = null;
	private int mX;
	private int mY;
	private int mW;
	private int mH;

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

	private Context mContext;
	public WebPlayer(Context context, SurfaceView surfaceView, RelativeLayout relativeLayout, int width,
		int height) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mDvbPlayLayout = relativeLayout;
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

//		playDvb("dvb://1.1.76", 63, 143, 558, 305);
	}

	public int start(String url) {
		Log.d(TAG,"start called... url = "+url +" mSurfaceCreated = "+mSurfaceCreated);
		

		mUrl = url;
		handler.sendEmptyMessageDelayed(0, 1000);
		return 0;
	}

	public void playDvb(String url, final int x, final int y, final int w, final int h){
		mDvbUrl = url;
		mX = x;
		mY = y;
		mW = w;
		mH = h;
		Log.i(TAG, "playDvb===>>" + url);
		if (url.startsWith("channelId")){
			Log.i(TAG, "channelId===>>" + url);
			mSurfaceView.setVisibility(View.GONE);
			mDvbPlayLayout.setVisibility(View.VISIBLE);
			mHostView = initRemoteView(mContext);
			if (mHostView != null) {
				mDvbPlayLayout.addView(mHostView);
			}
			//设置没有插cabel线时所显示的图片
			mHostView.setVideoMask(
				mContext.getResources().getDrawable(R.drawable.ic_launcher),
				TeeveeWidgetHostView.MASK_FLAG_NO_VIDEO);

			if(mHostView!=null){
				Log.d(TAG, "onClick toChannel");
				//开始播放
				mHostView.toChannel(url);

				Log.d(TAG, "onClick setVideoBounds");

				Log.d(TAG,"x----"+x+"---y----"+y+"---w---"+w+"---h----"+h);
				Rect r = new Rect();
				r.left = x;
				r.top = y;
				r.right = w + x;
				r.bottom = h + y;
				//设置视频大小、位置
				Log.d(TAG,"left----"+r.left+"---top----"+r.top+
						"--- right---"+r.right+"---bottom----"+r.bottom);
				mHostView.setAutoVideoBounds();
				mHostView.setVideoBounds(r);

				Log.d(TAG, "onClick setVolume");
				mHostView.setVolume(0.5f);
			}
		}else if (url.startsWith("dvb:")){
			Log.i(TAG, "dvb:===>>" + url);
			binDvbServer();
		}

	}

	private void binDvbServer(){
		Intent intent = new Intent();
		intent.setAction("android.ccdt.dvb.service.DvbPlayerService");
		boolean ret = mContext.bindService(intent,  conn, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, " onServiceConnected ...");
			mService = DvbPlayerService.Stub.asInterface(service);

			try {

				mService.setPlayerListener(mListener);
				//int x, int y, int w, int h
				//设定视频显示框
				mService.setPosition(mX, mY, mW, mH);
				/*url格式：dvb://network_id.transport_stream_id.service_id*/
				mService.start(mDvbUrl);//陕西卫视
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, " onServiceDisconnected ...");
		}
	};


	private DvbPlayerListener mListener = new DvbPlayerListener.Stub() {

		@Override
		public void onError(int errorCode) throws RemoteException {
			// TODO Auto-generated method stub
			Log.d(TAG, "onError    errorCode= " + errorCode);
		}
	};

	public void dvbResume(){
		if (mDvbUrl != null){
			Log.d(TAG, "onClick stop");
			if (mDvbUrl.startsWith("channelId")){
				if(mAppWidgetHost!=null){
					mHostView.toChannel(mDvbUrl);
				}
			}else if (mDvbUrl.startsWith("dvb:")){
				binDvbServer();
			}
		}
	}

	public void stopDvbPlay(){

		Log.d(TAG, "onClick stop");
		//停止播放
		if (mDvbUrl != null){
			if (mDvbUrl.startsWith("channelId")){
				if(mAppWidgetHost!=null){
					mHostView.stop();
				}
			}else if (mDvbUrl.startsWith("dvb:")){
				try {
					mService.stop();
					mContext.unbindService(conn);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private TeeveeWidgetHostView initRemoteView(Context context) {
		Log.d(TAG, "initRemoteView-->>");
		TeeveeWidgetHostView hostView = null;
		mNetworkManager = NetworkManager.getInstance(context);
		Log.d(TAG, "initRemoteView-- 1");
		mAppWidgetManager = AppWidgetManager.getInstance(context);
		Log.d(TAG, "initRemoteView-- 2");
		mAppWidgetHost = new TeeveeWidgetHost(context, APPWIDGET_HOST_ID);
		Log.d(TAG, "initRemoteView-->>3");
		mAppWidgetHost.startListening();
		appWidgetId = mAppWidgetHost.allocateAppWidgetId();
		Log.d(TAG, "initRemoteView-->>4");
		boolean b = mNetworkManager.bindNetworkTeeveeWidgetId(ID, appWidgetId,
			NetworkManager.PROPERTY_TEEVEE_WIDGET_SMALL);
		Log.d(TAG, "initRemoteView--b:"+b);
		if (b) {
			mAppWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
			hostView = (TeeveeWidgetHostView) mAppWidgetHost.createView(
				context, appWidgetId, mAppWidgetInfo);
			hostView.setId(appWidgetId);
		}
		Log.d(TAG, "initRemoteView--<<");
		return hostView;
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
		Log.d(TAG," "+ CommonFunction._FUNC_()+" called. mState = "+stateStr(mState));
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
		Log.d(TAG," "+CommonFunction._FUNC_()+" called. mState = "+stateStr(mState)+",flag="+flag);
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
						Log.d(TAG," "+CommonFunction._FUNC_()+" clear frame");
					}else{
						Log.d(TAG," "+CommonFunction._FUNC_()+" canvas is null!!!!");
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

		surfaceSetUp(x, y, w, h);

		Log.d(TAG," setVideoLayout:"+"x=" + x + ",y=" + y + ",w=" + w
		+ ",h=" + h);

		if (!TextUtils.isEmpty(mDvbUrl)){

			if (mDvbUrl.startsWith("channelId")){
				if (mHostView != null){
					Rect r = new Rect();
					r.left = x;
					r.top = y;
					r.right = w+x;
					r.bottom = h+y;

					Log.d(TAG,"new_left--"+r.left+"--- " +
							"new_top---"+r.top+"--new_right--"+
							r.right+"---new_bottom--"+r.bottom
					);
					//设置视频大小、位置
					mHostView.setVideoBounds(r);
				}
			}else if (mDvbUrl.startsWith("dvb:")){
				if (mService != null){
					try {
						mService.setPosition(x, y, w, h);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}

		}

		return 0;
	}

	private void surfaceSetUp(int x, int y, int w, int h){
		if(x+w > mWidth)
			w = mWidth-x;
		if(y+h > mHeight)
			h = mHeight-y;

		MarginLayoutParams params = (MarginLayoutParams)
				mSurfaceView.getLayoutParams();
		if(params!=null){
			params.leftMargin = x;
			params.topMargin = y;
			params.width = w;
			params.height = h;

			mSurfaceView.setLayoutParams(params);
		}
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
