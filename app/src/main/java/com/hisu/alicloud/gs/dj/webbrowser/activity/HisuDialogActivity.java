package com.hisu.alicloud.gs.dj.webbrowser.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.hisu.alicloud.gs.dj.webbrowser.MainActivity;
import com.hisu.alicloud.gs.dj.webbrowser.R;
import com.hisu.alicloud.gs.dj.webbrowser.js.SystemScript;
import com.hisu.alicloud.gs.dj.webbrowser.util.BrowserUtil;

public class HisuDialogActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hisu_dialog);
		
		new AlertDialog.Builder(HisuDialogActivity.this)
		.setTitle("提示ʾ")
		.setMessage("网络不稳定，或者已断开")
		.setNeutralButton("网络设置", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				new SystemScript(null, getApplication(), HisuDialogActivity.this)
						.openApp("com.android.settings");
			}
		})
		.setNegativeButton("重试",
				new OnClickListener() {
					public void onClick(
							DialogInterface dialog,
							int whichButton) {
						if (!BrowserUtil.isNetworkConnected(getApplication())) {
							Toast.makeText(getApplication(), "请检查网络", Toast.LENGTH_SHORT).show();
							return;
						}
						startActivity(new Intent(HisuDialogActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
						finish();
					}
				}).create().show();
	}
}
