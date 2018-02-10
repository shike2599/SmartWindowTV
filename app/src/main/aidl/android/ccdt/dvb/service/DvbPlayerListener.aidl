package android.ccdt.dvb.service;

interface DvbPlayerListener {
	/**
	 *
	 * 播放器的错误状态，值见下：
	 * errorcode：
     * 正常播放       0
     * 无信号       1
     * 未插卡       2
     * 无授权       3
     * 本地播放失败   4
     * 无频道       5  
	 */
	void onError(int errorCode);
}
