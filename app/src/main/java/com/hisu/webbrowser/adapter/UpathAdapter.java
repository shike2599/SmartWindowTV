package com.hisu.webbrowser.adapter;

import java.io.File;







import com.hisu.webbrowser.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UpathAdapter extends BaseAdapter{
	
	String[] names;
	File[] files;
	Activity context;
	 public UpathAdapter(String[] names,File[] files,Activity context) {
		// TODO Auto-generated constructor stub
		 this.names = names;
		 this.files = files;
		 this.context = context;
	 }
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return names == null ? 0 : names.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return names == null ? null : names[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		viewhodle hodler;
			
		if(convertView == null){
			
			hodler = new viewhodle();
			convertView = context.getLayoutInflater().inflate(R.layout.item_activity_u_path, null);
			hodler.image = (ImageView) convertView.findViewById(R.id.imageview);
			hodler.textview = (TextView) convertView.findViewById(R.id.textview);
			convertView.setTag(hodler);
		}else{
			hodler = (viewhodle) convertView.getTag();
		}
		
		hodler.image.setImageBitmap(getVideoThumbnail(files[position].getPath()));
		hodler.textview.setText(names[position]);
		
		
		return convertView;
	}
	
	/*
	 * ��Ƶ����ͼ
	 * */
	public static Bitmap getVideoThumbnail(String videoPath) {
		  MediaMetadataRetriever media =new MediaMetadataRetriever();
		  media.setDataSource(videoPath);
		  Bitmap bitmap = media.getFrameAtTime();
		  return bitmap;
		}
	
	public class viewhodle{
		ImageView image;
		TextView textview;
		
	}
	
	
}
