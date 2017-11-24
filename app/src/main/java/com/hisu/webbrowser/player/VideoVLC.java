package com.hisu.webbrowser.player;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.hisu.webbrowser.libvlc.EventHandler;
import com.hisu.webbrowser.libvlc.IVideoPlayer;
import com.hisu.webbrowser.libvlc.LibVLC;
import com.hisu.webbrowser.libvlc.Media;
import com.hisu.webbrowser.libvlc.MediaList;

public class VideoVLC {


    private String mFilePath;

    // display surface
    private SurfaceView mSurface;
    private SurfaceHolder holder;

    // media player
    private LibVLC libvlc;
    private int mVideoWidth;
    private int mVideoHeight;
    private final static int VideoSizeChanged = -1;
    
    private Activity mContext;
    
    /*************
     * Activity
     *************/
    private int x,y;
   
    
	IVideoPlayer mVideoPlayer = new IVideoPlayer(){

	    @Override
	    public void setSurfaceSize(int width, int height, int visible_width,
	            int visible_height, int sar_num, int sar_den) {
	        Message msg = Message.obtain(mHandler, VideoSizeChanged, width, height);
	        msg.sendToTarget();
	    }

		@Override
		public void eventHardwareAccelerationError() {
			// TODO Auto-generated method stub
			
		}
		
	};
	
    /*************
     * Surface
     *************/
	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
	    }

	    public void surfaceChanged(SurfaceHolder surfaceholder, int format,
	            int width, int height) {
	        if (libvlc != null)
	            libvlc.attachSurface(holder.getSurface(), mVideoPlayer);
	    }

	    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
	    }
	};

	
    /*************
     * Events
     *************/

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<VideoVLC> mOwner;

        public MyHandler(VideoVLC owner) {
            mOwner = new WeakReference<VideoVLC>(owner);
        }
        
        @Override
        public void handleMessage(Message msg) {
        	VideoVLC player = mOwner.get();

            // SamplePlayer events
            if (msg.what == VideoSizeChanged) {
                player.setSize(msg.arg1, msg.arg2);
                return;
            } 
            // Libvlc events
            Bundle b = msg.getData();
            switch (b.getInt("event")) {
            case EventHandler.MediaPlayerEndReached:
                player.releasePlayer();
                break;
            case EventHandler.MediaPlayerPlaying:
            case EventHandler.MediaPlayerPaused:
            case EventHandler.MediaPlayerStopped:
            default:
                break;
            }
        }
    }
    
    public VideoVLC(SurfaceView view, String url, Activity ac,int x, int y){
    	mContext  =ac;
    	mFilePath = url;
        mSurface = view;
        holder = mSurface.getHolder();
        holder.addCallback(mSHCallback);
        this.x = x;
        this.y = y;
    }
    
    public void createPlayer() {
    	String media = mFilePath;
        releasePlayer();
        try {

            // Create a new media player
            libvlc = LibVLC.getInstance();
            libvlc.setHardwareAcceleration(2);//�����ʽ -1 ~  2 ��  �ֱ��Ӧ�� �汾
            libvlc.setSubtitlesEncoding("");
//            libvlc.setAout(LibVLC.AOUT_AUDIOTRACK_JAVA);
            libvlc.setTimeStretching(false);
            libvlc.setChroma("RV32");
            libvlc.setVerboseMode(true);
            
//            libvlc.setSubtitlesEncoding("");
//            libvlc.setTimeStretching(false);
            libvlc.setFrameSkip(true);
//            libvlc.setChroma("RV32");
//            libvlc.setVerboseMode(true);
            libvlc.setAout(-1);
            libvlc.setDeblocking(4);
            libvlc.setNetworkCaching(1500);
            
            libvlc.restart(mContext);
            EventHandler.getInstance().addHandler(mHandler);
            holder.setFormat(PixelFormat.RGBX_8888);
            holder.setKeepScreenOn(true);
            MediaList list = libvlc.getMediaList();
            list.clear();
            list.add(new Media(libvlc, LibVLC.PathToURI(media)), false);
            libvlc.playIndex(0);
        } catch (Exception e) {
        }
    }

    public void releasePlayer() {
        if (libvlc == null)
            return;
        EventHandler.getInstance().removeHandler(mHandler);
        libvlc.stop();
        libvlc.detachSurface();
        holder = null;
        libvlc.closeAout();
        libvlc.destroy();
        libvlc = null;
        
        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    public void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        holder.setFixedSize(mVideoWidth, mVideoHeight);

        LayoutParams lp = mSurface.getLayoutParams();
        lp.width = 1280;
        lp.height = 720;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }
}
