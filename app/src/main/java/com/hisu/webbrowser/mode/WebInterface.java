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
	 * versionCode 10  ����
	 * */
//	public static String URL_PREFIX = "http://bank.hisugj.com";
	
	/*
	 * versionCode 10   ����
	 * */
	
	
//	public static  String URL_PREFIX = "http://10.14.1.210:8991";//���� ���� 
//	public static  String URL_PREFIX = "http://10.14.1.210:8088";//���� �Ƶ� 
//	public static  String URL_PREFIX = "http://community.hisugj.com";//���� 
//	public static  String URL_PREFIX = "http://10.14.1.210:8991";//���� ���� 
//	public static  String URL_PREFIX = "http://10.14.1.210:8088";//���� �Ƶ� 
//	public static  String URL_PREFIX = "http://10.14.1.210:8809";//���� ���� 
//	public static  String URL_PREFIX = "http://119.44.217.58:8809";//���� 
//	public static  String URL_PREFIX = "http://192.168.10.64:8809";//���� ���� 
//	public static  String URL_PREFIX = "http://172.20.27.251:8809";//���� ����ΰ
//	public static  String URL_PREFIX = "http://10.43.116.33";//��ý�� 33      cmd 
//	public static  String URL_PREFIX = "http://10.43.116.80";//��ý�� 80 /
//	public static  String URL_PREFIX = "http://172.16.134.195:8809";//���� ����
//	public static  String URL_PREFIX = "http://101.200.167.125:8809";//��ΰ
//	public static  String URL_PREFIX = "http://114.215.132.43:8809";//����
//	public static  String URL_PREFIX = "http://114.215.132.43:8088";//���ϾƵ�
//	public static  String URL_PREFIX = "http://114.215.132.43:8809";//��������
//	public static  String URL_PREFIX = "http://114.215.132.43:8991";//���ϵ���
//	public static  String URL_PREFIX = "http://hotel.hisugj.com";                         
	public static  String URL_PREFIX = "http://community.hisugj.com";	
//	public static  String URL_PREFIX = "http://192.16..17.188:8809";
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
		
//		return "http://community.hisugj.com/smart_file/sys_file/hotel/52/static/portal/lang_zh/indexzzj.htm";
//		return URL_PREFIX+"/smart_hotel/webpage/startup.html";
//		return URL_PREFIX+"//xy/html/index.htm";
		return "file:///android_asset/www/index.htm";
	}
	public static String URL_APP_VERSION(){
		return URL_PREFIX+"/SmartHotelInterface/api/smartHotel/checkAppVersion";
	}
	public static String URL_CACHE_VIDEO(){ 
		return URL_PREFIX+"/SmartHotelInterface/api/smartHotel/cacheVideos";
	}
	/*
	 * ����ϵͳ������Ƶ
	 * */
	public static boolean DOWNOPENSYSVIDEO = false;
	/*
	 * ��Ƕ��Ƶ
	 * */
	public static boolean DOWNVIDEO = false;
	/*
	 * ����ҳ��
	 * */
	public static boolean ERRORACTIVITY = false;
	
	
}
