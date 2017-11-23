package com.hisu.alicloud.gs.dj.webbrowser.mode;

public class CacheVideo{
	
	/**
	 * ��Ƶ���
	 */
	private int id;
	
	/**
	 * �Ƶ���
	 */
	private int hotelId;
	/**
	 * ��Ƶ�ļ���
	 */
	private String name;
	/**
	 * ��Ƶ����·��
	 */
	private String url;
	/**
	 * ��Ƶ���ش��·��
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
		return this.id == other.id 
				&& this.hotelId == other.hotelId
				&& this.url.equals(other.url);
	}
}
