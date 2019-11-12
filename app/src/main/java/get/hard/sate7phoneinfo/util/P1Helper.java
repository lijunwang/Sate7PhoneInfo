package get.hard.sate7phoneinfo.util;

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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import get.hard.sate7phoneinfo.PhoneInfo;
import get.hard.sate7phoneinfo.PhoneInfoApp;
import get.hard.sate7phoneinfo.XLog;
import get.hard.sate7phoneinfo.bean.V1ReportBean;
import get.hard.sate7phoneinfo.client.OkHttpHelper;

public class P1Helper {
    public static boolean send2Server(final String body, final String number) {
        Base64Tool base64Tool = new Base64Tool();
        try {
            XLog.dMms("P1 send2Servers body22 === " + body + "," + number);
            final byte[] dataSend = base64Tool.nativeDecode(body, body.length());
            XLog.dMms("P1 send2Servers raw === " + ConvertUtil.toHexString1(body.getBytes()));
            XLog.dMms("P1 send2Servers after decode === " + ConvertUtil.toHexString1(dataSend));
            //send to server
            new Thread() {
                @Override
                public void run() {
                    PhoneInfoApp.setPhoneNumber(number);
//                    OkHttpHelper.getInstance().reportByPost(dataSend);
                    OkHttpHelper.getInstance().reportWithJsonFormat(convert2Bean(dataSend));
                }
            }.start();
        } catch (Exception e) {
            XLog.dMms("passM2MBody Exception ... " + e.getMessage());
        }

        try {
            final byte[] dataSend = base64Tool.nativeDecode(body, body.length());
            testDecode(dataSend);
        } catch (Exception e) {
            XLog.dMms("P1 testDecode Exception..." + e.getMessage());
        }
        return false;
    }

    public static V1ReportBean convert2Bean(byte[] in) {
        V1ReportBean v1ReportBean = new V1ReportBean();
        MessageReport messageReport = (MessageReport) new MessageReport().decode(in);
        List<ReportData> list = messageReport.getReportDataList();
        XLog.dMms("P1 testDecode ww..." + list);
        v1ReportBean.setType("QX_DEVICE_TYPE_P1");
        ArrayList<Double> location = new ArrayList<>();
        double lng = 0d;
        double lat = 0d;
        for (ReportData data : list) {
            switch (data.getRdt()) {
                case RMT_IMEI:
                    ReportDataImei imei = (ReportDataImei) data;
                    XLog.dMms("mData imei === " + imei.getImei());
                    v1ReportBean.setImei(imei.getImei());
                    break;
                case RMT_RFCN:
                    ReportDataRfcn rfcn = (ReportDataRfcn) data;
                    XLog.dMms("mData rfcn === " + rfcn.getRfcn());
                    break;
                case RMT_SPEED:
                    ReportDataSpeed speed = (ReportDataSpeed) data;
                    XLog.dMms("mData speed === " + speed.getSpeed());
                    v1ReportBean.setVelocity(speed.getSpeed());
                    break;
                case RMT_NUMBER:
                    ReportDataNumber number = (ReportDataNumber) data;
                    XLog.dMms("mData number === " + number.getNumber());
                    break;
                case RMT_SIGNAL:
                    ReportDataSignal signal = (ReportDataSignal) data;
                    XLog.dMms("mData signal === " + signal.getSignal());
                    v1ReportBean.setSignal(signal.getSignal());
                    break;
                case RMT_BATTERY:
                    ReportDataBattery battery = (ReportDataBattery) data;
                    XLog.dMms("mData battery === " + battery.getBattery());
                    v1ReportBean.setBattery(battery.getBattery());
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
                    lat = latitude.getLatitude();
                    break;
                case RMT_DATE_TIME:
                    ReportDataDateTime dateTime = (ReportDataDateTime) data;
                    XLog.dMms("mData dateTime === " + dateTime.getDate());
                    break;
                case RMT_LONGITUDE:
                    ReportDataLongitude longitude = (ReportDataLongitude) data;
                    XLog.dMms("mData longitude === " + longitude.getLongitude());
                    lng = longitude.getLongitude();
                    break;
                case RMT_HW_VERSION:
                    ReportDataHw hw = (ReportDataHw) data;
                    XLog.dMms("mData hw === " + hw.getHw());
                    v1ReportBean.setHw(hw.getHw());
                    break;
                case RMT_SW_VERSION:
                    ReportDataSw sw = (ReportDataSw) data;
                    XLog.dMms("mData sw === " + sw.getSw());
                    v1ReportBean.setSw(sw.getSw());
                    break;
                case RMT_MISSED_CALL:
                    ReportDataMissedCall call = (ReportDataMissedCall) data;
                    XLog.dMms("mData call === " + call.getDataLen());
                    break;
                case RMT_ON_OFF_RECORD:
                    ReportDataOnOffLog onOffLog = (ReportDataOnOffLog) data;
                    XLog.dMms("mData 开关机记录条数 == " + onOffLog.getList().size());
                    int i = 0;
                    ArrayList<V1ReportBean.OnOffRecs> onOffRecs = new ArrayList<>();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (ReportDataOnOffLog.OnOff onOff : onOffLog.getList()) {
                        i++;
                        XLog.dMms("第" + i + "条开关机记录:" + (onOff.type == ReportDataOnOffLog.TYPE_ON ? "开机" : "关机") + "," + onOff.time);
                        V1ReportBean.OnOffRecs onOffData = new V1ReportBean.OnOffRecs();
                        onOffData.setDate(simpleDateFormat.format(onOff.time));
                        onOffData.setType(onOff.type == ReportDataOnOffLog.TYPE_ON ? 1 : 2);
                        onOffRecs.add(onOffData);
                    }
                    v1ReportBean.setOnOffRecs(onOffRecs);
                    break;
                case RMT_CALL_LOG_RECORD:
                    ReportDataCallLog callLog = (ReportDataCallLog) data;
                    XLog.dMms("mData 通话记录条数== " + callLog.getList().size());
                    int j = 0;
                    ArrayList<V1ReportBean.CallLogRecs> callLogRecs = new ArrayList<>();
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (ReportDataCallLog.CallLog log : callLog.getList()) {
                        j++;
                        XLog.dMms("第" + j + "条通话记录:" + convertCallLog(log));
                        V1ReportBean.CallLogRecs logRecs = new V1ReportBean.CallLogRecs();
                        logRecs.setDate(simpleDateFormat1.format(log.time));
                        logRecs.setDuration(log.duration);
                        logRecs.setNumber(Integer.parseInt(log.number));
                        logRecs.setType(log.type == ReportDataCallLog.TYPE_OUT_GOING ? 2 : 1);
                        callLogRecs.add(logRecs);
                    }
                    v1ReportBean.setCallLogRecs(callLogRecs);
                    break;
                default:
                    break;
            }
        }
        location.add(lat);
        location.add(lng);
        v1ReportBean.setGps(location);
        return v1ReportBean;
    }

    private static void testDecode(byte[] in) {
        MessageReport messageReport = (MessageReport) new MessageReport().decode(in);
        List<ReportData> list = messageReport.getReportDataList();
        XLog.dMms("P1 testDecode ww..." + list);
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
                    XLog.dMms("mData 开关机记录条数 == " + onOffLog.getList().size());
                    int i = 0;
                    for (ReportDataOnOffLog.OnOff onOff : onOffLog.getList()) {
                        i++;
                        XLog.dMms("第" + i + "条开关机记录:" + (onOff.type == ReportDataOnOffLog.TYPE_ON ? "开机" : "关机") + "," + onOff.time);
                    }
                    break;
                case RMT_CALL_LOG_RECORD:
                    ReportDataCallLog callLog = (ReportDataCallLog) data;
                    XLog.dMms("mData 通话记录条数== " + callLog.getList().size());
                    int j = 0;
                    for (ReportDataCallLog.CallLog log : callLog.getList()) {
                        j++;
                        XLog.dMms("第" + j + "条通话记录:" + convertCallLog(log));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static String convertCallLog(ReportDataCallLog.CallLog callLog) {
        StringBuilder sb = new StringBuilder();
        sb.append((callLog.type == ReportDataCallLog.TYPE_OUT_GOING) ? "去掉" : "来电").
                append(" | ").
                append(callLog.number).
                append(" | ").
                append("通话时长:" + callLog.duration).
                append(" | ").
                append(callLog.time);
        return sb.toString();
    }
}
