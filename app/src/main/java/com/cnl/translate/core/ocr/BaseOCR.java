package com.cnl.translate.core.ocr;

import java.net.URI;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseOCR{
	
	protected AipClientConfiguration config = new AipClientConfiguration();
	protected String accessToken = "24.4ba156dc274c0ef414a22d8eb9c4b518.2592000.1573732716.282335-15192302";
	
	protected BaseOCR(){
		config.setConnectionTimeoutMillis(5*1000);
		config.setSocketTimeoutMillis(10*1000);
	}
	
	public String getToken(){
		return accessToken;
	}
	
	protected void preOperation(AipRequest request){
        request.setHttpMethod(HttpMethodName.POST);
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.addHeader("accept", "*/*");
        request.setConfig(config);
    }

    protected void postOperation(AipRequest request){
		request.addParam("aipSdk", "java");
		request.addParam("access_token", accessToken);
    }

    protected JSONObject requestServer(AipRequest request){
        AipResponse response = AipHttpClient.post(request);
        String resData = response.getBodyStr();
        Integer status = response.getStatus();
        if(status.equals(200) && !resData.equals("")){
            try{
                return new JSONObject(resData);
            }catch(JSONException e){
                return Util.getGeneralError(-1, resData);
            }
        }
		return null;
    }

    public void oauthSync(final String apiKey, final String secretKey){
	    new Thread(new Runnable() {
            @Override
            public void run() {
                accessToken = oauth(apiKey, secretKey).optString("access_token");
            }
        }).start();
    }

	public JSONObject oauth(String apiKey, String secretKey){
        try{
            AipRequest request = new AipRequest();
            request.setUri(new URI("https://aip.baidubce.com/oauth/2.0/token"));
            request.addBody("grant_type", "client_credentials");
            request.addBody("client_id", apiKey);
            request.addBody("client_secret", secretKey);
            int statusCode = 500;
            AipResponse response = null;
            int cnt = 0;
            while(statusCode == 500 && cnt < 3){
                response = AipHttpClient.post(request);
                statusCode = response.getStatus();
                cnt++;
            }
            String res = response.getBodyStr();
            if(res != null && !res.equals("")){
                return new JSONObject(res);
            }else{
                return Util.getGeneralError(statusCode, "Server response code: " + statusCode);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return Util.getGeneralError(-1, "unknown error");
    }
}
