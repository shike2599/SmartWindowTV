package com.hisu.alicloud.gs.dj.webbrowser.mode;

/**
 * @author shijt
 *
 */
public class AppMsg {
	public static final int MSG_NET_UNCONNECTED = 0xFFF; // ����û�����ӣ�����
	public static final int MSG_REQUEST_TIMEOUT = 0x01; // ����ʱ
	public static final int MSG_REQUEST_FAILED = 0x02; // ����ʧ��
	public static final int MSG_REQUEST_DATA_ERROR = 0x03; // �������ݴ���
	public static final int MSG_REQUEST_ERROR = 0x04; // �������
	public static final int MSG_DATA_END = 0x05; // ���������
	public static final int MSG_REQUEST_FINISH = 0x06; // �������
	public static final int MSG_REQUEST_SUCCESS = 0x07; // ����ɹ�

	public static final int MSG_NEED_USER_LOGIN = 0x10; // �û���¼
	/**
	 * �б�����
	 */
	public static final int MSG_PAGE_LIST_VIEW = 0x11;

	
	/**
	 * �汾���
	 */
	public static final int MSG_CHECK_VERSION = 0x21;
	/**
	 * ������Ƶ
	 */
	public static final int MSG_CACHE_VIDEO = 0x22;
}
