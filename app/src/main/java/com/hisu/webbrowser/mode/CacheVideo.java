package com.hisu.webbrowser.mode;

public class CacheVideo{

	/**
	 * 视频编号
	 */
	private int id;

	/**
	 * 酒店编号
	 */
	private int hotelId;
	/**
	 * 视频文件名
	 */
	private String name;
	/**
	 * 视频下载路径
	 */
	private String url;
	/**
	 * 视频本地存放路径
	 */
	private String savePath;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getHotelId() {
		return hotelId;
	}
	public void setHotelId(int hotelId) {
		this.hotelId = hotelId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean equals(CacheVideo other){
		if (this.url == null){
			return false;
		}

		return this.id == other.id 
				&& this.hotelId == other.hotelId
				&& this.url.equals(other.url);
	}
}
