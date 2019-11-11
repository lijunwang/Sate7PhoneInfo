package get.hard.sate7phoneinfo.client;

import android.util.Log;

import com.google.common.io.BaseEncoding;

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
import get.hard.sate7phoneinfo.util.ConvertUtil;
import get.hard.sate7phoneinfo.util.NotificationHelper;
import okhttp3.OkHttpClient;

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

    public void testPost(byte[] data) {/*
        OutputStream os = null;
        String testData = "{}";
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("number", "18682145730");
//            jsonObj.put("data", new String(java.util.Base64.getEncoder().encode(data)));
//            jsonObj.put("data", new String(Base64.encodeToString(data,Base64.DEFAULT)));
            jsonObj.put("data", new String(Base64.encodeToString(data, Base64.URL_SAFE)));
            testData = jsonObj.toString();
        } catch (JSONException e) {
            XLog.dReport("testPost JSONException when construct PDU ...");
            e.printStackTrace();
        }
        try {
            URL serverUrl = new URL("https://qx.tsingk.net/api/v1/device/internal/forward");
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("cache-control", "no-cache");
            os = conn.getOutputStream();
            os.write(testData.getBytes());
            int respCode = conn.getResponseCode();

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append("\n").append(line);
            }
            JSONObject jsonObject = new JSONObject(builder.toString());
            int state = jsonObject.getInt("code");
            XLog.dReport("Test is ok state = " + state + "," + builder.toString());

            if (respCode == HttpURLConnection.HTTP_OK && state == 0) {
                XLog.dReport("Forward SMS successfully test !");
                NotificationHelper.notifyReportSuccess(true,true,true);
            } else {
//                Log.e(TAG, "HTTP response is " + respCode);
                XLog.dReport("HTTP response is  " + respCode);
            }
        } catch (Exception e) {
            XLog.dReport("Exception when forward SMS using HttpURLConnection!");
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        XLog.dReport("success 22...");*/
    }
}
