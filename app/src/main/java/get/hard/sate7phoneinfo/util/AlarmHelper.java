package get.hard.sate7phoneinfo.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.util.Map;

import get.hard.sate7phoneinfo.AlarmReceiver;
import get.hard.sate7phoneinfo.PhoneInfoApp;
import get.hard.sate7phoneinfo.ReportService;
import get.hard.sate7phoneinfo.XLog;

import static get.hard.sate7phoneinfo.ReportService.REPORT_TYPE;
import static get.hard.sate7phoneinfo.ReportService.REPORT_TYPE_CONTINUE;

public class AlarmHelper {
    private static final String TAG = "AlarmHelper";
    private static final boolean JUST_DEBUG = false;
    private static final String FrequencyTAG = "setting_frequency";

    public static void startNormalReport(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        Map info = preferences.getAll();
        int minute = Integer.parseInt(preferences.getString(FrequencyTAG, "60"));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmReceiver.ACTION_ALARM);
        intent.setPackage(context.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerMillion = JUST_DEBUG ? 10 * 1000 : minute * 60 * 1000;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerMillion + System.currentTimeMillis(), pendingIntent);
        XLog.dReport("startNormalReport ww22... " + info + " ,, " + minute);
        Intent intentService = new Intent(context, ReportService.class);
        intentService.putExtra(REPORT_TYPE, ReportService.REPORT_TYPE_NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    public static void startContinueReport(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmReceiver.ACTION_ALARM);
        intent.putExtra(REPORT_TYPE, REPORT_TYPE_CONTINUE);
        intent.setPackage(context.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerMillion = 5000;//5 seconds
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerMillion + System.currentTimeMillis(), pendingIntent);
        XLog.dReport("startContinueReport ww22... ");
        Intent intentService = new Intent(context, ReportService.class);
        intentService.putExtra(REPORT_TYPE, ReportService.REPORT_TYPE_NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    public static void startOnceReport(Context context) {
        XLog.dReport("startOnceReport start ...");
        Intent intent = new Intent(context, ReportService.class);
        intent.putExtra(REPORT_TYPE, ReportService.REPORT_TYPE_FIRST);
        context.startService(intent);
    }
}
