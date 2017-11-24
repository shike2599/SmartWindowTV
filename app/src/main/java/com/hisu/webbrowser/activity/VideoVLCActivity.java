package com.hisu.webbrowser.activity;

import java.lang.ref.WeakReference;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hisu.webbrowser.R;
import com.hisu.webbrowser.libvlc.EventHandler;
import com.hisu.webbrowser.player.VideoVLC;

@SuppressLint("InlinedApi")
public class VideoVLCActivity extends Activity {
	public final static String TAG = "LibVLCAndroidSample/VideoVLCActivity";

	public final static String LOCATION = "com.compdigitec.libvlcandroidsample.VideoActivity.location";
	SurfaceView surfaceview;
	private GestureDetector mGestureDetector;
	private VideoVLC mVideoView;

	/** 当前声音 */
	private int mVolume = -1;
	/** 当前亮度 */
	private float mBrightness = -1f;
	/** �?大声�? */
	private int mMaxVolume;

	private AudioManager mAudioManager;
	private View dialog;
	private final Handler eventHandler = new VideoPlayerEventHandler(this);
	private Intent intent ;
	private mthread run ;
	private boolean back = true;
	private int videotime = -1;
	int vedio = 0;
	Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 0:
				startvideo();
				break;
			case 1:
				dialog.setVisibility(View.GONE);
				break;
			case 2:
				Toast.makeText(getApplication(), "地址解析错误", Toast.LENGTH_SHORT).show();
				dialog.setVisibility(View.GONE);
				VideoVLCActivity.this.finish();
				break;
			case 3:
				VideoVLCActivity.this.finish();
				break;	
			}
		};
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);;
		setContentView(R.layout.sample);
		initdata();
		initview();
		EventHandler em = EventHandler.getInstance();
		em.addHandler(eventHandler);
		
		run = new mthread();
		run.start();
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

	}


	private void initdata() {
		// TODO Auto-generated method stub
		intent = getIntent();
		videotime = intent.getIntExtra("videotime",-1);
		Log.e("videotime", ""+videotime);
		if(videotime > 60){
			back = false;
			new Thread(){
				public void run() {
					
					while(videotime > -1){
						videotime --;
						try {
							sleep(1000);
							Log.e("videotime", ""+videotime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(videotime == 0){
							mhandler.sendEmptyMessage(3);
						}
					}
				};
			}.start();
		}
	}


	private void initview() {
		// TODO Auto-generated method stub
		surfaceview = (SurfaceView) findViewById(R.id.surface);
		dialog =  findViewById(R.id.dialog);
	}


	public class mthread extends Thread {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			mhandler.sendEmptyMessage(0);
		}
	}

	private void startvideo() {
		// TODO Auto-generated method stub
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如�?480px�?
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		mVideoView = new VideoVLC(
				surfaceview,
//				"http://fms.cntv.lxdns.com/live/flv/channel178.flv?AUTH=dbZJKJqpf9PQoPncjPje6Yija7QS4ZYH6oG+881mwH9QlUoPTgaN8NcqDD1TJ2B4ia9GotLJzlFkjjyoj4zRwQ==",
				intent.getStringExtra("path"),
//				"rtsp://admin:admin@192.168.1.15:554/11",
				this, screenWidth, screenHeight);
		mVideoView.createPlayer();
	}

	@Override
	protected void onResume() {
		super.onResume();

		Display disp = getWindowManager().getDefaultDisplay();
		int windowWidth = disp.getWidth();
		int windowHeight = disp.getHeight();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mVideoView != null) {
			mVideoView.releasePlayer();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mVideoView != null) {
			mVideoView.releasePlayer();
		}
		EventHandler em = EventHandler.getInstance();
		em.removeHandler(eventHandler);
		mAudioManager = null;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
//		case KeyEvent.KEYCODE_BACK:
//			
//			
//			return back;
		case KeyEvent.KEYCODE_HOME:
//			 mBrowser.destroy();
//			 android.os.Process.killProcess(android.os.Process.myPid());
			return back;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	


	private class VideoPlayerEventHandler extends Handler {
		private WeakReference<VideoVLCActivity> reference;

		public VideoPlayerEventHandler(VideoVLCActivity owner) {
			reference = new WeakReference<VideoVLCActivity>(owner);
		}

		public void handleMessage(Message msg) {
			// VideoVLCActivity activity = reference.get().ge
			// if (activity == null)
			// return;
			switch (msg.getData().getInt("event")) {
			case EventHandler.MediaPlayerPlaying:
				Log.e("mVLC", "Playing");
				break;
			case EventHandler.MediaPlayerPaused:
				Log.e("mVLC", "Paused");
				break;
			case EventHandler.MediaPlayerStopped:
				Log.e("mVLC", "Stopped");
				break;
			case EventHandler.MediaPlayerEndReached:
				Log.e("mVLC", "EndReached");
				break;
			case EventHandler.MediaPlayerVout:
				Log.e("mVLC", "PlayerVout");
				break;
			case EventHandler.MediaPlayerPositionChanged:
				if(vedio == 0){
					vedio = 1;
					mhandler.sendEmptyMessage(1);
				}
				Log.e("mVLC", "PositionChanged");
				break;
			case EventHandler.MediaPlayerEncounteredError:
				if(vedio == 0){
					vedio = 1;
					mhandler.sendEmptyMessage(2);
				}
				
				Log.e("mVLC", "EncounteredError");
				
				break;
			default:
				Log.i("mVLC", "eventHandler : " + msg.getData().getInt("event"));
				break;
			}
		}
	};

}
