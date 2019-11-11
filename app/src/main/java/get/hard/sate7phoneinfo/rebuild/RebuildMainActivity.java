package get.hard.sate7phoneinfo.rebuild;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import get.hard.sate7phoneinfo.R;
import get.hard.sate7phoneinfo.XLog;
import get.hard.sate7phoneinfo.bean.V1ReportBean;
import get.hard.sate7phoneinfo.util.CompressUtils;
import get.hard.sate7phoneinfo.util.MmsSender;
import get.hard.sate7phoneinfo.util.V1MmsHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

public class RebuildMainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    private final String TAG = "RebuildMainActivity";
    private String test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebuild_main);
        test = "{\n" +
                "  \"type\": \"QX_DEVICE_TYPE_V1\",\n" +
                "  \"imei\": \"866976046678050\",\n" +
                "  \"sw\": \"Android 28\",\n" +
                "  \"hw\": \"Kirin980\",\n" +
                "  \"battery\": 99,\n" +
                "  \"signal\": -102,\n" +
                "  \"gps\": [\n" +
                "    22.585734,\n" +
                "    113.85358\n" +
                "  ],\n" +
                "  \"velocity \": 10,\n" +
                "  \"on_off_recs\": [\n" +
                "    {\n" +
                "      \"date\": \"2018-10-21 10:31:34\",\n" +
                "      \"type\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"date\": \"2019-10-24 10:31:34\",\n" +
                "      \"type\": 2\n" +
                "    }\n" +
                "  ],\n" +
                "  \"call_log_recs \": [\n" +
                "    {\n" +
                "      \"date\": \"2019-10-11 10:31:34\",\n" +
                "      \"duration\": 200,\n" +
                "      \"number\": 15096092544,\n" +
                "      \"type\": 1\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Gson gson = new Gson();
        V1ReportBean v1ReportBean = gson.fromJson(test, V1ReportBean.class);
        v1ReportBean.setImei("866976046678045");
        XLog.d(TAG, "v1ReportBean ww:" + v1ReportBean.getImei());
        String convert = gson.toJson(v1ReportBean);
//        postTest(convert);
        postTest(test);
    }

    private void postTest(String json) {
        XLog.d(TAG, "testNew 22 ... ");
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.post:
                postTest(test);
                break;
            case R.id.get:
                try {
                    String raw = Base64.encodeToString(CompressUtils.compress(test), Base64.DEFAULT);
                    XLog.d(TAG, "raw ... " + test.length() + "," + raw.length());
                    String after = CompressUtils.uncompressToString(Base64.decode(raw, Base64.DEFAULT));
                    XLog.d(TAG, "after ... " + after);
                    V1MmsHelper.transferByMms(this,"18682145730",test);
                    String toSend = Base64.encodeToString(CompressUtils.compress(test), Base64.DEFAULT);
                    String passReceived = CompressUtils.uncompressToString(Base64.decode(raw, Base64.DEFAULT));
                    XLog.d("to send == " + toSend);
                    XLog.d("passReceived == " + passReceived);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }
}
