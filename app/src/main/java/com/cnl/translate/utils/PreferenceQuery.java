package com.cnl.translate.utils;
import android.content.Context;
import android.content.SharedPreferences;
import com.cnl.translate.fragments.MainPreFragment;

public class PreferenceQuery{
	
	private static PreferenceQuery instance;
	private SharedPreferences sp;
	
	private PreferenceQuery(Context ctx){
		sp = ctx.getSharedPreferences(MainPreFragment.SP_NAME, Context.MODE_PRIVATE);
	}
	
	public static PreferenceQuery getInstance(){
		return instance;
	}
	
	public static PreferenceQuery getInstance(Context ctx){
		if(instance == null){
			instance = new PreferenceQuery(ctx);
		}
		return instance;
	}
	
	public boolean getBoolean(String name){
		return sp.getBoolean(name, false);
	}
	
	public String getString(String name){
		return sp.getString(name, "");
	}
	
	public int getStringAsInt(String name){
		return Integer.parseInt(getString(name));
	}
}
