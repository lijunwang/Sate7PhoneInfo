package get.hard.sate7phoneinfo.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.qx.protocol.ReportDataCallLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import get.hard.sate7phoneinfo.AlarmReceiver;
import get.hard.sate7phoneinfo.XLog;

public class InfoGetter {
    public static ArrayList<ReportDataCallLog.CallLog> getCallLog(Context context) {
        Uri callLog = CallLog.Calls.CONTENT_URI;
        @SuppressLint("MissingPermission") Cursor cursor = context.getContentResolver().query(callLog,
                new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DURATION, CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE},
                null, null, "date DESC");
        ArrayList<ReportDataCallLog.CallLog> callLogs = new ArrayList<>(50);
        if (cursor.moveToFirst()) {
            do {
                String number = cursor.getString(0);
                int type = cursor.getInt(1);
                long duration = cursor.getLong(2);
                String name = cursor.getString(3);
                String date = cursor.getString(4);
                XLog.d("getCallLog ----" + number + "," + type + "," + duration + "," + name + "," + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Long.parseLong(date)));
                ReportDataCallLog.CallLog log = new ReportDataCallLog.CallLog();
                log.duration = (int) duration;
                log.number = number;
                log.time = new Date(Long.parseLong(date));
                if (type == CallLog.Calls.INCOMING_TYPE) {
                    log.type = ReportDataCallLog.TYPE_INCOMING;
                } else if (type == CallLog.Calls.OUTGOING_TYPE) {
                    log.type = ReportDataCallLog.TYPE_OUT_GOING;
                }
                callLogs.add(log);
            } while (cursor.moveToNext());
        }
        return callLogs;
    }

    public static PendingIntent get(Context context){
        Intent intent = new Intent(AlarmReceiver.ACTION_ALARM);
        intent.setPackage(context.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @SuppressLint("MissingPermission")
    public static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        if (TextUtils.isEmpty(imei)) {
            return PlatformRelated.getFakeImei(context);
        } else {
            return imei;
        }
    }
}
