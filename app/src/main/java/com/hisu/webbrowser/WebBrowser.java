package com.hisu.webbrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import com.hisu.webbrowser.R;
import com.hisu.webbrowser.activity.DialogActivity;
import com.hisu.webbrowser.activity.HisuDialogActivity;
import com.hisu.webbrowser.js.PlayerScript;
import com.hisu.webbrowser.js.SystemScript;
import com.hisu.webbrowser.mode.WebInterface;
import com.hisu.webbrowser.player.WebPlayer;
import com.hisu.webbrowser.util.BrowserEvent;
import com.hisu.webbrowser.util.BrowserMessage;
import com.hisu.webbrowser.util.BrowserUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class WebBrowser {

	final static String TAG = "WebBrowser";
	private WebView mWebView = null;
	private Context mContext = null;
	private WebPlayer mWebPlayer;
	private SystemScript mSystemScript;
	private ImageView mimage;
	private Activity ac;

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("JavascriptInterface")
	public WebBrowser(WebView webview, Context context, WebPlayer player,
			ImageView image, final Activity ac) {
		mWebView = webview;
		mContext = context;
		mWebPlayer = player;

		this.mimage = image;
		this.ac = ac;
		mWebPlayer.setHandler(mHandler);
		// Log.d("SWWebBrowser","mWebView.getLayerType() = "+mWebView.getLayerType());

		mWebView.setBackgroundColor(Color.TRANSPARENT);
		mWebView.getBackground().setAlpha(0);
		// mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);

		// Log.d("SWWebBrowser","mWebView.getLayerType() = "+mWebView.getLayerType());

		mWebView.setFocusable(true);

		mWebView.setInitialScale(100);
		WebSettings webSettings = mWebView.getSettings();
		// ����javaScript�ű�
		webSettings.setJavaScriptEnabled(true);

		// ����localStorage �� sessionStorage
		webSettings.setDomStorageEnabled(true);

		// ����Ӧ�ó��򻺴�
		webSettings.setAppCacheEnabled(true);
		if (BrowserUtil.isNetworkConnected(mContext)) {

			webSettings.setCacheMode(mWebView.getSettings().LOAD_DEFAULT);
		} else {
			webSettings
					.setCacheMode(mWebView.getSettings().LOAD_CACHE_ELSE_NETWORK);
		}

		webSettings.setAppCacheMaxSize(1024 * 1024 * 8);// ���û����С�����������8M
		String appCacheDir = context.getDir("cache", Context.MODE_PRIVATE)
				.getPath();
		webSettings.setAppCachePath(appCacheDir);
		webSettings.setAllowFileAccess(true);

		// ����webDatabase���ݿ�
		String databaseDir = context.getDir("database", Context.MODE_PRIVATE)
				.getPath();
		webSettings.setDatabaseEnabled(true);
		webSettings.setDatabasePath(databaseDir);
		// ���õ���λ
		webSettings.setGeolocationEnabled(true);
		// ���ö�λ�����ݿ�·��
		webSettings.setGeolocationDatabasePath(databaseDir);

		// �����������flash��֧�֣�
		// webSettings.setPluginsEnabled(true);
		webSettings.setRenderPriority(RenderPriority.HIGH);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setAllowUniversalAccessFromFileURLs(true);//����(����������Ҳ�����ô���   �籨�����������д����ɾ��)

		webSettings.setBuiltInZoomControls(true);

		// ����ͼƬ��Ⱦ
		webSettings.setRenderPriority(RenderPriority.HIGH);
		webSettings.setBlockNetworkImage(true);
		webSettings.setLoadsImagesAutomatically(true);
		// ����webView��userAgent
		webSettings.setUserAgentString(mContext
				.getString(R.string.webview_useragent));

		mWebView.requestFocus();
		mWebView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				
		
				// TODO Auto-generated method stub
				boolean ret = false;
				if (event.getAction() == KeyEvent.ACTION_DOWN) {

					/*
					 * Log.d(TAG,"Build.VERSION.RELEASE = "+Build.VERSION.RELEASE
					 * ); Log.d(TAG,"Build.VERSION.SDK = "+Build.VERSION.SDK);
					 * Log.d(TAG,"Build.MODEL = "+Build.MODEL);
					 */
					if (mSystemScript.isPreventDefault(keyCode)) {
						ret = true;
					}
					String bModel = Build.MODEL;

					if ("Full AOSP on godbox".equals(bModel)
							&& Build.VERSION.SDK_INT == 15) {
						if (keyCode != KeyEvent.KEYCODE_DPAD_CENTER) {
							notifyEvent(BrowserUtil.irKey2BrowserKey(keyCode));
						}
						return ret;
					}
					if ("MiBOX_iCNTV".equals(bModel)
							&& Build.VERSION.SDK_INT == 15) {
						notifyEvent(BrowserUtil.irKey2BrowserKey(keyCode));
						return ret;
					}

					if (keyCode == KeyEvent.KEYCODE_BACK) {
						notifyEvent(BrowserUtil.irKey2BrowserKey(keyCode));
					}
				}

				return ret;
			}
		});

		mWebView.setWebViewClient(new WebViewClient() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.webkit.WebViewClient#onPageFinished(android.webkit.WebView
			 * , java.lang.String)
			 */
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
//				mimage.setVisibility(View.GONE);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.webkit.WebViewClient#onReceivedError(android.webkit.WebView
			 * , int, java.lang.String, java.lang.String) ��ҳ������
			 */
			@Override
			public void onReceivedError(final WebView view, int errorCode,
					String description, final String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				if (WebInterface.ERRORACTIVITY) {

					Toast.makeText(mContext, "网络异常，请检查网络", Toast.LENGTH_LONG)
							.show();
					ac.startActivity(new Intent(ac, DialogActivity.class));
					ac.finish();

				} else {
					Toast.makeText(mContext, "网络异常，请检查网络", Toast.LENGTH_LONG)//10.10.7.150:8081
					.show();
					ac.startActivity(new Intent(ac, HisuDialogActivity.class));
					ac.finish();
				}
			}

			// @Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				mWebView.loadUrl(url);
				mWebView.requestFocus();
				return true;
			}

		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int newProgress) {
				if (mHandler != null) {
					Message msg = new Message();
					msg.what = BrowserMessage.SW_BROWSER_PROGRESS_CHANGED;
					msg.getData().putInt("PROGRESS", newProgress);
					mHandler.sendMessage(msg);
				}

				Log.i(TAG, "onProgressChanged progress:" + newProgress);
				if (newProgress < 1) {
					mSystemScript.clearPreventDefalutKeyList();
				} else if (newProgress >= 100) {
					mWebView.getSettings().setBlockNetworkImage(false);
				}
				super.onProgressChanged(view, newProgress);
			}

		});

		// disable the system use the default brower to open url

		mSystemScript = new SystemScript(mHandler, mContext, ac);
		mWebView.addJavascriptInterface(new PlayerScript(mHandler, mWebPlayer,
				mContext), mContext
				.getString(R.string.javascript_interface_media));

		mWebView.addJavascriptInterface(mSystemScript,
				mContext.getString(R.string.javascript_interface_system));

	}

	/**
	 * ���WebView����
	 */
	public void clearWebViewCache() {

		// ����Webview�������ݿ�
		try {
			mContext.deleteDatabase("webview.db");
			mContext.deleteDatabase("webviewCache.db");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// WebView �����ļ�
		File appCacheDir = new File(mContext.getDir("cache",
				Context.MODE_PRIVATE).getPath());
		Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());

		File webviewCacheDir = new File(mContext.getDir("database",
				Context.MODE_PRIVATE).getPath());
		Log.e(TAG, "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

		// ɾ��webview ����Ŀ¼
		if (webviewCacheDir.exists()) {
			deleteFile(webviewCacheDir);
		}
		// ɾ��webview ���� ����Ŀ¼
		if (appCacheDir.exists()) {
			deleteFile(appCacheDir);
		}

		CookieManager cm = CookieManager.getInstance();
		cm.removeSessionCookie();
		cm.removeAllCookie();

		mWebView.clearCache(true);
		mWebView.clearHistory();
		mWebView.clearFormData();
		mWebView.clearMatches();
		mWebView.clearSslPreferences();

	}

	/**
	 * �ݹ�ɾ�� �ļ�/�ļ���
	 * 
	 * @param file
	 */
	public void deleteFile(File file) {

		Log.i(TAG, "delete file path=" + file.getAbsolutePath());
		try {
			if (file.exists()) {
				if (file.isFile()) {
					file.delete();
				} else if (file.isDirectory()) {
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						deleteFile(files[i]);
					}
				}
				file.delete();
			} else {
				Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private WebChromeClient mChromeClient = new WebChromeClient() {
		// private View myView = null;
		public void onProgressChanged(WebView view, int newProgress) {
			if (mHandler != null) {
				Message msg = new Message();
				msg.what = BrowserMessage.SW_BROWSER_PROGRESS_CHANGED;
				msg.getData().putInt("PROGRESS", newProgress);
				mHandler.sendMessage(msg);
			}

			Log.i(TAG, "onProgressChanged progress:" + newProgress);

			if (newProgress >= 100) {
				mWebView.getSettings().setBlockNetworkImage(false);
			}
			super.onProgressChanged(view, newProgress);
		}

		// ���仺�������
		@Override
		public void onReachedMaxAppCacheSize(long spaceNeeded,
				long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
			quotaUpdater.updateQuota(spaceNeeded * 2);
		}

		// �������ݿ����������WebChromeClinet��ʵ�֣�
		@Override
		public void onExceededDatabaseQuota(String url,
				String databaseIdentifier, long currentQuota,
				long estimatedSize, long totalUsedQuota,
				WebStorage.QuotaUpdater quotaUpdater) {

			quotaUpdater.updateQuota(estimatedSize * 2);
		}

		// Android ʹWebView֧��HTML5 Video��ȫ�������ŵķ���
		/*
		 * @Override public void onShowCustomView(View view, CustomViewCallback
		 * callback) { if (myCallback != null) {
		 * myCallback.onCustomViewHidden(); myCallback = null; return; }
		 * 
		 * 
		 * ViewGroup parent = (ViewGroup) mWebView.getParent();
		 * parent.removeView(mWebView); parent.addView(view);
		 * 
		 * myView = view; myCallback = callback; mChromeClient = this; }
		 * 
		 * @Override public void onHideCustomView() { if (myView != null) { if
		 * (myCallback != null) { myCallback.onCustomViewHidden(); myCallback =
		 * null; }
		 * 
		 * ViewGroup parent = (ViewGroup) myView.getParent();
		 * parent.removeView(myView); parent.addView(mWebView); myView = null; }
		 * }
		 */
	};

	public String getCurrentUrl() {
		return mWebView.getUrl();
	}

	public void destroy() {

		mWebPlayer.stop(0);
	}

	public void loadUrl(String url) {
		// Toast.makeText(ac, "���ı������"+url,3000).show();
		mWebView.loadUrl(url);
		mWebView.requestFocus();
	}

	public void reload() {
		mWebView.reload();
	}

	public boolean canGoBack() {
		return mWebView.canGoBack();
	}

	public void goBack() {
		mWebView.goBack();
	}

	
	/*
	 * ��ֵ����ǰ��
	 * */
	public void notifyEvent(int evnetCode) {
		/*
		 * if(evnetCode == 0 || evnetCode == BrowserEvent.KeyEvent.KEY_SELECT)
		 * return;
		 */
		Log.e("javascript", "send event :eventCode = " + evnetCode);
		String url = "javascript:document.onkeydown({which:" + evnetCode
				+ ",keyCode:" + evnetCode + ",charCode:" + evnetCode + "})";
		mWebView.loadUrl(url);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.d(TAG, "handleMessage " + msg.what);
			String param = null;
			if (null != mContext) {
				switch (msg.what) {
				case BrowserMessage.SW_BROWSER_PROGRESS_CHANGED:
					int prog = msg.getData().getInt("PROGRESS");
					((Activity) mContext).setProgress(prog * 100);
					((Activity) mContext).setTitle(mContext
							.getString(R.string.app_name)
							+ " loading..."
							+ prog + "%");
					break;
				case BrowserMessage.SW_MEDIA_CMD_PLAY:
					// getProgressDialog("���ڼ���ӰƬ�����Ժ򡭡�").show();
					param = msg.getData().getString("URL");
					// mWebView.setVisibility(View.GONE);
					mWebPlayer.start(param);
					break;
				case BrowserMessage.SW_MEDIA_CMD_STOP:
					int flag = msg.getData().getInt("flag");
					mWebPlayer.stop(flag);
					break;
				case BrowserMessage.SW_MEDIA_CMD_PAUSE:
					mWebPlayer.pause();
					break;
				case BrowserMessage.SW_MEDIA_CMD_RESUME:
					mWebPlayer.resume();
					break;
				case BrowserMessage.SW_MEDIA_CMD_SEEK:
					int pos = msg.getData().getInt("pos");
					mWebPlayer.seek(pos * 1000);
					break;

				case BrowserMessage.SW_TOAST:
					Toast.makeText(mContext,
							msg.getData().getString("message"),
							Toast.LENGTH_LONG).show();
					break;
				case BrowserMessage.SW_OPEN_URL:
					param = msg.getData().getString("URL");
					loadUrl(param);
					break;
				case BrowserMessage.SW_CMD_EXIT:
					mWebPlayer.stop(0);
					android.os.Process.killProcess(android.os.Process.myPid());
					break;

				case BrowserMessage.SW_CMD_PLAY_END:
					mWebPlayer.stop(0);
					notifyEvent(BrowserEvent.PLAY_END);
					break;
				case BrowserMessage.SW_CMD_PLAY_ERROR:
					mWebPlayer.stop(0);
					notifyEvent(BrowserEvent.START_PLAY_FAILED);
					break;
				case BrowserMessage.SW_MEDIA_CMD_SET_LOCATION:
					int x = msg.getData().getInt("x");
					int y = msg.getData().getInt("y");
					int w = msg.getData().getInt("w");
					int h = msg.getData().getInt("h");
					mWebPlayer.setVideoLayout(x, y, w, h);
					break;
				case BrowserMessage.SW_CMD_PLAY_SUCCESS:
					notifyEvent(BrowserEvent.START_PLAY_SUCCESS);
					// getProgressDialog().hide();
					break;
				default:
					break;
				}
			}

			super.handleMessage(msg);
		}
	};

	public Handler getHandler() {
		return mHandler;
	}

	private ProgressDialog mProgressDialog;

	private ProgressDialog getProgressDialog() {
		return getProgressDialog(null);
	}

	private ProgressDialog getProgressDialog(String message) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setTitle("��ʾ");
			if (message == null)
				message = "�����������ݣ����Ժ򡣡���";
			mProgressDialog.setMessage(message);
		} else if (message != null) {
			mProgressDialog.setMessage(message);
		}
		return mProgressDialog;
	}

	public void clearCache() {
		// TODO Auto-generated method stub
		mSystemScript.clearGlobalVar();
	}

	
}
