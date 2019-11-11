package get.hard.sate7phoneinfo;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.location.Location;
import android.text.TextUtils;

import com.blankj.utilcode.util.Utils;
import com.tencent.bugly.crashreport.CrashReport;

public class PhoneInfoApp extends Application {
    private static Context context;
    private static int mBattery;
    private Location mLocation;
    private int mSignalStrength = 0;
    private static String mPhoneNumber = "";
    private String imei = "";

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        context = this;
        CrashReport.initCrashReport(getApplicationContext(), "78f5714561", true);
    }

    public void setBattery(int battery) {
        mBattery = battery;
    }

    public Location getLocation() {
        XLog.dReport("getLocation = " + mLocation);
        return mLocation;
    }

    public int getBattery() {
        return mBattery;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public void setSignalStrength(int signalStrength) {
        this.mSignalStrength = signalStrength;
    }

    public int getSignalStrength() {
        return mSignalStrength;
    }

    public static String getPhoneNumber() {
        return TextUtils.isEmpty(mPhoneNumber)  ? "01234567891" : mPhoneNumber;
    }

    public static void setPhoneNumber(String number) {
        mPhoneNumber = number;
    }

    public void setImei(String imei){
        this.imei = imei;
    }
    public String getImei(){
        return imei;
    }

    public static Context getContext() {
        return context;
    }

    private static PendingIntent mReportServiceIntent;
    public static void setFrequencyReportIntent(PendingIntent pendingIntent){
        mReportServiceIntent = pendingIntent;
    }
    public static PendingIntent getFrequencyReportIntent(){
        return mReportServiceIntent;
    }
}
