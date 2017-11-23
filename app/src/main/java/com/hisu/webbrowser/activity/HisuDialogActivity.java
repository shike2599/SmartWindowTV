package com.hisu.webbrowser.activity;

import com.hisu.webbrowser.MainActivity;
import com.hisu.webbrowser.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.Toast;

public class HisuDialogActivity extends Activity{

	private boolean back = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hisu_dialog);
		
		
		Intent intent = new Intent();
		 ComponentName localComponentName = new
		 ComponentName("com.hisu.dvbplayer",
		 "com.hisu.dvbplayer.DVBPlayerActivity");
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 intent.setComponent(localComponentName);
		 getApplication().startActivity(intent);
		
		
//		new AlertDialog.Builder(HisuDialogActivity.this)
//		.setTitle("��ʾ")
//		.setMessage("���粻�ȶ��������ѶϿ�")
//		.setNeutralButton("��������", new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog,
//					int which) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//				new SystemScript(null, getApplication(), HisuDialogActivity.this)
//						.openApp("com.android.settings");
//			}
//		})
//		.setNegativeButton("����",
//				new DialogInterface.OnClickListener() {
//					public void onClick(
//							DialogInterface dialog,
//							int whichButton) {
//						if (!BrowserUtil.isNetworkConnected(getApplication())) {
//							Toast.makeText(getApplication(), "��������", Toast.LENGTH_SHORT).show();
//							return;
//						}
//						startActivity(new Intent(HisuDialogActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
//						finish();
//					}
//				}).create().show();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(back){
			startActivity(new Intent(HisuDialogActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
			
			
			finish();
		}
		back = true;
	}
}
