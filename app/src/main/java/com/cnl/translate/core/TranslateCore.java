package com.cnl.translate.core;

import com.cnl.translate.core.translate.HttpGet;
import com.cnl.translate.core.translate.MD5;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class TranslateCore{

	private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";
	private static String appid;
	private static String securityKey;
	static{
		appid = "20181224000251544";
		securityKey = "kUUHPqIfCseZuVTCAl8T";
	}

	public static void oauth(String ak, String sk){
		appid = ak;
		securityKey = sk;
	}

	public static String getTransResult(String query, String from, String to){
		Map<String, String> params = buildParams(query, from, to);
		return HttpGet.get(TRANS_API_HOST, params);
	}

	private static Map<String, String> buildParams(String query, String from, String to){
		Map<String, String> params = new HashMap<String, String>();
		params.put("q", query);
		params.put("from", from);
		params.put("to", to);
        params.put("appid", appid);
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);
        String sign = appid + query + salt + securityKey;
        params.put("sign", MD5.md5(sign));
        return params;
    }

	public static String parse(String text) throws Exception{
		JSONObject dst = new JSONObject(text);
		String code = dst.optString("error_code");
		if(!"".equals(code)){
			String err = dst.optString("error_msg");
			throw new Exception(String.format("error_code: %s, error_msg: %s", code, err));
		}else{
			StringBuilder sb = new StringBuilder();
			JSONArray ja = dst.getJSONArray("trans_result");
			for(int i=0;i < ja.length();i++){
				sb.append(ja.getJSONObject(i).getString("dst")).append("\n");
			}
			return sb.deleteCharAt(sb.length() - 1).toString();
		}
	}

}
