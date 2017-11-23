package com.hisu.webbrowser.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class GsonHelper<T>{
	private Gson mGson;
	private Gson getGson(){
		if(mGson==null)mGson= new Gson();
		return mGson;
	}
	public  T parseObject(JSONObject json,Class<T> cls) {
		return parseObject(json.toString(),cls);
	}
	public  T parseObject(String json,Class<T> cls) {
		return getGson().fromJson(json,cls);
	}
	public  List<T> parseList(JSONArray array,Class<T> cls) throws JSONException {
		// TODO Auto-generated method stub
		List<T> list = new ArrayList<T>();
		for(int i=0,l=array.length();i<l;i++){
			T t = parseObject(array.getJSONObject(i),cls);
			list.add(t);
		}
		return list;
	}
	
	public T cloneObject(T t,Class<T> cls){
		String json = getGson().toJson(t);
		return parseObject(json,cls);
	}
	public String toJson(Object o){
		return getGson().toJson(o);
	}
}
