package get.hard.sate7phoneinfo.client;

import android.util.Base64;
import android.util.Log;

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

import java.util.List;

import get.hard.sate7phoneinfo.XLog;
import get.hard.sate7phoneinfo.util.ConvertUtil;

public class Test {
    public static void main(String[] args) {
        System.out.println("main ... ");
    }

    public void testDecode() {
        XLog.dMms("nani testDecode");
        String body = "UVgBEF9ob21lc2NyZWUDMAAAEwoJCxYXhgEGAAM2VW9RWFQtUDEtVjEuMy4xAAAAAAAAAFZY";
        byte[] base64Byte = Base64.decode(body,Base64.DEFAULT);
        try {
            XLog.dMms("testDecode raw ...");
            MessageReport messageReport = (MessageReport) new MessageReport().decode(base64Byte);
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
        } catch (Exception e) {
            XLog.dMms("testDecode Exception ww .... " + e);
            Log.e("MmsDebug","ee:" + e);
        }
    }
}
