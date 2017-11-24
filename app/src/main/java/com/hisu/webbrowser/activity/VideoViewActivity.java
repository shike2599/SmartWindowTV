package com.hisu.webbrowser.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.hisu.webbrowser.R;

public class VideoViewActivity extends Activity{
	
	private VideoView videoview;
	private Intent in;
	private String path;
	private View dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_videoview);
		videoview = (VideoView) findViewById(R.id.viedoview);
		initdata();
		initView();
		//设置视频控制器
//		videoview.setMediaController(new MediaController(this));

		videoview.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				dialog.setVisibility(View.GONE);
			}
		});

		//播放完成回调
		videoview.setOnCompletionListener( new MyPlayerOnCompletionListener());

		//设置视频路径
		videoview.setVideoPath(path);
	 
		videoview.start();
	}
	
	
	private void initView() {
		// TODO Auto-generated method stub
		dialog =  findViewById(R.id.dialog1);
	}


	private class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener{

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			finish();
		}
		
	}
	
	private void initdata() {
		// TODO Auto-generated method stub
		in = getIntent();
		path = in.getStringExtra("path");
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
