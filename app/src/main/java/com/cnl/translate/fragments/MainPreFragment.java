package com.cnl.translate.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.cnl.translate.R;
import android.content.SharedPreferences;
import android.content.Context;

public class MainPreFragment extends PreferenceFragment{
	
	public static final String SP_NAME = "setting";
	private final String[][] defData = {
		{"use_auto_delay", "1500"},
		{"image_compress", "30"},
		{"image_scale", "100"},
		{"ocr_language", "jp"},
		{"tra_language", "ch"},
		{"user_ocr_ak", ""},
		{"user_ocr_sk", ""},
		{"user_tra_ak", ""},
		{"user_tra_sk", ""}
	};
	private final PreferenceEnt[] defData2 = {
		new PreferenceEnt("use_auto", true),
		new PreferenceEnt("use_input", true),
		new PreferenceEnt("image_pale", true),
		new PreferenceEnt("user_ocr", false),
		new PreferenceEnt("user_tra", false)
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(SP_NAME);
		addPreferencesFromResource(R.xml.main_preference);
	}

	public boolean setDefault(Context ctx, boolean force){
		SharedPreferences sp = ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		if(sp.contains("use_auto") && !force){
			return false;
		}
		SharedPreferences.Editor spe = sp.edit();
		for(String[] data: defData){
			spe.putString(data[0], data[1]);
		}
		for(PreferenceEnt data: defData2){
			spe.putBoolean(data.name, data.enable);
		}
		return spe.commit();
	}
	
	private static class PreferenceEnt{
		public boolean enable;
		public String name;
		public PreferenceEnt(String n, boolean e){
			name = n;
			enable = e;
		}
	}
}
