package com.hisu.webbrowser.mode;

/**
 * @author shijt
 *
 */
public class AppMsg {
	public static final int MSG_NET_UNCONNECTED = 0xFFF; // 网络没有连接，断网
	public static final int MSG_REQUEST_TIMEOUT = 0x01; // 请求超时
	public static final int MSG_REQUEST_FAILED = 0x02; // 请求失败
	public static final int MSG_REQUEST_DATA_ERROR = 0x03; // 请求数据错误
	public static final int MSG_REQUEST_ERROR = 0x04; // 请求错误
	public static final int MSG_DATA_END = 0x05; // 数据最后了
	public static final int MSG_REQUEST_FINISH = 0x06; // 请求完成
	public static final int MSG_REQUEST_SUCCESS = 0x07; // 请求成功

	public static final int MSG_NEED_USER_LOGIN = 0x10; // 用户登录
	/**
	 * 列表数据
	 */
	public static final int MSG_PAGE_LIST_VIEW = 0x11;


	/**
	 * 版本检测
	 */
	public static final int MSG_CHECK_VERSION = 0x21;
	/**
	 * 缓存视频
	 */
	public static final int MSG_CACHE_VIDEO = 0x22;
}
