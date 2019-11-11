package get.hard.sate7phoneinfo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;

import get.hard.sate7phoneinfo.client.AdjustReporter;
import get.hard.sate7phoneinfo.client.AndroidV1Reporter;
import get.hard.sate7phoneinfo.client.FirstBootReporter;
import get.hard.sate7phoneinfo.client.NormalReporter;

public class ReportService extends IntentService {
    public ReportService() {
        this("ReportService");
    }

    public static final String REPORT_TYPE = "report_type";
    public static final int REPORT_TYPE_FIRST = 110;
    public static final int REPORT_TYPE_NORMAL = 111;
    public static final int REPORT_TYPE_ADJUST = 112;
    public static final int REPORT_TYPE_CONTINUE = 113;

    public ReportService(String name) {
        super(name);
    }

    private Notification mNotification;
    private Notification.Builder mNotificationBuilder;
    private NotificationManager notificationManager;
    private NotificationChannel mChannel;
    private String mNotificationChannelId= "ReportService";
    private String mNotificationChannelName= "ReportService";
    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d(BootReceiver.TAG, "onCreate 22... ");
        XLog.dReport("ReportService onCreate()... ");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new Notification.Builder(this).
                setSmallIcon(R.drawable.ic_done).setAutoCancel(true).setContentTitle("DataReportSuccess");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationBuilder.setChannelId(mNotificationChannelId);
            mChannel = new NotificationChannel(mNotificationChannelId,mNotificationChannelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        mNotification = mNotificationBuilder.build();
        startForeground(123, mNotification);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int type = intent.getIntExtra(REPORT_TYPE, REPORT_TYPE_NORMAL);
//        Log.d(BootReceiver.TAG, "onHandleIntent ... " + intent.getAction() + "," + type);
        XLog.dReport("ReportService onHandleIntent()... " + intent + "," + type);
        AndroidV1Reporter reporter = null;
        switch (type) {
            case REPORT_TYPE_FIRST:
                reporter = new FirstBootReporter(this);
                break;
            case REPORT_TYPE_NORMAL:
                reporter = new NormalReporter(this);
                break;
            case REPORT_TYPE_ADJUST:
                reporter = new AdjustReporter(this);
                break;
        }
        if (reporter != null) {
            reporter.sendToServer(this);
        }
    }
}
