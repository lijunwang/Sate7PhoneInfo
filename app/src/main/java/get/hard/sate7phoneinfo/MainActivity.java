package get.hard.sate7phoneinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.CallLog;
import android.provider.Settings;
import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

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

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import get.hard.sate7phoneinfo.client.OkHttpHelper;
import get.hard.sate7phoneinfo.util.AlarmHelper;
import get.hard.sate7phoneinfo.util.Base64Tool;
import get.hard.sate7phoneinfo.util.ConvertUtil;
import get.hard.sate7phoneinfo.util.InfoGetter;
import get.hard.sate7phoneinfo.util.M2MHelper;
import get.hard.sate7phoneinfo.util.P1Helper;

public class MainActivity extends AppCompatActivity implements LocationListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "PhoneInfo";

    private RecyclerView mRecyclerView;
    private PhoneInfoAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ArrayList<PhoneInfo> mInfoList = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private TelephonyManager mTelephonyManager;
    private LocationManager mLocationManager;
    private PhoneInfo mImei;
    private PhoneInfo mSoftware;
    private PhoneInfo mHareware;
    private PhoneInfo mManufacture;
    private PhoneInfo mBrand;
    private PhoneInfo mPhoneNub;
    private PhoneInfo mLocation;
    private PhoneInfo mBattery;
    private PhoneInfo mSignalStrength;
    private PhoneInfo mSignalCallLog;
    private static final int REQUEST_PERMISSION = 0x123;
    private String[] mPermissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALL_LOG, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS};
    private PhoneInfoApp mApp;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (PhoneInfoApp) getApplication();
        setContentView(R.layout.activity_main);
        initActionBar();
        initPhoneInfo();
        initViews();
        permissionCheck();

//        justTest();
        registerSmsParse();
    }

    private void initActionBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mAdapter = new PhoneInfoAdapter(mInfoList);
        mNavigationView = findViewById(R.id.navigationView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new ItemDecoration());
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initPhoneInfo() {
        mImei = new PhoneInfo(getResources().getString(R.string.info_title_imei), "");
        mSoftware = new PhoneInfo(getResources().getString(R.string.info_title_software), "Android " + Build.VERSION.SDK_INT);
        mHareware = new PhoneInfo(getResources().getString(R.string.info_title_hardware), Build.HARDWARE);
        mManufacture = new PhoneInfo(getResources().getString(R.string.info_title_manufacture), Build.MANUFACTURER);
        mBrand = new PhoneInfo(getResources().getString(R.string.info_title_brand), Build.BRAND);
        mPhoneNub = new PhoneInfo(getResources().getString(R.string.info_title_number), "");
        mLocation = new PhoneInfo(getResources().getString(R.string.info_title_lanlong), "");
        mBattery = new PhoneInfo(getResources().getString(R.string.info_title_battery), "");
        mSignalStrength = new PhoneInfo(getResources().getString(R.string.info_title_signal), "");
        mSignalCallLog = new PhoneInfo(getResources().getString(R.string.info_title_callLog), getResources().getString(R.string.detail));
        mSignalCallLog.setType(PhoneInfo.PhoneInfoType.COLLECTION);
        mInfoList.add(mImei);
        mInfoList.add(mSoftware);
        mInfoList.add(mHareware);
        mInfoList.add(mManufacture);
        mInfoList.add(mBrand);
        mInfoList.add(mPhoneNub);
        mInfoList.add(mLocation);
        mInfoList.add(mBattery);
        mInfoList.add(mSignalStrength);
        mInfoList.add(mSignalCallLog);
    }

    private void permissionCheck() {
        XLog.dPermission("permissionCheck ... ");
        ActivityCompat.requestPermissions(this, mPermissions, REQUEST_PERMISSION);
    }

    @SuppressLint("MissingPermission")
    private void updateInfoForREAD_PHONE_STATE() {
//        mImei.setContent(mTelephonyManager.getDeviceId());
        mImei.setContent(InfoGetter.getImei(this));
        mPhoneNub.setContent(mTelephonyManager.getLine1Number());
        mAdapter.notifyDataSetChanged();
        mApp.setPhoneNumber(mTelephonyManager.getLine1Number());
        mApp.setImei(InfoGetter.getImei(this));
    }

    private void updateInfoLocation(Location location) {
        mLocation.setContent(getResources().getString(R.string.lan_lon, location.getLongitude(), location.getLatitude()));
        mAdapter.notifyDataSetChanged();
        mApp.setLocation(location);
    }

    private void updateInfoBattery(int level) {
        mBattery.setContent(level + " % ");
        mAdapter.notifyDataSetChanged();
    }

    private void updateInfoSignalStrength(int level) {
        mSignalStrength.setContent("" + level);
        mAdapter.notifyDataSetChanged();
        mApp.setSignalStrength(level);
    }

    private AlertDialog mLocationDialog;

    @SuppressLint("MissingPermission")
    private void updateInfoForACCESS_FINE_LOCATION() {
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        mApp.setLocation(location);
        if (location != null) {
            float lat = (float) location.getLatitude();
            float lon = (float) location.getLongitude();
            mLocation.setContent(getResources().getString(R.string.lan_lon, lon, lat));
            mAdapter.notifyDataSetChanged();
        }
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        XLog.dLocation("getLocation gps:" + gps + ",network=" + network);
        if (gps) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        } else if (network) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
        }
        if (!gps && !network) {
            if (mLocationDialog == null) {
                mLocationDialog = new AlertDialog.Builder(this).setTitle(R.string.dialog_location_title).
                        setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton(R.string.dialog_open, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            intent.setAction(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                        mLocationDialog.dismiss();
                    }
                }).show();
            }
        }

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        XLog.dPermission("onRequestPermissionsResult ww :" + requestCode + "," + Arrays.toString(permissions) + "," + Arrays.toString(grantResults));
        if (permissions.length == 0 || grantResults.length == 0) {
            return;
        }
//        Manifest.permission.READ_PHONE_STATE
        if (requestCode == REQUEST_PERMISSION && permissions[0].equals(mPermissions[0]) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            XLog.dPermission("onRequestPermissionsResult 0 ..." + mPermissions[0]);
            updateInfoForREAD_PHONE_STATE();
        }

//        Manifest.permission.ACCESS_FINE_LOCATION
        if (requestCode == REQUEST_PERMISSION && permissions[1].equals(mPermissions[1]) && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            XLog.dPermission("onRequestPermissionsResult 1 ..." + mPermissions[1]);
            updateInfoForACCESS_FINE_LOCATION();
        }

        if (requestCode == REQUEST_PERMISSION && permissions[2].equals(mPermissions[2]) && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
//            getCallLog();
            XLog.dPermission("onRequestPermissionsResult 2 ..." + mPermissions[2]);
            InfoGetter.getCallLog(this);
        }

        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission ok", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        XLog.d("onOptionsItemSelected ...");
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCallLog() {
        Uri callLog = CallLog.Calls.CONTENT_URI;
        @SuppressLint("MissingPermission") Cursor cursor = getContentResolver().query(callLog,
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
    }

    @Override
    public void onLocationChanged(Location location) {
        XLog.dLocation("location onLocationChanged ... " + location);
        updateInfoLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        XLog.dLocation("location onStatusChanged ... " + provider + "," + status + "," + extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        XLog.d("location onProviderEnabled ... " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        XLog.d("location onProviderDisabled ... " + provider);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBatteryReceiver, mBatteryFilter);
        registerTimeTick();
//        Toast.makeText(this, PlatformRelated.isV1() ? "V1" : "no V1", Toast.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            updateInfoForACCESS_FINE_LOCATION();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBatteryReceiver);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
//        super.onBackPressed();
        /*Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
        intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
        startActivity(intent);*/
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unRegisterSmsParse();
    }

    private BatteryReceiver mBatteryReceiver = new BatteryReceiver();
    private IntentFilter mBatteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    @SuppressLint("MissingPermission")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.once_report:
                mApp.setPhoneNumber(mTelephonyManager.getLine1Number());
                AlarmHelper.startOnceReport(this);
                break;
            case R.id.continue_report:
                AlarmHelper.startContinueReport(this);
                break;
            case R.id.interval_report:
                AlarmHelper.startContinueReport(this);
                break;
            case R.id.menu_item1:
//                startActivity(new Intent(this, DecryptActivity.class));
//                CrashReport.initCrashReport(getApplicationContext());
                break;
        }
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        return false;
    }

    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            XLog.d("onReceive BatteryReceiver ... " + intent.getAction());
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                if (level != -1) {
                    updateInfoBattery(level);
                    PhoneInfoApp app = (PhoneInfoApp) getApplication();
                    app.setBattery(level);
                }
            }
        }
    }

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            try {
                Method method = signalStrength.getClass().getDeclaredMethod("getDbm");
                method.setAccessible(true);
                int strength = (int) method.invoke(signalStrength);
                XLog.d("onSignalStrengthsChanged aa... " + strength);
                updateInfoSignalStrength(strength);
            } catch (Exception e) {
                e.printStackTrace();
                XLog.d("onSignalStrengthsChanged Exception:" + e.getMessage());
            }
        }
    };

    private void registerTimeTick() {
        mFilter.addAction("com.huawei.fuck");
        mFilter.addAction(Intent.ACTION_WALLPAPER_CHANGED);
        mFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        mFilter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(mReceiver, mFilter);
    }

    private TestReceiver mReceiver = new TestReceiver();
    private IntentFilter mFilter = new IntentFilter(Intent.ACTION_TIME_TICK);

    private class TestReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            XLog.d("onReceive TestReceiver... " + intent.getAction());
        }
    }


    //
    private void justTest(int which) {
        //此数据是byte[]数据 encode64后的字符串
        String body = "UVgBEPjvz0Ngc89DyHMDMAAAEwoJEQgAhgEGAAM2VW9RWFQtUDEtVjEuMy4xAAAAAAAAAFZY\n" +
                "WAAAAAAA\n";
        //此数据是byte[]数据 encode64后的字符串
        String body2 = "UVgBYMyFz0MAAAAAAAAzMAAAEwoJCTAAhgEGAAM2VW8jQPNC6Lf5QVFYVC1QMS1WMS4zLjEA\n" +
                "AAAAAAAAVlhYAAAAAAA=\n";
        String body3 = "UVgBIFAxLVYxLjMuMQAAADADEwsVDwgPhgEGAAM2VW9RWFQtUDEtVjEuMy4xAAAAAAAAAFZY\n" +
                "WAAAAAAA\n";
        //对应body
        byte[] bufs = {0x51, 0x58, 0x01, 0x10, (byte) 0xf8, (byte) 0xef, (byte) 0xcf, 0x43, 0x60, 0x73, (byte) 0xcf, 0x43, (byte) 0xc8, 0x73, 0x03, 0x30, 0x00, 0x00, 0x13, 0x0a, 0x09, 0x11, 0x08, 0x00,
                (byte) 0x86, 0x01, 0x06, 0x00, 0x03, 0x36, 0x55, 0x6f, 0x51, 0x58, 0x54, 0x2d, 0x50, 0x31, 0x2d, 0x56, 0x31, 0x2e, 0x33, 0x2e, 0x31, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x56, 0x58, 0x58, 0x00, 0x00, 0x00, 0x00, 0x00};
        //对应body2
        byte[] bufs2 = {0x51, 0x58, 0x01, 0x60, (byte) 0xCC, (byte) 0x85, (byte) 0xCF, 0x43, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x33, 0x30, 0x00, 0x00, 0x13, 0x0A, 0x09, 0x09, 0x30,
                0x00, (byte) 0x86, 0x01, 0x06, 0x00, 0x03, 0x36, 0x55, 0x6F, 0x23, 0x40, (byte) 0xF3, 0x42, (byte) 0xE8, (byte) 0xB7, (byte) 0xF9, 0x41, 0x51, 0x58, 0x54, 0x2D, 0x50, 0x31,
                0x2D, 0x56, 0x31, 0x2E, 0x33, 0x2E, 0x31, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x56, 0x58, 0x58, 0x00, 0x00, 0x00, 0x00, 0x00};

        Base64Tool base64Tool = new Base64Tool();
        String encodeStr = base64Tool.nativeEncode(bufs, bufs.length);
        XLog.dMms("encodeStr =" + encodeStr + ", len = " + encodeStr.length());
        byte[] decode = null;
        if (which == 0) {
            decode = base64Tool.nativeDecode(body2, body2.length());
        } else {
            decode = base64Tool.nativeDecode(body3, body3.length());
        }
        XLog.dMms("decode ww body3 =" + ConvertUtil.bytesToHexString(decode) + ", len = " + decode.length);
        final byte[] finalDecode = decode;
        new Thread() {
            @Override
            public void run() {
                OkHttpHelper.getInstance().reportByPost(finalDecode);
            }
        }.start();
    }

    private void testDecode(byte[] input) {
        try {
            MessageReport messageReport = (MessageReport) new MessageReport().decode(input);
            List<ReportData> list = messageReport.getReportDataList();
            XLog.dMms("testDecode raw 22 ..." + list);
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
            Log.e("MmsDebug", "ee:" + e);
        }
    }

    private final String SMS_TEST = "com.wlj.test.sms";
    private SmsParsTest smsParsTest = new SmsParsTest();
    private IntentFilter mParsFilter = new IntentFilter(SMS_TEST);

    private void registerSmsParse() {
        registerReceiver(smsParsTest, mParsFilter);
    }

    private void unRegisterSmsParse() {
        unregisterReceiver(smsParsTest);
    }

    private class SmsParsTest extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            adb shell am broadcast -a com.wlj.test.sms --ei body 2
            int bodyIndex = intent.getIntExtra("body", 1);
            XLog.dMms("onReceive ..." + bodyIndex + "," + intent);
            if (SMS_TEST.equals(intent.getAction())) {
                final String body1 = "51580100000000000000000000000000317B130B050A0C13866976046678045F000000000BB5E34283AFB44162A0FF416E64726F69643A3238000000000000000000006B6972696E393830";
                String body2 = "UVgREFAxLVYxLjMuMQAAAD3HEQUCAy4ohgEGAAM2VW8HEjWHUIFofwEBAAAAAAIAAAgBBREA\n" +
                        "IAIIAQURAQXWhiguAwIFEQUSOTSFMegDKC4DAgURBRI5NIUy7oIoLgMCBREFEjk0hTPQBygu\n" +
                        "AwIFEQUSOTSFNHKG\n";
                String body3 = "UVgBIAAAAAAAAAAAAAAAAD3HEQUBCAMwhgEGAAM2VW8HEjWHUIFofwEBAAAAAAIAAAgBBREA\n" +
                        "IAIIAQURAQHWhjADCAEFEQUSOTSFNFFYVC1QMS1WMS4zLjEAAAAAAAAAVlhYAAAAAAA=\n";
                switch (bodyIndex) {
                    case 1:
                        P1Helper.send2Server(body1, "17400310065");
//                        new Thread() {
//                            @Override
//                            public void run() {
//                                OkHttpHelper.getInstance().reportByPost(ConvertUtil.hexStringToByte(body1));
//                            }
//                        }.start();
                        break;
                    case 2:
                        P1Helper.send2Server(body2, "17400310065");
                        break;
                    case 3:
                        P1Helper.send2Server(body3, "17400310065");
                        break;
                    default:
                        break;
                }

            }
        }
    }

}
