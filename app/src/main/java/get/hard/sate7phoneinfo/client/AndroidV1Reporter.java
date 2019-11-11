package get.hard.sate7phoneinfo.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.blankj.utilcode.util.NetworkUtils;
import com.qx.protocol.Message;
import com.qx.protocol.MessageReport;
import com.qx.protocol.ReportData;
import com.qx.protocol.ReportDataAltitude;
import com.qx.protocol.ReportDataBattery;
import com.qx.protocol.ReportDataCallLog;
import com.qx.protocol.ReportDataDateTime;
import com.qx.protocol.ReportDataHw;
import com.qx.protocol.ReportDataImei;
import com.qx.protocol.ReportDataLatitude;
import com.qx.protocol.ReportDataLongitude;
import com.qx.protocol.ReportDataMissedCall;
import com.qx.protocol.ReportDataNumber;
import com.qx.protocol.ReportDataOnOffLog;
import com.qx.protocol.ReportDataRfcn;
import com.qx.protocol.ReportDataSignal;
import com.qx.protocol.ReportDataSpeed;
import com.qx.protocol.ReportDataSw;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import get.hard.sate7phoneinfo.PhoneInfoApp;
import get.hard.sate7phoneinfo.ReportService;
import get.hard.sate7phoneinfo.XLog;
import get.hard.sate7phoneinfo.util.AlarmHelper;
import get.hard.sate7phoneinfo.util.M2MHelper;

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

    @SuppressLint("MissingPermission")
    public void sendToServer(Context context) {
        buildReportData();
        mData = mMessageReport.encode();
        //@@@start test by WLJ
//        testDecode(null);
        //@@@end test by WLJ
        if (isDataConnected()) {
//            reportWithSocket();
            reportWithHttp();
            testDecode(null);

//            Test M2M
//            String body = "Time:2019-10-11-17-41,IMEI:,LNG:113.853443E,LAT:22.585754N";
//            String body = "Time:2019-10-12-9-30,IMEI:860106000336556,LNG:113.853489E,LAT:22.585769N";
//            passM2MBody(body,"18682145736");
//            M2MHelper.send2Server(body,"18682145739");
        } else {
            reportWithSms();
        }

    }

    private void testDecode(byte[] in) {
        XLog.dMms("testDecode ww...");
        MessageReport messageReport = (MessageReport) new MessageReport().decode(in == null ? mData : in);
        List<ReportData> list = messageReport.getReportDataList();
        if(list == null){
            XLog.dMms("testDecode ww list == null...");
            return;
        }
        for (ReportData data : list) {
            switch (data.getRdt()) {
                case RMT_IMEI:
                    ReportDataImei imei = (ReportDataImei) data;
                    XLog.dMms("mData imei === " + imei.getImei());
                    break;
                case RMT_RFCN:
                    ReportDataRfcn rfcn = (ReportDataRfcn) data;
                    XLog.dMms("mData rfcn === " + rfcn.getRfcn());
                    break;
                case RMT_SPEED:
                    ReportDataSpeed speed = (ReportDataSpeed) data;
                    XLog.dMms("mData speed === " + speed.getSpeed());
                    break;
                case RMT_NUMBER:
                    ReportDataNumber number = (ReportDataNumber) data;
                    XLog.dMms("mData number === " + number.getNumber());
                    break;
                case RMT_SIGNAL:
                    ReportDataSignal signal = (ReportDataSignal) data;
                    XLog.dMms("mData signal === " + signal.getSignal());
                    break;
                case RMT_BATTERY:
                    ReportDataBattery battery = (ReportDataBattery) data;
                    XLog.dMms("mData battery === " + battery.getBattery());
                    break;
                case RMT_UNKNOWN:
                    XLog.dMms("mData RMT_UNKNOWN === ");
                    break;
                case RMT_ALTITUDE:
                    ReportDataAltitude altitude = (ReportDataAltitude) data;
                    XLog.dMms("mData altitude === " + altitude.getAltitude());
                    break;
                case RMT_LATITUDE:
                    ReportDataLatitude latitude = (ReportDataLatitude) data;
                    XLog.dMms("mData latitude === " + latitude.getLatitude());
                    break;
                case RMT_DATE_TIME:
                    ReportDataDateTime dateTime = (ReportDataDateTime) data;
                    XLog.dMms("mData dateTime === " + dateTime.getDate());
                    break;
                case RMT_LONGITUDE:
                    ReportDataLongitude longitude = (ReportDataLongitude) data;
                    XLog.dMms("mData longitude === " + longitude.getLongitude());
                    break;
                case RMT_HW_VERSION:
                    ReportDataHw hw = (ReportDataHw) data;
                    XLog.dMms("mData hw === " + hw.getHw());
                    break;
                case RMT_SW_VERSION:
                    ReportDataSw sw = (ReportDataSw) data;
                    XLog.dMms("mData sw === " + sw.getSw());
                    break;
                case RMT_MISSED_CALL:
                    ReportDataMissedCall call = (ReportDataMissedCall) data;
                    XLog.dMms("mData call === " + call.getDataLen());
                    break;
                case RMT_ON_OFF_RECORD:
                    ReportDataOnOffLog onOffLog = (ReportDataOnOffLog) data;
                    XLog.dMms("mData onOffLog === " + onOffLog.getList());
                    break;
                case RMT_CALL_LOG_RECORD:
                    ReportDataCallLog callLog = (ReportDataCallLog) data;
                    XLog.dMms("mData callLog === " + callLog.getList());
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isDataConnected() {
//        return false;
        XLog.dReport("isDataConnected ... " + NetworkUtils.isConnected());
//        return NetworkUtils.isConnected();
        return true;
    }

    private void reportWithSocket() {
        XLog.dReport("reportWithSocket ... " + mData);
        SocketHelper.getInstance().sendData(mData);
    }

    @SuppressLint("MissingPermission")
    private void reportWithHttp() {
        /*MessageReport mr = new MessageReport();
//        ReportDataImei imei = new ReportDataImei("738497593453455");
        ReportDataImei imei = new ReportDataImei(PhoneUtils.getIMEI());
        mr.addReportData(imei);

        ReportDataDateTime dateTime = new ReportDataDateTime(new Date());
        mr.addReportData(dateTime);
//        北纬N22°32′43.86″, 东经E114°03′10.40″
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location != null) {
            XLog.dReport("reportWithHttp ... " + location.getLongitude() + "," + location.getLatitude());
        } else {
            XLog.dReport("reportWithHttp location == null... ");
        }
        ReportDataLatitude latitude = new ReportDataLatitude(location == null ? 22.324386f : (float) location.getLatitude());
        ReportDataLongitude longitude = new ReportDataLongitude(location == null ? 14.031040f : (float) location.getLongitude());
        mr.addReportData(latitude);
        mr.addReportData(longitude);
        PhoneInfoApp app = (PhoneInfoApp) context.getApplicationContext();
        ReportDataBattery battery = new ReportDataBattery(app.getBattery());
        mr.addReportData(battery);

        ReportDataSw sw = new ReportDataSw("Android:" + Build.VERSION.SDK_INT);
        mr.addReportData(sw);

//        byte[] d = mr.encode();
//        OkHttpHelper.getInstance().testPost(d);*/
        OkHttpHelper.getInstance().reportByPost(mData);
    }

    public void reportWithSms() {
        Log.d(TAG, "reportWithSms ... " + mData + "," + getMmsType());
        if (getMmsType() != ReportService.REPORT_TYPE_FIRST && getMmsType() != ReportService.REPORT_TYPE_ADJUST && getMmsType() != ReportService.REPORT_TYPE_NORMAL) {
            Log.e(TAG, "REPORT_TYPE error,see at ReportService.java");
        }
        String content = new String(mData);
//        BootReceiver.sendSmsSilent(context.getResources().getString(R.string.mms_center_number), content, getMmsType());
        XLog.dReport("reportWithSms ..." + content);
    }

    public abstract int getMmsType();

    private void passM2MBody(String body, String number) {
        try {
            String[] data = body.trim().split(",");
            XLog.dMms("passM2MBody ww ... " + body + "," + number + "," + data.length + "," + Arrays.toString(data));
            String time = data[0].split(":")[1];
            String imei;
            if(data[1].split(":").length > 1){
                imei  = data[1].split(":")[1];
            }else{
                imei = "0123456789123456";
            }

            String lng = data[2].split(":")[1];
            String lat = data[3].split(":")[1];
            XLog.dMms("pass body ww ... " + time + "," + imei + "," + lng + "," + lat + "," + number);
            MessageReport messageReport = new MessageReport();
            messageReport.addReportData(new ReportDataImei(imei));
            messageReport.addReportData(new ReportDataDateTime(new Date()));
            messageReport.addReportData(new ReportDataLongitude(Float.parseFloat(lng.replace("E",""))));
            messageReport.addReportData(new ReportDataLatitude(Float.parseFloat(lat.replace("N",""))));
            messageReport.addReportData(new ReportDataAltitude(0f));
            messageReport.addReportData(new ReportDataSw("M2M_V1.0"));
            messageReport.addReportData(new ReportDataHw("M2M"));
            messageReport.addReportData(new ReportDataNumber(number));
            PhoneInfoApp.setPhoneNumber(number);
            messageReport.addReportData(new ReportDataBattery(92));
            messageReport.addReportData(new ReportDataSignal(95));
            byte[] sendData = messageReport.encode();
            testDecode(sendData);
            OkHttpHelper.getInstance().reportByPost(sendData);
        } catch (Exception e) {
            XLog.dMms("passM2MBody Exception ... " + e.getMessage());
        }
    }
}
