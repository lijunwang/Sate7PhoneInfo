package get.hard.sate7phoneinfo.util;

import android.content.Context;
import android.util.Base64;

import java.io.IOException;

import get.hard.sate7phoneinfo.XLog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class V1MmsHelper {
    private static final String TAG = "V1MmsHelper";

    public static void transferByMms(Context context, String number, String jsonString) {
        String raw = null;
        try {
            raw = Base64.encodeToString(CompressUtils.compress(jsonString), Base64.DEFAULT);
            MmsSender.sendSms(context, number, raw);
        } catch (IOException e) {
            e.printStackTrace();
            XLog.d(TAG, "transferByMms IOException ..." + e.getMessage());
        }
        XLog.d(TAG, "raw in == " + jsonString);
        XLog.d(TAG, "raw after == " + raw);

    }

    public static void parseAndReportToServer(Context context, String body) {
        XLog.d(TAG,"parseAndReportToServer body == "  + body);
        String parsed = CompressUtils.uncompressToString(Base64.decode(body, Base64.DEFAULT));
        XLog.d(TAG,"parseAndReportToServer parsed == "  + parsed);
        post2Server(parsed);
    }

    private static void post2Server(String json) {
        XLog.d(TAG, "testNew 22 ... " + json);
        String url = "Https://qx-new.tsingk.net/api/v1/device/report/v1";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
        Request request = new Request.Builder().
                addHeader("user_name", "vuser1").
                addHeader("pass", "vuser1").
                url(url).post(body).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.d(TAG, "onFailure ... " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                XLog.d(TAG, "onResponse ... " + response.isSuccessful());
                XLog.d(TAG, "onResponse message ... " + response.message());
                XLog.d(TAG, "onResponse body ... " + response.body().string());
            }
        });
    }
}
