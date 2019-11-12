package get.hard.sate7phoneinfo.util;

import android.content.Context;
import android.util.Base64;

import com.google.gson.Gson;

import java.io.IOException;

import get.hard.sate7phoneinfo.XLog;
import get.hard.sate7phoneinfo.bean.V1ReportBean;
import get.hard.sate7phoneinfo.client.OkHttpHelper;
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
            XLog.dReport("transferByMms IOException ..." + e.getMessage());
        }
        XLog.dReport("transferByMms number == " + number);
        XLog.dReport("raw in == " + jsonString);
        XLog.dReport("raw after == " + raw);

    }

    public static void parseAndReportToServer(String body) {
        XLog.dReport("parseAndReportToServer body == "  + body);
        String parsed = CompressUtils.uncompressToString(Base64.decode(body, Base64.DEFAULT));
        XLog.dReport("parseAndReportToServer parsed == "  + parsed);
//        post2Server(parsed);
        OkHttpHelper.getInstance().reportWithJsonFormat(new Gson().fromJson(parsed, V1ReportBean.class));
    }

    private static void post2Server(String json) {
        XLog.dReport("testNew 22 ... " + json);
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
                XLog.dReport("onFailure ... " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                XLog.dReport("onResponse ... " + response.isSuccessful());
                XLog.dReport("onResponse message ... " + response.message());
                XLog.dReport("onResponse body ... " + response.body().string());
            }
        });
    }
}
