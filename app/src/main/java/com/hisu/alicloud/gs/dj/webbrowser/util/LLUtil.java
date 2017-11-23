package com.hisu.alicloud.gs.dj.webbrowser.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Log;

import com.hisu.alicloud.gs.dj.webbrowser.bean.SysVideo;
import com.hisu.alicloud.gs.dj.webbrowser.mode.WebInterface;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class LLUtil {
	private final String PATH = Environment.getExternalStorageDirectory().getPath().toString()+"/hisu/";
	private final String LOGO = "LOGO";
	private final String MOVIE = "MOVIE";
	private final String ANIMATION = "ANIMATION";
	Activity act;
	SharedPreferences share;
	public LLUtil(Activity act){
		this.act = act;
		share = act.getSharedPreferences("sys",0);
	}
	/*
	 * ?????????????3?????
	 * */
	
	public  void checkSystemVideo(){
	
//		String url = WebInterface.SYSTEM_URL_CACHE_VIDEO+"?requestUser=sxbctv&requestPassword=123456&type=0&hotelId=7550004";
		String url = WebInterface.SYSTEM_URL_CACHE_VIDEO;
		
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
//		ArrayList<NameValuePair> list = new ArrayList<>();
////		params.addBodyParameter(list);
		params.addQueryStringParameter("requestUser","sxbctv");
		params.addQueryStringParameter("requestPassword","123456");
		params.addQueryStringParameter("type","0");
		params.addQueryStringParameter("hotelId","7550004");
		http.send(HttpRequest.HttpMethod.GET,
			    url,
			    params,
			    new RequestCallBack<String>() {

			        @Override
			        public void onStart() {
			        }

			        @Override
			        public void onLoading(long total, long current, boolean isUploading) {
			        }


					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.e("onFailure", arg0.toString());
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						try {
							JSONObject resp = new JSONObject(response.result);
							Log.e("onSuccess", resp.toString());
	
							if (200 == resp.getInt("resultCode")) {
								
								HashMap<String,SysVideo> map = getsysvideolist(response.result); 
								SysVideo syslo = map.get(LOGO);
								
//								Log.e("????????syslo", ""+share.getInt(LOGO, -1)+"/"+ Integer.parseInt(syslo.getCode()));
								if(share.getInt(LOGO, -1)< Integer.parseInt(syslo.getCode()))
								new UpdateSystemVideoThread(syslo.getUrl(),"logo.img",Integer.parseInt(syslo.getCode())).start();
								
								SysVideo sysmo = map.get(LOGO);
//								Log.e("????????sysmo", ""+share.getInt(LOGO, -1)+"/"+ Integer.parseInt(sysmo.getCode()));
								if(share.getInt(MOVIE, -1)< Integer.parseInt(sysmo.getCode()))
								new UpdateSystemVideoThread(sysmo.getUrl(),"movie.img",Integer.parseInt(syslo.getCode())).start();
								
								SysVideo sysan = map.get(LOGO);
//								Log.e("????????sysan", ""+share.getInt(LOGO, -1)+"/"+ Integer.parseInt(sysan.getCode()));
								if(share.getInt(ANIMATION, -1)< Integer.parseInt(sysan.getCode()))
								new UpdateSystemVideoThread(sysan.getUrl(),"animation.zip",Integer.parseInt(syslo.getCode())).start();
//								
							} else {

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
			});
	}
	
	/*
	 * ??????????
	 * */
	private HashMap<String,SysVideo> getsysvideolist(String obj){
		HashMap<String,SysVideo> map = new HashMap<>();
		
		try {
			JSONObject json = new JSONObject(obj);
			JSONArray array = json.getJSONArray("data");
			for(int i =0;i<array.length();i++){
				SysVideo sys = new SysVideo();
				JSONObject been = (JSONObject) array.get(i);
				if(been.has("code"))
					sys.setCode(been.getString("code"));
				if(been.has("content"))
					sys.setContent(been.getString("content"));
				if(been.has("createTime"))
					sys.setCreatTime(been.getString("createTime"));
				if(been.has("url"))
					sys.setUrl(been.getString("url"));
				if(been.has("version"))
					sys.setVersion(been.getString("version"));
				map.put(sys.getVersion(), sys);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return map;
	}
	
	/*????*/
	public class UpdateSystemVideoThread extends Thread{
		
		String url;
		String name;
		int version;
		public UpdateSystemVideoThread(String url,String name,int version){
			this.url = url;
			this.name = name;
			this.version = version;
//			Log.e("????????", url+"/"+PATH+name);
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
		
			HttpUtils http = new HttpUtils();
			File file = new File(PATH+name);
			if(file.exists())file.delete();
			http.download(url,
			    PATH+name,true,true, 
			    new RequestCallBack<File>() {
			        @Override
			        public void onStart() {
			            //testTextView.setText("conn...");
			        }

			        @Override
			        public void onLoading(long total, long current, boolean isUploading) {
			            //testTextView.setText(current + "/" + total);
			        	Log.e("onLoading", total+"/"+current);
			        }

			        @Override
			        public void onFailure(HttpException error, String msg) {
			           // testTextView.setText(msg);
			        	Log.e("onFailure", error.toString());
			        }

				
					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						// TODO Auto-generated method stub
						
						Log.e("onSuccess", arg0.toString());
						creatTXT(name,version);
					}
			});
		}
		}
	
	
	private void creatTXT(String name,int version){
		String namepath = null;
		Editor ed = share.edit();
		switch (name) {
		case "logo.img":
			namepath = "logo.txt";
			ed.putInt(LOGO, version);
			break;
		case "movie.img":
			
			namepath = "movie.txt";
			ed.putInt(MOVIE, version);
			break;
		case "animation.zip":
			
			namepath = "animation.txt";
			ed.putInt(ANIMATION, version);
			break;
		}
		ed.commit();
		File folder = new File(PATH);
		if(!folder.exists()){
		   folder.mkdirs();   

		}

		File file = new File(folder,namepath);
		FileOutputStream fou;
		try {
			fou = new FileOutputStream(file);
			fou.write("true".getBytes());
			fou.flush();
			fou.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
