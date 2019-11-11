package get.hard.sate7phoneinfo.client;

import android.content.Context;
import android.location.Location;
import android.os.Build;

import com.blankj.utilcode.util.PhoneUtils;
import com.qx.protocol.ReportDataAltitude;
import com.qx.protocol.ReportDataBattery;
import com.qx.protocol.ReportDataCallLog;
import com.qx.protocol.ReportDataDateTime;
import com.qx.protocol.ReportDataHw;
import com.qx.protocol.ReportDataImei;
import com.qx.protocol.ReportDataLatitude;
import com.qx.protocol.ReportDataLongitude;
import com.qx.protocol.ReportDataNumber;
import com.qx.protocol.ReportDataOnOffLog;
import com.qx.protocol.ReportDataRfcn;
import com.qx.protocol.ReportDataSignal;
import com.qx.protocol.ReportDataSw;

import java.util.ArrayList;
import java.util.Date;

import get.hard.sate7phoneinfo.ReportService;
import get.hard.sate7phoneinfo.XLog;
import get.hard.sate7phoneinfo.util.InfoGetter;


public class NormalReporter extends AndroidV1Reporter {
    public NormalReporter(Context context) {
        super(context);
    }

    @Override
    public void buildReportData() {
//        上报数据：终端IMEI， 终端时间， 当前系统软件版本，
//        硬件版本以及终端的位置信息。
//        到上报时间即上报， 如果当前设备获得到位置信息，则携带位置信息，如果没有获得到位置信息， 则忽略位置信息
//        硬件版本以及终端的位置信息、
//        电话号码、海拔、经度、纬度、电量、
//        信号频点、信号质量、速度、开/关机记录、通话记录
//        手持终端（不支持海拔，速度， 可以忽略）
        mMessageReport.addReportData(new ReportDataImei(mApp.getImei()));
        XLog.dReport("NormalReporter buildReportData imei = " + mApp.getImei());
        mMessageReport.addReportData(new ReportDataDateTime(new Date()));
        Location location = mApp.getLocation();
        if (location != null) {
            XLog.dReport("NormalReporter buildReportData location = " + location.getLongitude() + "," + location.getLatitude() + "," + location.getAltitude());
            mMessageReport.addReportData(new ReportDataLongitude((float) location.getLongitude()));
            mMessageReport.addReportData(new ReportDataLatitude((float) location.getLatitude()));
            mMessageReport.addReportData(new ReportDataAltitude((float) location.getAltitude()));
        }
        mMessageReport.addReportData(new ReportDataSw("Android:" + Build.VERSION.SDK_INT));
        mMessageReport.addReportData(new ReportDataHw(Build.HARDWARE));
        XLog.dReport("NormalReporter buildReportData getPhoneNumber = " + mApp.getPhoneNumber());
        mMessageReport.addReportData(new ReportDataNumber(mApp.getPhoneNumber()));
        mMessageReport.addReportData(new ReportDataBattery(mApp.getBattery()));
        mMessageReport.addReportData(new ReportDataSignal(mApp.getSignalStrength()));
        ReportDataCallLog reportDataCallLog = new ReportDataCallLog();
        ArrayList<ReportDataCallLog.CallLog> logs = InfoGetter.getCallLog(context);
        for (ReportDataCallLog.CallLog log : logs) {
//            XLog.dReport("reportDataCallLog log ... " + log.number + "," + log.type + "," + log.duration + "," + log.time);
            reportDataCallLog.addCallLogRec(log);
        }
        mMessageReport.addReportData(reportDataCallLog);
    }

    @Override
    public String buildMmsData() {
        return "NormalReport";
    }

    @Override
    public int getMmsType() {
        return ReportService.REPORT_TYPE_NORMAL;
    }
}
