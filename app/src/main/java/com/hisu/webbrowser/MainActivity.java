	package com.hisu.webbrowser;

	import android.app.Activity;
	import android.app.AlertDialog;
	import android.app.AlertDialog.Builder;
	import android.content.BroadcastReceiver;
	import android.content.ComponentName;
	import android.content.Context;
	import android.content.DialogInterface;
	import android.content.Intent;
	import android.content.IntentFilter;
	import android.os.Build;
	import android.os.Bundle;
	import android.os.Environment;
	import android.os.Handler;
	import android.os.Message;
	import android.os.SystemProperties;
	import android.support.annotation.RequiresApi;
	import android.text.TextUtils;
	import android.util.Log;
	import android.view.KeyEvent;
	import android.view.LayoutInflater;
	import android.view.SurfaceView;
	import android.view.View;
	import android.view.View.OnClickListener;
	import android.view.ViewGroup;
	import android.view.Window;
	import android.view.WindowManager;
	import android.webkit.WebView;
	import android.widget.AdapterView;
	import android.widget.AdapterView.OnItemClickListener;
	import android.widget.ArrayAdapter;
	import android.widget.Button;
	import android.widget.EditText;
	import android.widget.GridView;
	import android.widget.ImageView;
	import android.widget.LinearLayout;
	import android.widget.RelativeLayout;
	import android.widget.TextView;
	import android.widget.Toast;

	import com.hisu.webbrowser.js.SystemScript;
	import com.hisu.webbrowser.manager.UpdateManager;
	import com.hisu.webbrowser.manager.UpdateVideoManager;
	import com.hisu.webbrowser.mode.WebInterface;
	import com.hisu.webbrowser.player.WebPlayer;
	import com.hisu.webbrowser.util.BrowserEvent;
	import com.hisu.webbrowser.util.DataUtil;
	import com.hisu.webbrowser.util.ToolsUtil;

	import java.io.File;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.Random;
	import java.util.Timer;
	import java.util.TimerTask;

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

	private int handler = 1;
	private boolean ShutDown = false;
	WebView webView;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (ToolsUtil.isNetworkConnected(mContext)) {
					String mac = ToolsUtil.getLocalMacAddressFromIp(mContext);
					
//					Toast.makeText(getApplication(), "mac" + mac,Toast.LENGTH_SHORT).show();
					
//					String mac = android.os.SystemProperties.get("persist.sys.mmcp.smartcard");//数码盒子ca
//					String mac = android.os.SystemProperties.get("persist.sys.ca.card_id");//茁壮盒子ca
//					String mac = "8420610621539759";
					if("".equals(mac)||mac == null ){
						Toast.makeText(getApplication(), "CA卡获取失败",Toast.LENGTH_SHORT).show();
						return;
					}
					Log.d(TAG, " mac = " + mac);
					if (!TextUtils.isEmpty(mac)) {
						// 检测本地视频更新
						UpdateVideoManager uvm = new UpdateVideoManager(
								mContext, mHandler);
						uvm.checkUpdate(mac);
//						sendEmptyMessageDelayed(1, 2000);
						break;
					}
				}
					 
					timer += 5;
					if (timer >= 10) {
						isSetting = true;
						showPwdDialog();
						Toast.makeText(mContext, "网络异常，请检查网络", Toast.LENGTH_LONG).show();
					finish();    //襄阳项目使用
					} else {
						Toast.makeText(mContext, "请检查网络连接！", Toast.LENGTH_SHORT).show();
						sendEmptyMessageDelayed(handler, 2000);
					}
				

				break;
			case 1:
				// 检测版本更新
				
				UpdateManager um = new UpdateManager(mContext,
						MainActivity.this);
				um.checkUpdate();

				Random rnd = new Random();
				Log.d(TAG, "rnd = " + rnd);
				String url = mUrl + (mUrl.indexOf("?") > -1 ? "&" : "?")
						+ "Sbory_Math_Random=" + rnd.nextInt();
				// url += "&stbMac=52-54-4F-FF-FF-F6";
				Log.d(TAG, " mUrl = " + url);
			
				mBrowser.loadUrl(url);
				// mTimer.schedule(mTask, 10);
//				sendEmptyMessageDelayed(2, 2000);

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

	private int mLoadType = -1;
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		SystemScript.group_ID = SystemProperties.get("sys.auth.groupID");
        Log.d(TAG,"SystemScript.group_ID === "+SystemScript.group_ID);

  		defaultImage = (ImageView) findViewById(R.id.defaultImage);
		mLoadType = getIntent().getIntExtra("load_type", -1);

		SystemScript.LOAD_TYPE = mLoadType;

		Log.d(TAG,"SystemScript.LOAD_TYPE === "+SystemScript.LOAD_TYPE);
		/*判断是否有修改的启动IP
		 * */
		DataUtil data = new DataUtil(this);
//		if(data.getsharepreferences(http) != null){
//			WebInterface.URL_PREFIX = data.getsharepreferences(http);
//			Toast.makeText(getApplication(), WebInterface.URL_PREFIX,Toast.LENGTH_LONG).show();
//		}
//		initbackground();
		//		new SystemScript(mHandler, getApplication(), MainActivity.this).openVLC("http://192.168.1.168:80/hdmi_ext");
		
		mUrl = WebInterface.DEFAULT_HOME_PAGE();

		Intent intent = getIntent();
		
		if(intent != null && intent.getIntExtra("type", -1) != -1){
			mUrl = mUrl + "?jump_type=" + intent.getIntExtra("type", -1);
			Log.d(TAG,"Intent-type --- mUrl==="+mUrl);
		}

		if (intent != null && intent.getStringExtra("url") != null) {
			mUrl = intent.getStringExtra("url");
			Log.d(TAG,"Intent-url --- mUrl==="+mUrl);
		}
		

		mUrl = savedInstanceState == null
				|| !savedInstanceState.containsKey(CURRENT_URL) ? mUrl
				: savedInstanceState.getString(CURRENT_URL);
		Log.d(TAG,"savedInstanceState-url --- mUrl==="+mUrl);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		int width = this.getWindowManager().getDefaultDisplay().getWidth();
		int height = this.getWindowManager().getDefaultDisplay().getHeight();
		Log.d(TAG, "mWidth " + width + " mHeight " + height);

		RelativeLayout dvbPlayLayout = (RelativeLayout) findViewById(R.id.dvb_layout);

		WebPlayer webPlayer = new WebPlayer(this,surfaceView, dvbPlayLayout,width, height);

		webView = (WebView) findViewById(R.id.htmlview);
//		 webView.loadUrl(mUrl);
//		 webView.setVisibility(View.GONE);

		mBrowser = new WebBrowser(webView, MainActivity.this, webPlayer,
				defaultImage, MainActivity.this);
		// boolean newEnter = savedInstanceState == null ||
		// !savedInstanceState.containsKey(CURRENT_URL);
		deleteFilesByDirectory(mContext.getCacheDir());
		cleanExternalCache(mContext);
//		Toast.makeText(mContext, "正在连接网络，请稍候...", Toast.LENGTH_LONG).show();

		/*
		 * 0 下载本地视频 1 不下载
		 */
		mHandler.sendEmptyMessageDelayed(handler, 2000);
//		mHandler.sendEmptyMessageDelayed(handler, 500);
//		mHandler.sendEmptyMessage(handler);
		mBrowser.clearCache();
		mBrowser.clearWebViewCache();
		// mBrowser.loadUrl(mUrl);
	}
	
	/*
	 * 更换背景图片
	 * */
	private void initbackground() {
		// TODO Auto-generated method stub
	}
	protected void showPwdDialog() {
		// TODO Auto-generated method stub
		ShutDown = true;
		Builder builder = new Builder(mContext);
		View view = View.inflate(mContext, R.layout.dialog_pwd, null);
		builder.setView(view);
		mDialog = builder.create();
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);

		Button btn_yes = (Button) view.findViewById(R.id.btn_yes);
//		final Button btn_input = (Button) view.findViewById(R.id.btn_input);
//		Button btn_no = (Button) view.findViewById(R.id.btn_no);
//		final TextView tv_tip = (TextView) view.findViewById(R.id.tv_tip);

		/*
		 * 启动电视
		 */
//		Button btn_toTV = (Button) view.findViewById(R.id.gototv);
//		btn_toTV.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = mContext.getPackageManager()
//						.getLaunchIntentForPackage(getResources().getString(R.string.system_dvb));
//				startActivity(intent);
//				mDialog.dismiss();
//			}
//		});

		/*
		 * 常德
		 */

		btn_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				String inputString = btn_input.getText().toString();
//				if (!"****".equals(inputString)) {
//					tv_tip.setText("工程密码不正确");
//					Message msg = new Message();
//					msg.what = 3;
//					msg.obj = tv_tip;
//					mHandler.sendMessageDelayed(msg, 1000);
//				} else {
//					String packageName = getString(R.string.system_set_package);
//					Intent intent = mContext.getPackageManager()
//							.getLaunchIntentForPackage(packageName);
//					try {
//						startActivity(intent);
//						isSetting = true;
//						mDialog.dismiss();
//					} catch (ActivityNotFoundException ane) {
//						Log.d(TAG,
//								"SW_OPEN_APP ============SYSTEM_SETTINGS ActivityNotFoundException========");
////						Toast.makeText(mContext, "未发现设置包" + packageName,Toast.LENGTH_SHORT).show();
//						isSetting = false;
//					}
//				}
				MainActivity.this.finish();
			}
		});
//		btn_no.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				timer = 0;
//				mHandler.sendEmptyMessageDelayed(handler, 5000);
//				mDialog.dismiss();
//			}
//		});
//		btn_input.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				String inputString = btn_input.getText().toString();
//				btn_input.setText(inputString + "*");
//			}
//		});
		mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
//					String inputString = btn_input.getText().toString();
//					btn_input.setText(inputString.substring(0,
//							inputString.length() - 1));
					return true;
				} else {
					return false; // 默认返回 false
				}
			}
		});
//		btn_input.requestFocus();
		mDialog.show();// 去除黑色背景
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
	 * *删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理  * *
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
	 * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * *
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
			mHandler.sendEmptyMessageDelayed(handler, 2000);
//			mHandler.sendEmptyMessage(handler);
		}
		/*
		 * home键广播
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
		 * ͣ停止HOME 广播接受
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
		Log.e("键值", "" + keyCode+"event:"+event.getKeyCode());
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			
//			 if (keyCode == KeyEvent.KEYCODE_BACK && mBrowser.canGoBack()) {  
//			        // 返回上一页面
//				 mBrowser.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  
//				 mBrowser.goBack();  
//			        return true;  
//			    }  
//			
			if(ShutDown){
				finish();
				return false;
			}
			if(SystemScript.LOAD_TYPE != -1){
				return true;
			}
//			Log.d(TAG, "onKeyDown KEYCODE_BACK");

		case KeyEvent.KEYCODE_HOME:
//			 mBrowser.destroy();
//			 android.os.Process.killProcess(android.os.Process.myPid());
				 break;
				 /*
				  * 和系统power冲突 导致无效
				  * */
//		case KeyEvent.KEYCODE_POWER:
//			try {
//				/*
//				 * reboot 重启
//				 * shutdown 关机
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
		// TODO Auto-generated method st ub
		Log.e(TAG, "event:"+event.getKeyCode());
		// back ˫��Ӧ
//		if (event.getAction() != KeyEvent.ACTION_DOWN)
//			return super.dispatchKeyEvent(event);
        if(event.getAction() == KeyEvent.ACTION_DOWN){
			int	keyCode = event.getKeyCode();
			if (keyCode == BrowserEvent.KeyEvent.KEY_EXIT){
				mBrowser.notifyEvent(BrowserEvent.KeyEvent.KEY_EXIT);
				return true;
			}

			if(keyCode == KeyEvent.KEYCODE_BACK) {
				if (mLoadType != -1){
					mBrowser.notifyEvent(BrowserEvent.KeyEvent.KEY_BACK);
//					android.os.Process.killProcess(android.os.Process.myPid());
				}else{
					mBrowser.notifyEvent(BrowserEvent.KeyEvent.KEY_BACK);
				}
				return true;
			}

//		if (keyCode == 4) {
//			By2Click();
//		}
			if (isExit) {
				putpw(keyCode);
			}
		}
//	    int	keyCode = event.getKeyCode();
//		if (keyCode == BrowserEvent.KeyEvent.KEY_EXIT){
//			mBrowser.notifyEvent(BrowserEvent.KeyEvent.KEY_EXIT);
//			return true;
//		}
//
//		if(keyCode == KeyEvent.KEYCODE_BACK) {
//			if (mLoadType != -1){
//				android.os.Process.killProcess(android.os.Process.myPid());
//			}else{
//				mBrowser.notifyEvent(BrowserEvent.KeyEvent.KEY_BACK);
//			}
//			return true;
//		}
//
////		if (keyCode == 4) {
////			By2Click();
////		}
//		if (isExit) {
//			putpw(keyCode);
//		}
		
		return super.dispatchKeyEvent(event);
	
	}
	
	/*
	 * HOME 的广播
	 * */
	private HomeKeyEventBroadCastReceiver homeKeyEventReceiver; 
	public class HomeKeyEventBroadCastReceiver extends BroadcastReceiver{  
	    @Override  
	    public void onReceive(Context context, Intent intent) {  
	        //do what you want  
	    	Log.e("home","HOME");
	    	mBrowser.notifyEvent(BrowserEvent.KeyEvent.KEY_HOMEPAGE);
	    	finish();
	    }  
	} 


	/**
	 * 双击输入密码
	 */

	private List<Integer> mlist = new ArrayList<Integer>();
	private static Boolean isExit = false;

	private void putpw(int i) {
		mlist.add(i);
	}

	private void By2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
					mlist.clear();
				}
			}, 5000); // 如果5秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

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
			}
			mlist.clear();
		}
	}
}
