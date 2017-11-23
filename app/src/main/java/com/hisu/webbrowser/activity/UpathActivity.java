package com.hisu.webbrowser.activity;

import java.io.File;

import com.hisu.webbrowser.R;
import com.hisu.webbrowser.adapter.UpathAdapter;
import com.hisu.webbrowser.util.DataUtil;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class UpathActivity extends Activity{
	
	private String[] names;
	private File[] files;
	private UpathAdapter adapter;
	private GridView gridview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_u_path);
		initdata();
		initview();
		
	}
	 private void initview() {
		// TODO Auto-generated method stub
		 gridview = (GridView) findViewById(R.id.gridview);
		 gridview.setAdapter(adapter);
		 gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UpathActivity.this,VideoViewActivity.class);
				intent.putExtra("path", files[position].getPath());
				Log.e("path", files[position].getPath());
                startActivity(intent);
			}
		});
	}
	private void initdata() {
		// TODO Auto-generated method stub
		 File file = new File(new DataUtil(getApplication()).getsharepreferences("u_path")+"/hisu");
		 names = file.list();
		 files = file.listFiles();
		 Log.e("path",file.getPath());
		 Log.e("name",""+names.length);
		 Log.e("files",""+files.length);
		 if(adapter == null){
			 adapter = new UpathAdapter(names, files, UpathActivity.this);
		 }
		 
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	 
	 @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
