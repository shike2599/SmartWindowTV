package android.ccdt.dvb.service;

import android.ccdt.dvb.service.DvbPlayerListener;

interface DvbPlayerService {
	/**
     * 设置播放器的状态监听器
     */
	void setPlayerListener(in DvbPlayerListener listener);	
     
	boolean setPosition(int x, int y, int w, int h);
	
	/*url格式：dvb://network_id.transport_stream_id.service_id*/
	boolean start(String url);
	
	/**
	 * 该方法异步执行（即不可阻塞），处理过程中的错误触发监控器
	 */
	boolean stop();
}
