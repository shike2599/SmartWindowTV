package com.hisu.alicloud.gs.dj.webbrowser.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hisu.alicloud.gs.dj.webbrowser.R;
import com.hisu.alicloud.gs.dj.webbrowser.mode.AppMsg;
import com.hisu.alicloud.gs.dj.webbrowser.mode.CacheVideo;
import com.hisu.alicloud.gs.dj.webbrowser.mode.WebInterface;
import com.hisu.alicloud.gs.dj.webbrowser.util.GsonHelper;
import com.hisu.alicloud.gs.dj.webbrowser.util.HttpRequestUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class UpdateVideoManager {
	List<CacheVideo> mVideoList;
	private Context mContext;

	private int progress;
	private static final int DOWNLOAD = 1;
	private static final int DOWNLOAD_FINISH = 2;
	private static final int HAS_NEW_VERSION = 3;
	private static final int HAS_NOT_NEW_VERSION = 4;
	private static final int DOWNLOAD_FAILED = 5;	

	private int mIndex = 0;
	private ProgressBar mProgressBar;
	private Dialog mDownloadDialog;
		
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DOWNLOAD:
				//System.out.println((int)(progress/10));
				if(mProgressBar != null)
				mProgressBar.setProgress(progress);
				break;
			case HAS_NOT_NEW_VERSION:
				Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_SHORT).show();
				mMainHandler.sendEmptyMessage(1);
				break;
			case DOWNLOAD_FAILED:
				Toast.makeText(mContext, R.string.soft_download_failed, Toast.LENGTH_SHORT).show();
			case DOWNLOAD_FINISH:
				mDownloadDialog.dismiss();
				mIndex ++;

			case HAS_NEW_VERSION:
				if(WebInterface.DOWNVIDEO){
					
				if(mIndex<mVideoList.size()){
					showDownloadDialog();
					
					downloadFile(mVideoList.get(mIndex));
				}else{
					mMainHandler.sendEmptyMessage(1);
				}
				}else{
					
					mMainHandler.sendEmptyMessage(1);
				}
				break;
			case	AppMsg.MSG_CACHE_VIDEO:
				JSONObject obj = (JSONObject)msg.obj;
				try {
					if(obj.has("dataList")){
						GsonHelper<CacheVideo> cacheHelper = new GsonHelper<CacheVideo>();
						mVideoList = cacheHelper.parseList(obj.getJSONArray("dataList"),CacheVideo.class);
						Log.e("UpdateVideoManager", obj.getJSONArray("dataList").toString());
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(hasNewVideo()?HAS_NEW_VERSION:HAS_NOT_NEW_VERSION);
				break;
				
			}
		};
	};
	private Handler mMainHandler = null;
	public UpdateVideoManager(Context context,Handler handler) {
		super();
		this.mContext = context;
		this.mMainHandler = handler;
	}


	public void checkUpdate(String stbMac) {
		String url = WebInterface.URL_CACHE_VIDEO();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("stbMac", stbMac);
		new HttpRequestUtil(mContext, mHandler).doRequest(url, params , false, AppMsg.MSG_CACHE_VIDEO);
	}
	
	private boolean hasNewVideo(){
		try{
			if (null != mVideoList && mVideoList.size()>0) {
				GsonHelper<CacheVideo> cacheHelper = new GsonHelper<CacheVideo>();
				List<CacheVideo> list = new ArrayList<CacheVideo>();
				String spKey = "hisu_cache_video_json";
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
				String saveCacheJson = sp.getString(spKey, "[]");
				System.out.println("saveCacheJson=="+saveCacheJson);
				List<CacheVideo> saveCacheList = cacheHelper.parseList(new JSONArray(saveCacheJson), CacheVideo.class);
				for(CacheVideo video : mVideoList){
					boolean isCached = false;
					for(CacheVideo v : saveCacheList){
						if(v.equals(video)){
							isCached = true;
							break;
						}
					}
					if(!isCached){
						list.add(video);
						Log.e("****", "��һ��");
					}
				}
				for(int i=0;i<saveCacheList.size();){
					boolean keep = false;
					CacheVideo v = saveCacheList.get(i);
					for(CacheVideo o : mVideoList){
						if(v.equals(o)){
						
							keep = true;
							break;
						}
					}
					File file = new File(v.getSavePath());
					Log.e("file", file.toString());
					if(!keep){
						if(file.exists()){
							file.delete();
						}
						saveCacheList.remove(i);
						continue;
					}else{
						if(!file.exists()){
							list.add(v);
							Log.e("****", "�ڶ���");
						}
					}
					i++;
				}
				SharedPreferences.Editor eidt = sp.edit();
				//System.out.println("saveJSon=========="+cacheHelper.toJson(mVideoList));
				eidt.putString(spKey,cacheHelper.toJson(mVideoList));
				eidt.commit();
				System.out.println("list.size()=="+list.size());
				if(list.size()>0){
					mVideoList = list;
					System.out.println("mVideoList.size()=="+mVideoList.size());
					return true;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	private void showDownloadDialog() {
		if(mDownloadDialog==null){
			Builder builder = new Builder(mContext);
			builder.setTitle(R.string.app_downloading);
			final LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(R.layout.softupdate_progress, null);
			mProgressBar = (ProgressBar) view.findViewById(R.id.update_progress);
			builder.setView(view);
	
			/*builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});*/
			mDownloadDialog = builder.create();
		}
		mProgressBar.setProgress(0);
		mDownloadDialog.show();
	}

	private static final String TAG = "UpdateVideoManager";
	private void downloadFile(CacheVideo video) {
		// TODO Auto-generated method stub
//		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
////			Toast.makeText(mContext, "SD�������ڣ�", Toast.LENGTH_LONG).show();
//			mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
//			return;
//		}
		
		HttpUtils http = new HttpUtils();
		File file = new File(video.getSavePath());
		if(file.exists())file.delete();
		http.download(video.getUrl(),
		    video.getSavePath(),
		    true, 
		    true, 
		    new RequestCallBack<File>() {
		        @Override
		        public void onStart() {
		            //testTextView.setText("conn...");
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		            //testTextView.setText(current + "/" + total);
		        	progress = (int) (((float) current / total) * 100);
		        	Log.e("onLoading", "onLoading");
					mHandler.sendEmptyMessage(DOWNLOAD);
					
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		           // testTextView.setText(msg);
					Log.i(TAG, TAG + ": onFailure===" + msg + ",==" + error.toString());
					mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
		        }

			
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					// TODO Auto-generated method stub
					//testTextView.setText("downloaded:" + responseInfo.result.getPath());
					Log.e("onSuccess", "onSuccess");
					mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
				}
		});
		//handler.cancel();
	}
}
