package com.cnl.translate.core.translate;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpGet{
    protected static final int SOCKET_TIMEOUT = 10000; // 10秒
    protected static final String GET = "GET";

    public static String get(String host, Map<String, String> params){
        try{
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] { myX509TrustManager }, null);
			
            String sendUrl = getUrlWithQueryString(host, params);
            URL uri = new URL(sendUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            if(conn instanceof HttpsURLConnection){
                ((HttpsURLConnection) conn).setSSLSocketFactory(sslcontext.getSocketFactory());
            }
            conn.setConnectTimeout(SOCKET_TIMEOUT);
            conn.setRequestMethod(GET);
            int statusCode = conn.getResponseCode();
            if(statusCode != HttpURLConnection.HTTP_OK){
                System.out.println("Http错误码：" + statusCode);
            }
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while((line = br.readLine()) != null){
                builder.append(line);
            }
			String text = builder.toString();
            close(br);
            close(is);
            conn.disconnect();
            return text;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getUrlWithQueryString(String url, Map<String, String> params){
        if(params == null){
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if(url.contains("?")){
            builder.append("&");
        }else{
            builder.append("?");
        }

        int i = 0;
        for(String key : params.keySet()){
            String value = params.get(key);
            if(value == null){
                continue;
            }

            if(i != 0){
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }

    protected static void close(Closeable closeable){
        if(closeable != null){
            try{
                closeable.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    public static String encode(String input){
        if(input == null){
            return "";
        }
        try{
            return URLEncoder.encode(input, "utf-8");
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return input;
    }

    private static TrustManager myX509TrustManager = new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers(){
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException{
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException{
        }
    };

}
