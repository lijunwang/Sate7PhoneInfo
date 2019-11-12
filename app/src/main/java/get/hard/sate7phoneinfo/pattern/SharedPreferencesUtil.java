package get.hard.sate7phoneinfo.pattern;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import get.hard.sate7phoneinfo.PhoneInfoApp;

/**
 * Created by hsg on 14/10/2017.
 */

public class SharedPreferencesUtil {
    private static SharedPreferencesUtil instance;
    public static  final String MMSCenterKey = "settings_mms_center";
    private SharedPreferences.Editor editor;
    private SharedPreferences prefer;

    public SharedPreferencesUtil() {
        this.prefer = PreferenceManager.getDefaultSharedPreferences(PhoneInfoApp.getContext());
        this.editor = this.prefer.edit();
    }

    public static SharedPreferencesUtil getInstance() {
        if (instance == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (instance == null) {
                    instance = new SharedPreferencesUtil();
                }
            }
        }

        return instance;
    }

    public void saveString(String name, String data) {
        this.editor.putString(name, data);
        this.editor.commit();
    }

    public String getString(String name) {
        return this.prefer.getString(name, null);
    }
}
