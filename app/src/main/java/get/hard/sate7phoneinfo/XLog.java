package get.hard.sate7phoneinfo;

import android.util.Log;

public class XLog {
    private static final String TAG = "XLog";
    private static final String TAG_REPORT_DATA = "ReportData";
    private static final String TAG_MMS = "MmsDebug";
    private static final String TAG_REPORT_FREQUENCY = "ReportFrequency";
    private static final String TAG_PERMISSION = "Permission";
    private static final String TAG_LOCATION = "WLJ_Location";

    public static void d(String msg) {
        Log.d(TAG, "" + msg);
    }

    public static void d(String tag,String msg) {
        Log.d(tag, "" + msg);
    }

    public static void dLocation(String msg) {
        Log.d(TAG_LOCATION, "" + msg);
    }

    public static void dReport(String msg) {
        Log.d(TAG_REPORT_DATA, "" + msg);
    }

    public static void dMms(String msg) {
        Log.d(TAG_MMS, "" + msg);
    }

    public static void dPermission(String msg) {
        Log.d(TAG_PERMISSION, "" + msg);
    }

    public static void dReportFrequency(String msg) {
        Log.d(TAG_REPORT_FREQUENCY, "" + msg);
    }
}
