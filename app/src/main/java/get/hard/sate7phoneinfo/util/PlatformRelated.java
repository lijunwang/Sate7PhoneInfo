package get.hard.sate7phoneinfo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Random;

import get.hard.sate7phoneinfo.XLog;

public class PlatformRelated {

    private static final String sate7 = "7Sate";

    public static boolean isV1() {
        try {
            Class property = Class.forName("android.os.SystemProperties");
            Method method = property.getDeclaredMethod("get", String.class);
            String brand = (String) method.invoke(null, "ro.product.brand");
            XLog.d("PlatformRelated brand = " + brand);
//            return sate7.equals(brand);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            XLog.d("PlatformRelated Exception:" + e.getMessage());
        }
        return false;
    }

    private static final String FakeImei = "fakeImei";

    public static String getFakeImei(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("fake_imei", Context.MODE_PRIVATE);
        String fakeImei = preferences.getString(FakeImei, "");
        if (TextUtils.isEmpty(fakeImei)) {
            long time = System.currentTimeMillis();
            fakeImei = "" + time + new Random().nextInt(9) + "" + new Random().nextInt(9);
            boolean save = preferences.edit().putString(FakeImei, fakeImei).commit();
            XLog.dReport("PlatformRelated getFakeImei ..." + fakeImei + "," + save);
            return fakeImei;
        }
        XLog.dReport("PlatformRelated getFakeImei ww..." + fakeImei);
        return fakeImei;
    }
}
