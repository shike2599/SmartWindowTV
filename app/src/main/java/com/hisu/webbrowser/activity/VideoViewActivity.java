package com.hisu.webbrowser.activity;

import java.net.URI;
import java.net.URL;

import com.hisu.webbrowser.R;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoViewActivity extends Activity{
	
	private VideoView videoview;
	private Intent in;
	private String path;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_videoview);
		videoview = (VideoView) findViewById(R.id.viedoview);
		initdata();
		//������Ƶ������
		videoview.setMediaController(new MediaController(this));
	 
	    //������ɻص�
		videoview.setOnCompletionListener( new MyPlayerOnCompletionListener());
	 
	    //������Ƶ·��
		videoview.setVideoURI( Uri.parse(path));
	 
		videoview.start();
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
