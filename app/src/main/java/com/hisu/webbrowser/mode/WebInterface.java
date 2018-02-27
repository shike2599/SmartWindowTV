package com.hisu.webbrowser.mode;


public class WebInterface {
	/*
	 * versionCode 10
	 * updateName CommunistTV
	 * */
//	private static final String URL_PREFIX = "http://10.43.116.80";
	/*
	 * versionCode 10
	 * */
//	public static  String URL_PREFIX = "http://dj.hisugj.com";
	/*
	 * versionCode 10  银行
	 * */
//	public static String URL_PREFIX = "http://bank.hisugj.com";

	/*
	 * versionCode 10   社区
	 * */


	//	public static  String URL_PREFIX = "http://10.14.1.210:8991";//内蒙 党建
//	public static  String URL_PREFIX = "http://10.14.1.210:8088";//内蒙 酒店
//	public static  String URL_PREFIX = "http://10.14.1.210:8809";//内蒙 社区
//	public static  String URL_PREFIX = "http://119.44.217.58:8809";//衡阳
//	public static  String URL_PREFIX = "http://10.0.0.122:8809";//武汉 张少伟
//	public static  String URL_PREFIX = "http://192.168.10.64:8809";//襄阳 刘工
//	public static  String URL_PREFIX = "http://172.20.27.251:8809";//襄阳 张少伟
//	public static  String URL_PREFIX = "http://172.31.137.2:8809";//襄阳  王瑞
	public static  String URL_PREFIX = "http://10.43.116.33";//新媒体 33      cmd
//	public static  String URL_PREFIX = "http://10.43.116.80";//新媒体 80 /
//	public static  String URL_PREFIX = "http://10.43.158.146";
//	public static  String URL_PREFIX = "http://172.16.134.195:8809";//衡阳 内网
//	public static  String URL_PREFIX = "http://101.200.167.125:8809";//张伟
//	public static  String URL_PREFIX = "http://114.215.132.43:8809";//常德
//	public static  String URL_PREFIX = "http://114.215.132.43:8088";//湖南酒店
//	public static  String URL_PREFIX = "http://114.215.132.43:8809";//湖南社区
//	public static  String URL_PREFIX = "http://114.215.132.43:8991";//湖南党建
//	public static  String URL_PREFIX = "http://hotel.hisugj.com";
//	public static  String URL_PREFIX = "http://community.hisugj.com";
//	public static  String URL_PREFIX = "http://192.16..17.188:8809";
//	public static  String URL_PREFIX = "http://10.248.1.155:8809";//十堰社区
	/*
	 * versionCode 10
	 * */
//	public static  String URL_PREFIX = "http://enterprise.hisugj.com";

	//	public static  String DEFAULT_HOME_PAGE = "http://124.47.33.73/nehe(9-27)/";
//	public static  String DEFAULT_HOME_PAGE = URL_PREFIX+"/smart_hotel/webpage/startup.html";
//
//	public static  String URL_APP_VERSION =  URL_PREFIX+"/SmartHotelInterface/api/smartHotel/checkAppVersion";
//
//	public static  String URL_CACHE_VIDEO =  URL_PREFIX+"/SmartHotelInterface/api/smartHotel/cacheVideos";
	public static final String SYSTEM_URL_CACHE_VIDEO = "http://123.57.18.76:8991/SmartHotelInterface/api/smartHotelAdmin/checkFileVersion";
	//public static final String URL_CACHE_VIDEO =  URL_PREFIX+"/apk/cacheVideos.json";

	public static String DEFAULT_HOME_PAGE(){
//		return "10.10.7.82:8088";21l
		return URL_PREFIX+"/smart_hotel/webpage/startup.html";
	}
	public static String URL_APP_VERSION(){
		return URL_PREFIX+"/SmartHotelInterface/api/smartHotel/checkAppVersion";
	}
	public static String URL_CACHE_VIDEO(){
		return URL_PREFIX+"/SmartHotelInterface/api/smartHotel/cacheVideos";
	}
	/*
	 * 更新系统开机视频
	 * */
	public static boolean DOWNOPENSYSVIDEO = false;
	/*
	 * 内嵌视频
	 * */
	public static boolean DOWNVIDEO = true;
	/*
	 * 断网页面
	 * */
	public static boolean ERRORACTIVITY = false;
	
	
}
