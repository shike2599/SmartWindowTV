package com.hisu.webbrowser.manager;

import java.io.File;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hisu.webbrowser.R;
import com.hisu.webbrowser.mode.Constants;
import com.hisu.webbrowser.mode.WebInterface;
import com.hisu.webbrowser.util.HttpRequestUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class UpdateManager {
	// 下载中...
	private static final int DOWNLOAD = 1;
	// 下载完成
	private static final int DOWNLOAD_FINISH = 2;
	// 有新版本...
	private static final int HAS_NEW_VERSION = 3;
	// 没有新版本...
	private static final int HAS_NOT_NEW_VERSION = 4;
	// 下载失败.
	private static final int DOWNLOAD_FAILED = 5;

	private static final int CHECK_VERSION = 6;
	private JSONObject mVersion;
	// 下载保存路径
	private String mSavePath;
	// 记录进度条数量
	private int progress;
	// 是否取消更新
	private boolean cancelUpdate = false;
	// 上下文对象
	private Context mContext;
	// 进度条
	private ProgressBar mProgressBar;
	// 更新进度条的对话框
	private Dialog mDownloadDialog;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				// 下载中。。。
				case DOWNLOAD:
					// 更新进度条
					System.out.println(progress);
					mProgressBar.setProgress(progress);
					break;
				// 下载完成
				case DOWNLOAD_FINISH:
					// 安装文件
					try {
						mDownloadDialog.dismiss();
						installApk();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				// 提示更新
				case HAS_NEW_VERSION:
					showDownloadDialog();
					break;
				// 没有新版本
				case HAS_NOT_NEW_VERSION:
//				Toast.makeText(
//						mContext,
//						mContext.getString(R.string.soft_update_no)
//								+ mVersionName, Toast.LENGTH_SHORT).show();
					break;
				// 下载失败
				case DOWNLOAD_FAILED:
					mDownloadDialog.dismiss();
					Toast.makeText(mContext, R.string.soft_download_failed,
							Toast.LENGTH_SHORT).show();
					break;
				case CHECK_VERSION:
					JSONObject obj = (JSONObject) msg.obj;

					Log.e("APP",obj.toString());
					try {
						if (obj.has("versionInfo")) {
							mVersion = obj.getJSONObject("versionInfo");
							mVersion.put("name", Constants.APP_FLAG + ".apk");
							mHandler.sendEmptyMessage(HAS_NEW_VERSION);
							break;
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					mHandler.sendEmptyMessage(HAS_NOT_NEW_VERSION);
					break;
			}
		};
	};

	private String mVersionName = "";
	private Activity act;

	public UpdateManager(Context context, Activity act) {
		super();
		this.mContext = context;
		this.act = act;
	}

	public UpdateManager(Context context) {
		super();
		this.mContext = context;
	}

	/**
	 * 检测软件更新
	 */
	public void checkUpdate() {
		String url = WebInterface.URL_APP_VERSION();
		RequestParams params = new RequestParams();
		int versionCode = getVersionCode(mContext);

		params.addQueryStringParameter("app", Constants.APP_FLAG);
		params.addQueryStringParameter("code", "" + versionCode);
		params.addQueryStringParameter("type", "0");
		new HttpRequestUtil(mContext, mHandler).doRequest(url, params, false,
				CHECK_VERSION);
	}

	private void showNoticeDialog() {
		showNoticeDialog(false);
	}

	private void showNoticeDialog(boolean isDownload) {
		// TODO Auto-generated method stub
		// 构造对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(isDownload ? R.string.app_download_title
				: R.string.soft_update_title);
		builder.setMessage(isDownload ? R.string.app_download_info
				: R.string.soft_update_info);
		// 更新
		builder.setPositiveButton(isDownload ? R.string.app_download_updatebtn
				: R.string.soft_update_updatebtn, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				// 显示下载对话框
				showDownloadDialog();
			}
		});
		// 稍后更新
		builder.setNegativeButton(isDownload ? R.string.soft_update_cancel
				: R.string.soft_update_later, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	private void showDownloadDialog() {
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.softupdate_progress, null);
		mProgressBar = (ProgressBar) view.findViewById(R.id.update_progress);
		builder.setView(view);

		/*
		 * builder.setNegativeButton(R.string.soft_update_cancel, new
		 * OnClickListener() {
		 *
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * TODO Auto-generated method stub dialog.dismiss(); // 设置取消状态
		 * cancelUpdate = true; } });
		 */
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 下载文件
		try {
			downloadApk();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void downloadApk(String url) {
		try {
			mVersion = new JSONObject();
			mVersion.put("name", "apk" + new Date().getTime());
			mVersion.put("downLoadUrl", url);
			showNoticeDialog(true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 下载APK文件
	 *
	 * @throws JSONException
	 */
	private void downloadApk() throws JSONException {
		// TODO Auto-generated method stub
		// 启动新线程下载软件
		// new DownloadApkThread().start();

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(mContext, "SD卡不存在", Toast.LENGTH_LONG).show();
			mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
			return;
		}
		// 获取SDCard的路径
		String sdpath = Environment.getExternalStorageDirectory().getPath() + "/";
		mSavePath = sdpath + "download";
		String filePath = mSavePath + "/" + mVersion.getString("name");
		File saveFile = new File(filePath);
		if (saveFile.exists()) {
			saveFile.delete();
		}
		HttpUtils http = new HttpUtils();
		// HttpHandler handler =
		// System.out.println("mVersion.getString(downLoadUrl)==="+mVersion.getString("downLoadUrl"));
		// Log.e("mVersion.getString(downLoadUrl)===",
		// mVersion.getString("downLoadUrl"));
		// System.out.println("mVersion.getString(name)==="+mSavePath+"/"+mVersion.getString("name"));
		http.download(mVersion.getString("downLoadUrl"), filePath, true, true,
				new RequestCallBack<File>() {
					@Override
					public void onStart() {
						// testTextView.setText("conn...");
					}

					@Override
					public void onLoading(long total, long current,
										  boolean isUploading) {
						// testTextView.setText(current + "/" + total);
						progress = (int) (((float) current / total) * 100);
						mHandler.sendEmptyMessage(DOWNLOAD);

					}

					@Override
					public void onFailure(HttpException error, String msg) {
						System.out.println("downloadApk onFailure msg=" + msg);
						mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
					}

					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						// TODO Auto-generated method stub
						// testTextView.setText("downloaded:" +
						// responseInfo.result.getPath());
						mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
					}
				});

	}

	/**
	 * 获取软件版本号
	 *
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		// TODO Auto-generated method stub
		int versionCode = 0;

		// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
		try {
			PackageInfo packageInfo = mContext.getPackageManager()
					.getPackageInfo(mContext.getPackageName(), 0);
			versionCode = packageInfo.versionCode;
			mVersionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 安装APK文件
	 * @throws JSONException
	 */

	private void installApk() throws JSONException {
		File apkfile = new File(mSavePath, mVersion.getString("name"));
		if (!apkfile.exists()) {
			return;
		}
		PackageManager packageManager = mContext.getPackageManager();
		PackageInfo packageInfo = packageManager.getPackageArchiveInfo(
				apkfile.getAbsolutePath(), PackageManager.GET_ACTIVITIES);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		String jsonStr = sp.getString(Constants.APK_FILE_PATH_INFO, "{}");
		JSONObject jsonObj = new JSONObject(jsonStr);
		jsonObj.put(packageInfo.packageName, apkfile.getAbsolutePath());
		sp.edit().putString(Constants.APK_FILE_PATH_INFO, jsonObj.toString())
				.commit();
		System.out.println("jsonStr===========" + jsonObj.toString());
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		Log.e("installApk", Uri.parse("file://" + apkfile.toString())
				.toString());
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(i);
		// Uri mPackageURI = Uri.fromFile(new File("file://" +
		// apkfile.toString()));
		//
		// int installFlags = 0;
		//
		// PackageManager pm = mContext.getPackageManager();
		//
		// try
		//
		// {
		//
		// PackageInfo pi = pm.getPackageInfo("com.webbrowser",
		//
		// PackageManager.GET_UNINSTALLED_PACKAGES);
		//
		// if(pi != null)
		//
		// {
		//
		// installFlags |= PackageManager.REPLACE_EXISTING_PACKAGE;
		//
		// }
		//
		// }
		//
		// catch (NameNotFoundException e)
		//
		// {}
		//
		// PackageInstallObserver observer = new PackageInstallObserver();
		//
		// pm.installPackage(mPackageURI, observer, installFlags);
	}
}
