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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import get.hard.sate7phoneinfo.PhoneInfoApp;
import get.hard.sate7phoneinfo.XLog;
import get.hard.sate7phoneinfo.client.OkHttpHelper;

public class M2MHelper {
    public static boolean send2Server(String body, final String number) {
        try {
            String[] data = body.trim().split(",");
            XLog.dMms("passM2MBody ww ... " + body + "," + number + "," + data.length + "," + Arrays.toString(data));
            String time = data[0].split(":")[1];
            String imei;
            if (data[1].split(":").length > 1) {
                imei = data[1].split(":")[1];
            } else {
                imei = "869377037720655";
            }

            String lng = data[2].split(":")[1];
            String lat = data[3].split(":")[1];
            XLog.dMms("pass body ww ... " + time + "," + imei + "," + lng + "," + lat + "," + number);
            MessageReport messageReport = new MessageReport();
            messageReport.addReportData(new ReportDataImei(imei));
            messageReport.addReportData(new ReportDataDateTime(new Date()));
            messageReport.addReportData(new ReportDataLongitude(Float.parseFloat(lng.replace("E", ""))));
            messageReport.addReportData(new ReportDataLatitude(Float.parseFloat(lat.replace("N", ""))));
            messageReport.addReportData(new ReportDataAltitude(0f));
            messageReport.addReportData(new ReportDataSw("M2M_V1.0"));
            messageReport.addReportData(new ReportDataHw("M2M"));
            messageReport.addReportData(new ReportDataNumber(number));
            PhoneInfoApp.setPhoneNumber(number);
            messageReport.addReportData(new ReportDataBattery(92));
            messageReport.addReportData(new ReportDataSignal(95));
            final byte[] sendData = messageReport.encode();
            testDecode(sendData);
            new Thread() {
                @Override
                public void run() {
                    PhoneInfoApp.setPhoneNumber(number);
                    OkHttpHelper.getInstance().reportByPost(sendData);
                }
            }.start();
            return true;
        } catch (Exception e) {
            XLog.dMms("passM2MBody Exception ... " + e.getMessage());
        }
        return false;
    }

    private static void testDecode(byte[] in) {
        XLog.dMms("testDecode ww...");
        MessageReport messageReport = (MessageReport) new MessageReport().decode(in);
        List<ReportData> list = messageReport.getReportDataList();
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
}
