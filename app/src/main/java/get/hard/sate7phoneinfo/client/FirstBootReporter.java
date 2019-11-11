package get.hard.sate7phoneinfo.client;

import android.content.Context;
import android.location.Location;
import android.os.Build;

import com.blankj.utilcode.util.PhoneUtils;
import com.qx.protocol.ReportDataAltitude;
import com.qx.protocol.ReportDataBattery;
import com.qx.protocol.ReportDataDateTime;
import com.qx.protocol.ReportDataHw;
import com.qx.protocol.ReportDataImei;
import com.qx.protocol.ReportDataLatitude;
import com.qx.protocol.ReportDataLongitude;
import com.qx.protocol.ReportDataSignal;
import com.qx.protocol.ReportDataSw;

import java.util.Date;

import get.hard.sate7phoneinfo.ReportService;
import get.hard.sate7phoneinfo.XLog;


public class FirstBootReporter extends AndroidV1Reporter{
    public FirstBootReporter(Context context) {
        super(context);
    }

    @Override
    public void buildReportData() {
//         上报数据：
//         终端IMEI， 终端时间， 当前系统软件版本，硬件版本以及终端的位置信息。
//         到上报时间即上报， 如果当前设备获得到位置信息，则携带位置信息，
//         如果没有获得到位置信息， 则忽略位置信息。
        XLog.dReport("buildReportData ..." + mApp.getImei());
        mMessageReport.addReportData(new ReportDataImei(mApp.getImei()));
        mMessageReport.addReportData(new ReportDataDateTime(new Date()));
        mMessageReport.addReportData(new ReportDataSw("Android:" + Build.VERSION.SDK_INT));
        mMessageReport.addReportData(new ReportDataHw(Build.HARDWARE));
        mMessageReport.addReportData(new ReportDataBattery(mApp.getBattery()));
        mMessageReport.addReportData(new ReportDataSignal(mApp.getSignalStrength()));
        Location location = mApp.getLocation();
        if(location != null){
            mMessageReport.addReportData(new ReportDataLongitude((float) location.getLongitude()));
            mMessageReport.addReportData(new ReportDataLatitude((float) location.getLatitude()));
            mMessageReport.addReportData(new ReportDataAltitude((float) location.getAltitude()));
        }
        XLog.dReport("FirstBootReporter buildReportData ... ");
    }

    @Override
    public String buildMmsData() {
        return "FirstReport Mms";
    }

    @Override
    public int getMmsType() {
        return ReportService.REPORT_TYPE_FIRST;
    }
}
