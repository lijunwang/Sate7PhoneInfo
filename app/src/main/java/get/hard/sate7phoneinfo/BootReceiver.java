package get.hard.sate7phoneinfo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.Utils;

import java.util.List;

import get.hard.sate7phoneinfo.db.BootShutRecordHelper;
import get.hard.sate7phoneinfo.util.AlarmHelper;

import static get.hard.sate7phoneinfo.ReportService.REPORT_TYPE;


public class BootReceiver extends BroadcastReceiver {
    public static final String TAG = "BootReceiver";
    private final String FIRST_BOOT_INFO_TAG = "first_boot_info";
    private final String FIRST_BOOT = "first_boot";
    private final String FIRST_BOOT_TIME = "first_boot_time";
    private final String FIRST_BOOT_REPORT_SUCCESS = "first_boot_report_status";
    //    private final long TIME_DELAY = 30 * 60 * 1000;//30 minutes
    private final long TIME_DELAY = 1 * 30 * 1000;//for Test 1 minutes

    private boolean mAutoSend = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive ww22 ... " + intent.getAction());
        Intent intentAc = new Intent(context,MainActivity.class);
        intentAc.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intentAc);
        if (mAutoSend && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            long id = new BootShutRecordHelper(context).insertPowerOn();
            Log.d(TAG, "onReceive recod ... " + id);
            if (isFirstBoot(context)) {
                //start report after 30 minutes, if success, set FIRST_BOOT_REPORT_SUCCESS as true
                SharedPreferences sharedPreferences = context.getSharedPreferences(FIRST_BOOT_INFO_TAG, Context.MODE_PRIVATE);
                sharedPreferences.edit().putLong(FIRST_BOOT_TIME, System.currentTimeMillis()).commit();
                executeReportDelay(TIME_DELAY, context);
            } else if (!hasReportFirst(context)) {
                //first boot report failed, so we report is right now.
                SharedPreferences sharedPreferences = context.getSharedPreferences(FIRST_BOOT_INFO_TAG, Context.MODE_PRIVATE);
                long savedTime = sharedPreferences.getLong(FIRST_BOOT_TIME, 0);
                long diff = System.currentTimeMillis() - savedTime;
                Log.d(TAG, "onReceive ... not ww the first boot " + savedTime + "," + diff + "," + (diff > TIME_DELAY));
                if (diff > TIME_DELAY) {
                    AlarmHelper.startOnceReport(context);
                } else {
                    executeReportDelay(diff, context);
                }
            }
        } else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
            long id = new BootShutRecordHelper(context).insertPowerOff();
            Log.d(TAG, "ACTION_SHUTDOWN ... " + id);
        } else if (SEND_MMS_V1.equals(intent.getAction())) {
            int smsSendType = intent.getIntExtra(SEND_MMS_TYPE, 0);
            Log.d(TAG, "first report success ... " + intent + "," + smsSendType);
            if (smsSendType == ReportService.REPORT_TYPE_FIRST) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(FIRST_BOOT_INFO_TAG, Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(FIRST_BOOT_REPORT_SUCCESS, true).commit();
            }
        } else if ("com.wlj.test".equals(intent.getAction())) {
            executeReportDelay(10, context);
        }
    }


    private void executeReportDelay(long timeMillion, Context context) {
        Log.d(TAG, "executeReportDelay ww ... " + timeMillion);
        Intent intentService = new Intent(context, ReportService.class);
        intentService.putExtra(REPORT_TYPE, ReportService.REPORT_TYPE_FIRST);
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intentService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeMillion, pendingIntent);
    }

    private boolean isFirstBoot(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FIRST_BOOT_INFO_TAG, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(FIRST_BOOT, true)) {
            sharedPreferences.edit().putBoolean(FIRST_BOOT, false).commit();
            return true;
        }
        return false;
    }

    private boolean hasReportFirst(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FIRST_BOOT_INFO_TAG, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(FIRST_BOOT_REPORT_SUCCESS, false)) {
            return true;
        }
        return false;
    }

    private static final String SEND_MMS_V1 = "com.7mate.send.sms";
    private static final String SEND_MMS_TYPE = "type";

    public static void sendSmsSilent(final String phoneNumber, final String content, int type) {
        if (TextUtils.isEmpty(content)) return;
        Intent intent = new Intent(SEND_MMS_V1);
        intent.putExtra(SEND_MMS_TYPE, type);
        PendingIntent sentIntent = PendingIntent.getBroadcast(Utils.getApp(), 0, intent, 0);
        SmsManager smsManager = SmsManager.getDefault();
        if (content.length() >= 70) {
            List<String> ms = smsManager.divideMessage(content);
            for (String str : ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
        }
    }
}
