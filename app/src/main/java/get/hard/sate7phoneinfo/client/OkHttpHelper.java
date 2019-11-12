package get.hard.sate7phoneinfo.client;

import android.content.Context;
import android.util.Log;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import get.hard.sate7phoneinfo.PhoneInfoApp;
import get.hard.sate7phoneinfo.XLog;
import get.hard.sate7phoneinfo.bean.V1ReportBean;
import get.hard.sate7phoneinfo.pattern.SharedPreferencesUtil;
import get.hard.sate7phoneinfo.util.ConvertUtil;
import get.hard.sate7phoneinfo.util.NotificationHelper;
import get.hard.sate7phoneinfo.util.V1MmsHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpHelper {
    private static final String TAG = "OkHttpHelper";
    private OkHttpClient mClient;
    private static OkHttpHelper mInstance;

    public static OkHttpHelper getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClient.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpHelper();
                }
            }
        }
        return mInstance;
    }

    private OkHttpHelper() {
        mClient = new OkHttpClient();
    }

    public void reportByPost(byte[] data) {
        XLog.dReport("reportByPost ww22ww Mr.wang ..." + ConvertUtil.bytesToHexString(data));
        OutputStream os = null;
        InputStream is = null;
        BufferedReader reader = null;
        String testData = "{}";
        JSONObject jsonObj = new JSONObject();
        try {
//            jsonObj.put("number", "18682145730");
            XLog.dReport("reportByPost getPhoneNumber " + PhoneInfoApp.getPhoneNumber());
            jsonObj.put("number", PhoneInfoApp.getPhoneNumber());
//            jsonObj.put("data", new String(java.util.Base64.getEncoder().encode(data)));
//            jsonObj.put("data", new String(Base64.encodeToString(data,Base64.DEFAULT)));
//            jsonObj.put("data", new String(Base64.encodeToString(data, Base64.URL_SAFE)));
//            String encodedText = BaseEncoding.base64().encode(data);
            jsonObj.put("data", new String(BaseEncoding.base64().encode(data)));
            testData = jsonObj.toString();
        } catch (JSONException e) {
            XLog.dReport("testPost JSONException when construct PDU ...");
            e.printStackTrace();
        }
        try {
//            URL serverUrl = new URL("https://qx.tsingk.net/api/v1/device/internal/forward");
            URL serverUrl = new URL("https://qx.tsingk.net:8443/api/v1/device/internal/forward");
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("cache-control", "no-cache");
            os = conn.getOutputStream();
            os.write(testData.getBytes());
            int respCode = conn.getResponseCode();

            is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                XLog.dReport("response json in while = " + line);
                builder.append("\n").append(line);
            }
            JSONObject jsonObject = new JSONObject(builder.toString());
            int state = jsonObject.getInt("code");
            XLog.dReport("response json state = " + state + "," + builder.toString());
            if (respCode == HttpURLConnection.HTTP_OK && state == 0) {
                NotificationHelper.notifyReportSuccess();
                XLog.dReport("Report Data success!!");
            } else {
                XLog.dReport("HTTP response is  " + respCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception when forward SMS using HttpURLConnection!");
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void reportWithJsonFormat(V1ReportBean reportBean) {
        Gson gson = new Gson();
        String content = gson.toJson(reportBean);
        XLog.dReport("reportWithJsonFormat == " + reportBean.getImei() + "," + content);
        String url = "Https://qx-new.tsingk.net/api/v1/device/report/v1";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        final Request request = new Request.Builder().
                addHeader("user_name", "vuser1").
                addHeader("pass", "vuser1").
                url(url).post(body).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.dReport("onFailure ... " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                XLog.dReport("onResponse ... " + response.isSuccessful() + "," + response.code());
                XLog.dReport("onResponse message ... " + response.message());
                XLog.dReport("onResponse body ... " + response.body().string());
                if (response.isSuccessful()) {
                    NotificationHelper.notifyReportSuccess();
                }
            }
        });
    }
}
