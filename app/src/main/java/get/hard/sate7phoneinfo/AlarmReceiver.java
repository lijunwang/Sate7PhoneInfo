package get.hard.sate7phoneinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import get.hard.sate7phoneinfo.util.AlarmHelper;

import static get.hard.sate7phoneinfo.ReportService.REPORT_TYPE;
import static get.hard.sate7phoneinfo.ReportService.REPORT_TYPE_CONTINUE;
import static get.hard.sate7phoneinfo.ReportService.REPORT_TYPE_NORMAL;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String ACTION_ALARM = "com.stat7.alarm";
    public static final String TAG = "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        XLog.dReportFrequency("onReceive AlarmReceiver ... " + intent.getAction() + "," + intent.getIntExtra(REPORT_TYPE,REPORT_TYPE_NORMAL));
        if(ACTION_ALARM.equals(intent.getAction())){
            Intent go = new Intent(context,MainActivity.class);
            go.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(go);
            if(intent.getIntExtra(REPORT_TYPE,REPORT_TYPE_NORMAL) == REPORT_TYPE_CONTINUE){
                AlarmHelper.startContinueReport(context);
            }else{
                AlarmHelper.startNormalReport(context);
            }

        }
    }
}
