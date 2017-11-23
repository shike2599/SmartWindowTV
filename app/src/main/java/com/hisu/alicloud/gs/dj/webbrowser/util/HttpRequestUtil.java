package com.hisu.alicloud.gs.dj.webbrowser.util;

import java.util.List;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.hisu.alicloud.gs.dj.webbrowser.mode.AppMsg;
import com.hisu.alicloud.gs.dj.webbrowser.mode.Constants;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class HttpRequestUtil {
	private boolean INTERCEPT_MSG_FLAG = false; // 是否拦截
	private Handler mHandler;
	private Context mContext;
	private int mMessageType = -1;
	private HttpUtils mClient;

	private Handler errorHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppMsg.MSG_REQUEST_FAILED:
				String desc = (String) msg.obj;
				if(TextUtils.isEmpty(desc)){
					desc = "操作失败，请稍后重试";
				}
				Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
				break;
			case AppMsg.MSG_REQUEST_TIMEOUT:
				Toast.makeText(mContext, "网络请求超时，请稍后重试", Toast.LENGTH_SHORT).show();
				break;
			case AppMsg.MSG_NET_UNCONNECTED:
				Toast.makeText(mContext, "网络未连接,请检查网络", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	public HttpRequestUtil(Context context, Handler handler) {
		this.mContext = context;
		this.mHandler = handler;
	}

	public HttpRequestUtil(Context context) {
		this.mContext = context;
	}
	/**
	 * 联网请求数据
	 */
	public void doRequest(String url, RequestParams params,
			boolean checkLogin,int MsgType) {
		mMessageType = MsgType;
		doRequest(url,params,checkLogin);
	}
	/**
	 * 联网请求数据
	 */
	public void doRequest(String url, RequestParams params,
			boolean checkLogin) {
		if (!ToolsUtil.isNetworkConnected(mContext)) {
			sendMessage(AppMsg.MSG_NET_UNCONNECTED);
			return;
		}
		if (params == null)
			params = new RequestParams();


		ToolsUtil.log("===doRequest url===" + url);
		List<NameValuePair> list = params.getQueryStringParams();
		if(list!=null && list.size()>0){
			for(NameValuePair item : list){
				ToolsUtil.log(item.getName()+"======" + item.getValue());
			}
		}
		
		setParams(params);

		if (mClient == null)
			mClient =  new HttpUtils();
		mClient.send(HttpRequest.HttpMethod.POST,
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
						HttpRequestUtil.this.sendMessage(AppMsg.MSG_REQUEST_ERROR);
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						// TODO Auto-generated method stub
						try {
							JSONObject resp = new JSONObject(response.result);
							ToolsUtil.log(mMessageType+"-----"+ resp.toString());
	
							if (200 == resp.getInt("resultCode")) {
								if (mMessageType < 0)
									mMessageType = AppMsg.MSG_REQUEST_SUCCESS;
								HttpRequestUtil.this.sendMessage(mMessageType, resp);
							} else {
								String desc = resp.getString("resultDesc");
								HttpRequestUtil.this.sendMessage(AppMsg.MSG_REQUEST_FAILED, desc);

							}
						} catch (JSONException e) {
							e.printStackTrace();
							HttpRequestUtil.this.sendMessage(AppMsg.MSG_REQUEST_FAILED);
							ToolsUtil.log(mMessageType+"-----"+ response.result);
						}
						mMessageType = -1;
					}
					
			});
	}
	private void setParams(RequestParams params) {
		params.addQueryStringParameter("requestUser", Constants.REQUEST_USER);
		params.addQueryStringParameter("requestPassword", Constants.REQUEST_PASSWROD);
		params.addQueryStringParameter("requestSource", Constants.REQUEST_SOURCE);
	}

	/**
	 * 发送消息
	 * 
	 * @param what
	 */
	public void sendMessage(int what) {
		sendMessage(what, null);
	}

	public void sendMessage(int what, Object obj) {
		ToolsUtil.log("HttpRequestUtil--------sendMessage-----"+ what);
		Message msg = Message.obtain();
		msg.what = what;
		if (obj != null) {
			msg.obj = obj;
		}
		if (mHandler == null) {
			return;
		}
		
		if(!INTERCEPT_MSG_FLAG){
			Message m = new Message();
			m.copyFrom(msg);
			errorHandler.sendMessage(m);
		}
		
		mHandler.sendMessage(msg);
	}

	public void pageListRequest(String url, RequestParams  params,
			boolean needLogin) {
		// TODO Auto-generated method stub
		mMessageType = AppMsg.MSG_PAGE_LIST_VIEW;
		doRequest(url,params,needLogin);
	}
	
	public void setMsgInterceptFlag(boolean bool){
		INTERCEPT_MSG_FLAG = bool;
	}

}
