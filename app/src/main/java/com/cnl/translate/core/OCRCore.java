package com.cnl.translate.core;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import com.cnl.translate.core.ocr.AipRequest;
import com.cnl.translate.core.ocr.BaseOCR;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cnl.translate.utils.MainModul;
import com.cnl.translate.utils.PreferenceQuery;

public class OCRCore extends BaseOCR {

    private static OCRCore instance;

    private int defCompressInt = 30;
    private int defScale = 100;
    private boolean useGray = false;

    private boolean userOcr = false;
    private String ocrAK = "GpgA6ArdSYKOHhBHz6QcbyxB";
    private String ocrSK = "GG2q09MFqDyIRqtGuyLgQlL9KGkizx4Y";

    private OCRCore() {
        super();
        init();
        oauthSync(ocrAK, ocrSK);
    }

    private void init() {
        PreferenceQuery pq = PreferenceQuery.getInstance();
        defCompressInt = pq.getStringAsInt("image_compress");
        defScale = pq.getStringAsInt("image_scale");
        useGray = pq.getBoolean("image_pale");

        userOcr = pq.getBoolean("user_ocr");
        if(userOcr){
            ocrAK = pq.getString("user_ocr_ak");
            ocrSK = pq.getString("user_ocr_sk");
        }
    }

    public static OCRCore getInstance() {
        if (instance == null) {
            instance = new OCRCore();
        }
        return instance;
    }

    public void ocrAsync(Bitmap image, OnOCRListener callback) {
        ocrAsync(image, new HashMap<String, String>(), callback);
    }

    public void ocrAsync(Bitmap image, HashMap<String, String> options, OnOCRListener callback) {
        OCRTask task = new OCRTask(image, options, callback);
        task.executeOnExecutor(Executors.newCachedThreadPool());
    }

    private class OCRTask extends AsyncTask<Void, Void, String[]> {
        Bitmap bmp;
        HashMap<String, String> options;
        OnOCRListener callback;

        OCRTask(Bitmap bitmap, HashMap<String, String> option, OnOCRListener cb) {
            bmp = bitmap;
            options = option;
            callback = cb;
        }

        @Override
        protected String[] doInBackground(Void... params) {
            String[] result = new String[2];
            try {
                result[0] = parse(ocr(bmp, options));
            } catch (Exception e) {
                result[1] = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result[0] != null) {
                callback.onResult(result[0]);
            } else if (result[1] != null) {
                callback.onResult(result[1]);
            }
        }
    }

    public JSONObject ocr(Bitmap image, HashMap<String, String> options) {
        int w = (int) (image.getWidth() * defScale / 100.f);
        int h = (int) (image.getHeight() * defScale / 100.f);
        Bitmap sImg = MainModul.FitView(w, h, image);
        if(useGray){
            Bitmap bf = MainModul.bitmap2Gray(sImg);
            sImg.recycle();
            sImg = bf;
        }
        ByteArrayOutputStream bop = new ByteArrayOutputStream();
        sImg.compress(Bitmap.CompressFormat.JPEG, defCompressInt, bop);
        return ocr(bop.toByteArray(), options);
    }

    public JSONObject ocr(byte[] image, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);

        String base64Content = Base64.encodeToString(image, Base64.DEFAULT);
        request.addBody("image", base64Content);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri("https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic");
        postOperation(request);
        return requestServer(request);
    }

    public static String parse(JSONObject jobj) throws Exception {
        int err = jobj.optInt("error_code");
        StringBuilder sb = new StringBuilder();
        if (err != 0) {
            throw new Exception(jobj.optString("error_msg"));
        } else {
            JSONArray jarr = jobj.optJSONArray("words_result");
            JSONObject item;
            for (int i = 0, len = jarr.length(); i < len; i++) {
                item = jarr.optJSONObject(i);
                sb.append(item.optString("words"));
                sb.append("\n");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public interface OnOCRListener {
        public abstract void onResult(String text);

        public abstract void onError(String err);
    }

}
