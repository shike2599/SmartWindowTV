package com.hisu.webbrowser.activity;

import com.hisu.webbrowser.MainActivity;
import com.hisu.webbrowser.R;
import com.hisu.webbrowser.js.SystemScript;
import com.hisu.webbrowser.util.BrowserUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class DialogActivity extends Activity implements OnClickListener{
	
	SystemScript sys;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		
		initview();
	}

	private void initview() {
		// TODO Auto-generated method stub
		findViewById(R.id.lau_menu1).setOnClickListener(this);
		findViewById(R.id.lau_menu2).setOnClickListener(this);
		findViewById(R.id.lau_menu3).setOnClickListener(this);
		findViewById(R.id.lau_menu4).setOnClickListener(this);
		findViewById(R.id.lau_menu5).setOnClickListener(this);
		findViewById(R.id.lau_menu6).setOnClickListener(this);
		findViewById(R.id.lau_menu7).setOnClickListener(this);
		findViewById(R.id.lau_menu8).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.lau_menu1:
			startActivty("com.dvb.live");
			break;
		case R.id.lau_menu2:
			break;
		case R.id.lau_menu3:
			
			break;
		case R.id.lau_menu4:
			if (!BrowserUtil.isNetworkConnected(getApplication())) {
				Toast.makeText(getApplication(), "请检查网络", Toast.LENGTH_SHORT).show();
				return;
			}
			startActivity(new Intent(DialogActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
			finish();
			break;
		case R.id.lau_menu5:
			
			break;
		case R.id.lau_menu6:
			
			break;
		case R.id.lau_menu7:
			startActivty("com.ejian");
			
			break;
		case R.id.lau_menu8:
//			startActivty("com.fc.setting");
			startActivty("com.android.settings");
			
			break;
		}
	}
	
	private void startActivty(String pag){
		if(sys == null){
			sys = new SystemScript(getApplication());
		}
		
		sys.openApp(pag.trim());
	}
	
	
}
