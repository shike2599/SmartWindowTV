package com.hisu.webbrowser.util;

public class BrowserMessage {

	/**************************************************************
	 * SW Messages
	 **/
	public static final int SW_BROWSER_PROGRESS_CHANGED = 0;
	public static final int SW_MEDIA_CMD_PLAY = 1;
	public static final int SW_MEDIA_CMD_STOP = 2;
	public static final int SW_MEDIA_CMD_SEEK = 3;
	public static final int SW_MEDIA_CMD_PAUSE = 4;
	public static final int SW_MEDIA_CMD_RESUME = 5;
	public static final int SW_MEDIA_CMD_SET_LOCATION = 6;
	public static final int SW_CMD_PLAY_END = 7;
	public static final int SW_CMD_PLAY_SUCCESS = 8;
	public static final int SW_MEDIA_DVB_PLAY = 0x3322;

	public static final int SW_OPEN_APP = 9; 
	public static final int SW_OPEN_URL = 10;
	public static final int SW_TOAST = 11;
	public static final int SW_GOTO_BACK = 12;
	public static final int SW_CMD_EXIT = 13;

	public static final int SW_CMD_PLAY_ERROR = 14;
}
