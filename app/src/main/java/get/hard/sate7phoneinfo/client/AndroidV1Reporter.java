package get.hard.sate7phoneinfo.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.Gson;
import com.qx.protocol.MessageReport;

import java.util.ArrayList;

import get.hard.sate7phoneinfo.PhoneInfoApp;
import get.hard.sate7phoneinfo.R;
import get.hard.sate7phoneinfo.ReportService;
import get.hard.sate7phoneinfo.XLog;
import get.hard.sate7phoneinfo.bean.V1ReportBean;
import get.hard.sate7phoneinfo.pattern.SharedPreferencesUtil;
import get.hard.sate7phoneinfo.util.V1MmsHelper;

import static get.hard.sate7phoneinfo.MainActivity.TAG;


public abstract class AndroidV1Reporter {
    MessageReport mMessageReport = new MessageReport();
    public byte[] mData;

    public abstract void buildReportData();

    public abstract String buildMmsData();

    protected Context context;
    protected PhoneInfoApp mApp;

    public AndroidV1Reporter(Context context) {
        this.context = context;
        mApp = (PhoneInfoApp) context.getApplicationContext();
    }

    private boolean isDataConnected() {
        XLog.dReport("isDataConnected ... " + NetworkUtils.isConnected());
        return NetworkUtils.isConnected();
    }

    @SuppressLint("MissingPermission")
    public void sendToServer(Context context) {
        buildReportData();
        mData = mMessageReport.encode();
        if (isDataConnected()) {
            reportWithHttp();
        } else {
            reportWithSms();
        }
    }

    @SuppressLint("MissingPermission")
    private void reportWithHttp() {
        boolean jsonUpload = context.getResources().getBoolean(R.bool.json_format);
        XLog.dReport("reportWithHttp jsonUpload ..." + jsonUpload);
        if (jsonUpload) {
            OkHttpHelper.getInstance().reportWithJsonFormat(buildV1Bean());
        } else {
            OkHttpHelper.getInstance().reportByPost(mData);
        }
    }

    public void reportWithSms() {
        XLog.dReport("reportWithSms ... ");
        Gson gson = new Gson();
        String smsContent = gson.toJson(buildV1Bean());
        V1MmsHelper.transferByMms(context, SharedPreferencesUtil.getInstance().getString(SharedPreferencesUtil.MMSCenterKey), smsContent);
    }

    public abstract int getMmsType();

    private V1ReportBean buildV1Bean() {
        V1ReportBean v1ReportBean = new V1ReportBean();
        v1ReportBean.setType(context.getResources().getString(R.string.device_type_v1));
        v1ReportBean.setImei(mApp.getImei());
        v1ReportBean.setSw("Android:" + Build.VERSION.SDK_INT);
        v1ReportBean.setHw(Build.HARDWARE);
        v1ReportBean.setBattery(mApp.getBattery());
        v1ReportBean.setSignal(mApp.getSignalStrength());
        Location location = mApp.getLocation();
        ArrayList<Double> loc = new ArrayList<>();
        double speed = 0;
        if (location != null) {
            loc.add(location.getLatitude());
            loc.add(location.getLongitude());
            speed = location.getSpeed();
        } else {
            loc.add(0d);
            loc.add(0d);
        }
        v1ReportBean.setGps(loc);
        v1ReportBean.setVelocity((int) speed);

        ArrayList<V1ReportBean.OnOffRecs> onOffRecsArrayList = new ArrayList<>();
        v1ReportBean.setOnOffRecs(onOffRecsArrayList);

        ArrayList<V1ReportBean.CallLogRecs> callLogRecsArrayList = new ArrayList<>();
        v1ReportBean.setCallLogRecs(callLogRecsArrayList);
        return v1ReportBean;
    }
}
