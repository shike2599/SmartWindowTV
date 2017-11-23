	package com.hisu.webbrowser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import com.hisu.webbrowser.R;
import com.hisu.webbrowser.manager.UpdateManager;
import com.hisu.webbrowser.manager.UpdateVideoManager;
import com.hisu.webbrowser.mode.WebInterface;
import com.hisu.webbrowser.player.WebPlayer;
import com.hisu.webbrowser.util.BrowserEvent;
import com.hisu.webbrowser.util.DataUtil;
import com.hisu.webbrowser.util.LLUtil;
import com.hisu.webbrowser.util.ToolsUtil;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	private static final String TAG = "SmartHotelViewApp";
	private static final String CURRENT_URL = "webViewCurrentUrl";
	private WebBrowser mBrowser = null;
	private Context mContext = this;
	private String mUrl;
	private int timer = 0;
	private boolean isSetting = false;
	private static final String http= "HTTP";
	protected AlertDialog mDialog;
	ImageView defaultImage;

	private int handler = 0;
	private boolean ShutDown = false;
	WebView webView;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				
				//������
				Random rnd = new Random();
				String url = mUrl + (mUrl.indexOf("?") > -1 ? "&" : "?")
						+ "Sbory_Math_Random=" + rnd.nextInt();
				mBrowser.loadUrl(url);
				
				defaultImage.clearAnimation();
				defaultImage.setVisibility(View.INVISIBLE);
				//������
				
				
				if (ToolsUtil.isNetworkConnected(mContext)) {
					String mac = ToolsUtil.getLocalMacAddressFromIp(mContext);
					Log.e("MAC", mac);
					if("".equals(mac)||mac == null ){
						Toast.makeText(getApplication(), "CA卡获取失败",Toast.LENGTH_SHORT);
						return;
					}
					Log.d(TAG, " mac = " + mac);
					if (!TextUtils.isEmpty(mac)) {
						// ��Ȿ����Ƶ����
						UpdateVideoManager uvm = new UpdateVideoManager(
								mContext, mHandler);
						uvm.checkUpdate(mac);     
						break;
					}
				}

				timer += 5;
				if (timer >= 15) {
					isSetting = true;
					startdvb();
					Toast.makeText(mContext, "网络异常，请检查网络", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(mContext, "请检查网络连接！", Toast.LENGTH_SHORT).show();
//					sendEmptyMessageDelayed(handler, 5000);
				}
				break;
			case 1:
				// ���汾����
				UpdateManager um = new UpdateManager(mContext,
						MainActivity.this);
				um.checkUpdate();

				Random rnd2 = new Random();
				String url2 = mUrl + (mUrl.indexOf("?") > -1 ? "&" : "?")
						+ "Sbory_Math_Random=" + rnd2.nextInt();
				// url += "&stbMac=52-54-4F-FF-FF-F6";
//				mBrowser.loadUrl(url2);   //����
				// mTimer.schedule(mTask, 10);
				Log.d(TAG, " mUrl = " + mUrl);
//				sendEmptyMessageDelayed(2, 2000);//����

				break;
			case 2:

				defaultImage.clearAnimation();
				defaultImage.setVisibility(View.INVISIBLE);
				break;
			case 3:
				TextView tv_tip = (TextView) msg.obj;
				tv_tip.setText("进系统设置前请输入工程密码");
				break;
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		defaultImage = (ImageView) findViewById(R.id.defaultImage);
		
		/*�ж��Ƿ����޸ĵ�����IP
		 * */
		DataUtil data = new DataUtil(this);
//		if(data.getsharepreferences(http) != null){
////			Log.e("����",data.getsharepreferences(http));
//			WebInterface.URL_PREFIX = data.getsharepreferences(http);
////			Toast.makeText(getApplication(), WebInterface.URL_PREFIX,Toast.LENGTH_LONG).show();
//		}
		//		new SystemScript(mHandler, getApplication(), MainActivity.this).openVLC("http://192.168.1.168:80/hdmi_ext");
		if (WebInterface.DOWNOPENSYSVIDEO){
			
			new LLUtil(MainActivity.this).checkSystemVideo();
		}
		
		mUrl = WebInterface.DEFAULT_HOME_PAGE();

		Intent intent = getIntent();
		if (intent != null && intent.getStringExtra("url") != null) {
			mUrl = intent.getStringExtra("url");
		}
		mUrl = savedInstanceState == null
				|| !savedInstanceState.containsKey(CURRENT_URL) ? mUrl
				: savedInstanceState.getString(CURRENT_URL);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		int width = this.getWindowManager().getDefaultDisplay().getWidth();
		int height = this.getWindowManager().getDefaultDisplay().getHeight();
		Log.d(TAG, "mWidth " + width + " mHeight " + height);

		WebPlayer webPlayer = new WebPlayer(surfaceView, width, height);

		webView = (WebView) findViewById(R.id.htmlview);
		// webView.loadUrl(mUrl);
		// webView.setVisibility(View.GONE);

		mBrowser = new WebBrowser(webView, MainActivity.this, webPlayer,
				defaultImage, MainActivity.this);

		// boolean newEnter = savedInstanceState == null ||
		// !savedInstanceState.containsKey(CURRENT_URL);
		deleteFilesByDirectory(mContext.getCacheDir());
		cleanExternalCache(mContext);
//		Toast.makeText(mContext, "�����������磬���Ժ�...", Toast.LENGTH_LONG).show();

		/*
		 * 0 ���ر�����Ƶ 1 ������
		 */
		mHandler.sendEmptyMessageDelayed(handler, 5000);
		mBrowser.clearCache();
		mBrowser.clearWebViewCache();
		// mBrowser.loadUrl(mUrl);
	}
	
	/*
	 * ��������ͼƬ
	 * */
	private void initbackground() {
		// TODO Auto-generated method stub
	}
	
	private void startdvb(){
//		Intent intent = getPackageManager().getLaunchIntentForPackage("com.hisu.dvbplayer/com.hisu.dvbplayer.DVBPlayerActivity");
		Intent intent = new Intent();
		 ComponentName localComponentName = new
		 ComponentName("com.hisu.dvbplayer",
		 "com.hisu.dvbplayer.DVBPlayerActivity");
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 intent.setComponent(localComponentName);
		 getApplication().startActivity(intent);
	}
	protected void showPwdDialog() {
		// TODO Auto-generated method stub
		Builder builder = new Builder(mContext);
		View view = View.inflate(mContext, R.layout.dialog_pwd, null);
		builder.setView(view);
		mDialog = builder.create();
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);

		Button btn_yes = (Button) view.findViewById(R.id.btn_yes);
		final Button btn_input = (Button) view.findViewById(R.id.btn_input);
		Button btn_no = (Button) view.findViewById(R.id.btn_no);
		final TextView tv_tip = (TextView) view.findViewById(R.id.tv_tip);

		/*
		 * ��������
		 */
		Button btn_toTV = (Button) view.findViewById(R.id.gototv);
		btn_toTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = mContext.getPackageManager()
						.getLaunchIntentForPackage(getResources().getString(R.string.system_dvb));
				startActivity(intent);
				mDialog.dismiss();
			}
		});

		/*
		 * ����
		 */

		btn_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String inputString = btn_input.getText().toString();
				if (!"****".equals(inputString)) {
					tv_tip.setText("工程密码不正确");
					Message msg = new Message();
					msg.what = 3;
					msg.obj = tv_tip;
					mHandler.sendMessageDelayed(msg, 1000);
				} else {
					String packageName = getString(R.string.system_set_package);
					Intent intent = mContext.getPackageManager()
							.getLaunchIntentForPackage(packageName);
					try {
						startActivity(intent);
						isSetting = true;
						mDialog.dismiss();
					} catch (ActivityNotFoundException ane) {
						Log.d(TAG,
								"SW_OPEN_APP ============SYSTEM_SETTINGS ActivityNotFoundException========");
//						Toast.makeText(mContext, "δ�������ð�" + packageName,Toast.LENGTH_SHORT).show();
						isSetting = false;
					}
				}
			}
		});
		btn_no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				timer = 0;
				mHandler.sendEmptyMessageDelayed(handler, 5000);
				mDialog.dismiss();
			}
		});
		btn_input.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String inputString = btn_input.getText().toString();
				btn_input.setText(inputString + "*");
			}
		});
		mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					String inputString = btn_input.getText().toString();
					btn_input.setText(inputString.substring(0,
							inputString.length() - 1));
					return true;
				} else {
					return false; // Ĭ�Ϸ��� false
				}
			}
		});
		btn_input.requestFocus();
		mDialog.show();// ȥ����ɫ����
		try {
			ViewGroup viewGroup1 = (ViewGroup) view.getParent();
			if (viewGroup1 != null) {
				viewGroup1.setBackgroundResource(android.R.color.transparent);

				ViewGroup viewGroup2 = (ViewGroup) viewGroup1.getParent();
				if (viewGroup2 != null) {
					viewGroup2
							.setBackgroundResource(android.R.color.transparent);
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * *
	 * 
	 * @param directory
	 * */
	private void deleteFilesByDirectory(File directory) {
		Log.e(TAG,
				"deleteFilesByDirectory cacheDir path="
						+ directory.getAbsolutePath());
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				Log.i(TAG, "delete file path=" + item.getAbsolutePath());
				try {
					item.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * * ����ⲿcache�µ�����(/mnt/sdcard/android/data/com.xxx.xxx/cache) * *
	 * 
	 * @param context
	 */
	public void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e(TAG, "onPause ========================");
		mBrowser.notifyEvent(BrowserEvent.BROWSER_PAUSE);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		String url = mBrowser.getCurrentUrl();
		Log.e(TAG, "onResume ========================" + url);
		if (!TextUtils.isEmpty(url)) {
			mBrowser.notifyEvent(BrowserEvent.BROWSER_RESUME);
		}
		if (isSetting) {
			timer = 0;
			mHandler.sendEmptyMessageDelayed(handler, 5000);
		}
		/*
		 * home���㲥
		 * */
		homeKeyEventReceiver = new HomeKeyEventBroadCastReceiver();  
	     registerReceiver(homeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)); 
		
		super.onResume();
	}
	
	public void BrowserDestroy(){
		mBrowser.destroy();
	}
	
	@Override
	public void onDestroy() {
		mBrowser.destroy();
		/*
		 * ֹͣHOME �㲥����
		 * */
		unregisterReceiver(homeKeyEventReceiver); 
		super.onDestroy();
		Log.d(TAG, "onDestroy ========================");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Store the game state
		String url = mBrowser.getCurrentUrl();
		if (url != null) {
			Log.d(TAG, url);
			outState.putString(CURRENT_URL, url);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("键值ֵ", "" + keyCode+"event:"+event.getKeyCode());
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:

//			Log.d(TAG, "onKeyDown KEYCODE_BACK");
			return false;
		case KeyEvent.KEYCODE_HOME:
//			 mBrowser.destroy();
//			 android.os.Process.killProcess(android.os.Process.myPid());
				 break;
				 /*
				  * ��ϵͳpower��ͻ ������Ч
				  * */
//		case KeyEvent.KEYCODE_POWER:
//			try {
//				/*
//				 * reboot ���� 
//				 * shutdown �ػ�
//				 * */
//				Runtime.getRuntime().exec("su -c \"/system/bin/shutdown\"");
//				ShutDown = true;
//			} catch (IOException e) {
//				
//				e.printStackTrace();
//			}
//			if(!ShutDown){
//				Intent newIntent = new Intent(Intent.ACTION_SHUTDOWN);
//				
//                startActivity(newIntent);
//			}
//			return super.onKeyDown(keyCode, event);

		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		Log.e("键值", "event:"+event.getKeyCode());
		// back ˫��Ӧ
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
		}
	  int	keyCode = event.getKeyCode();
		if (keyCode == 4) {
			By2Click();
		}
		if (isExit) {
			putpw(keyCode);
		}
		
		return super.dispatchKeyEvent(event);
	
	}
	
	/*
	 * HOME �Ĺ㲥
	 * */
	private HomeKeyEventBroadCastReceiver homeKeyEventReceiver; 
	public class HomeKeyEventBroadCastReceiver extends BroadcastReceiver{  
	    @Override  
	    public void onReceive(Context context, Intent intent) {  
	        //do what you want  
	    	Log.e("home","HOME");
	    	mBrowser.notifyEvent(BrowserEvent.KeyEvent.KEY_HOMEPAGE);
	    }  
	} 
	
	/**
	 * ˫����������
	 */

	private List<Integer> mlist = new ArrayList<Integer>();
	private static Boolean isExit = false;

	private void putpw(int i) {
		mlist.add(i);
	}

	private void By2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // ׼���˳�
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // ȡ���˳�
					mlist.clear();
				}
			}, 5000); // ���5������û�а��·��ؼ�����������ʱ��ȡ�����ղ�ִ�е�����

		} else {
			if (mlist.size() > 8) {
//				if (mlist.get(2) == 19 && mlist.get(4) == 21
//						&& mlist.get(6) == 25 && mlist.get(8) == 20
//						&& mlist.get(10) == 21) {
				if (mlist.get(2) ==KeyEvent.KEYCODE_3  && mlist.get(4) == KeyEvent.KEYCODE_3 
						&& mlist.get(6) == KeyEvent.KEYCODE_3  && mlist.get(8) == KeyEvent.KEYCODE_3 
						&& mlist.get(10) == KeyEvent.KEYCODE_3 ) {
					Builder dialog = new Builder(mContext,
							AlertDialog.THEME_DEVICE_DEFAULT_DARK);

					LayoutInflater inflater = (LayoutInflater) mContext
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					LinearLayout layout = (LinearLayout) inflater.inflate(
							R.layout.editpw, null);
					final EditText et_search = (EditText) layout
							.findViewById(R.id.btn_input);
					GridView gridview = (GridView) layout
							.findViewById(R.id.gridview);
					gridview.setAdapter(new ArrayAdapter<>(mContext,
							android.R.layout.simple_expandable_list_item_1,
							mContext.getResources().getStringArray(
									R.array.ipname)));
					gridview.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							et_search.setText(mContext.getResources().getStringArray(R.array.ipurl)[position]);
						}
					});
					dialog.setTitle("提示");
					dialog.setView(layout);
					dialog.setNeutralButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							new DataUtil(mContext).setsharepreferences("HTTP",
									"http://"
											+ et_search.getText().toString()
													.trim());
							Intent intent = mContext.getPackageManager()
									.getLaunchIntentForPackage(
											getBaseContext().getPackageName());
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							mContext.startActivity(intent);
						}

					}).setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();
								}
							});
					dialog.create().show();
				}else if(mlist.get(2) == KeyEvent.KEYCODE_5 && mlist.get(4) == KeyEvent.KEYCODE_9  //5918
						&& mlist.get(6) == KeyEvent.KEYCODE_1 && mlist.get(8) == KeyEvent.KEYCODE_8){
					ComponentName localComponentName = new ComponentName(
							"com.hisu.systemsetting", "com.hisu.systemsetting.SystemUpgradeActivity");
					mContext.startActivity(new Intent().setComponent(localComponentName));
				}
				else if(mlist.get(2) == KeyEvent.KEYCODE_5 && mlist.get(4) == KeyEvent.KEYCODE_6  //5618
						&& mlist.get(6) == KeyEvent.KEYCODE_1 && mlist.get(8) == KeyEvent.KEYCODE_8){
					ComponentName localComponentName = new ComponentName(
					"com.android.hisu.factorytest", "com.hisu.factorytest.factorytest.MainActivity");
					mContext.startActivity(new Intent().setComponent(localComponentName));
				}else if(mlist.get(2) == KeyEvent.KEYCODE_5 && mlist.get(4) == KeyEvent.KEYCODE_3  //5318
						&& mlist.get(6) == KeyEvent.KEYCODE_1 && mlist.get(8) == KeyEvent.KEYCODE_8){
					ComponentName localComponentName = new ComponentName(
							"com.hisu.twins", "com.hisu.twins.ui.activity.MainActivity");
					mContext.startActivity(new Intent().setComponent(localComponentName));
				}
				else if(mlist.get(2) == KeyEvent.KEYCODE_5 && mlist.get(4) == KeyEvent.KEYCODE_5  //5555
						&& mlist.get(6) == KeyEvent.KEYCODE_5 && mlist.get(8) == KeyEvent.KEYCODE_5){
					Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("com.android.settings");
					mContext.startActivity(intent);
				}
				else if(mlist.get(2) == KeyEvent.KEYCODE_1 && mlist.get(4) == KeyEvent.KEYCODE_2  //5555
						&& mlist.get(6) == KeyEvent.KEYCODE_3 && mlist.get(8) == KeyEvent.KEYCODE_4){
					Intent intent = new Intent();
					 ComponentName localComponentName = new
					 ComponentName("com.hisu.dvbplayer",
					 "com.hisu.dvbplayer.DVBPlayerActivity");
					 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 intent.setComponent(localComponentName);
					 getApplication().startActivity(intent);
				}
			}
			mlist.clear();
		}
	}
}
