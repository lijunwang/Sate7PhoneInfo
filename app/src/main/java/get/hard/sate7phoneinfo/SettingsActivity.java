package get.hard.sate7phoneinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

import java.util.Map;

import get.hard.sate7phoneinfo.pattern.PatternHelper;
import get.hard.sate7phoneinfo.util.AlarmHelper;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    private static final String KEY_MMS_CENTER = "settings_mms_center";
    private static final String KEY_SERVER_PORT = "settings_server_port";
    private static final String KEY_SERVER_IP = "settings_server_ip";
    private static final String KEY_SERVER_PWD = "setting_pwd";
//    private static final String KEY_SERVER_FREQUENCY_HOUR = "setting_frequency_hour";
//    private static final String KEY_SERVER_FREQUENCY_MINUTE = "setting_frequency_minute";
    private static final String KEY_SERVER_FREQUENCY = "setting_frequency";
    private static final String KEY_UPLOAD_SOUND = "upload_sound";
    private static final String KEY_UPLOAD_VIBRATE = "upload_vibrate";
    private PatternHelper mPatternHelper = new PatternHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_prf_screen);
        XLog.d("actionBar... " + getActionBar());
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        getPreferenceManager().findPreference(KEY_MMS_CENTER).setOnPreferenceChangeListener(this);
        getPreferenceManager().findPreference(KEY_SERVER_PORT).setOnPreferenceChangeListener(this);
        getPreferenceManager().findPreference(KEY_SERVER_IP).setOnPreferenceChangeListener(this);
        getPreferenceManager().findPreference(KEY_SERVER_PWD).setOnPreferenceChangeListener(this);
        getPreferenceManager().findPreference(KEY_SERVER_FREQUENCY).setOnPreferenceChangeListener(this);
//        getPreferenceManager().findPreference(KEY_SERVER_FREQUENCY_HOUR).setOnPreferenceChangeListener(this);
//        getPreferenceManager().findPreference(KEY_SERVER_FREQUENCY_MINUTE).setOnPreferenceChangeListener(this);
//        startActivityForResult(new Intent(this, DecryptActivity.class), 123);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(getPackageName() + "_preferences", Context.MODE_PRIVATE);
        Map info = preferences.getAll();
        String[] value = getResources().getStringArray(R.array.frequency_values);
        String[] entry = getResources().getStringArray(R.array.frequency_entries);
        for (int i = 0; i < value.length; i++) {
            if (info.get(KEY_SERVER_FREQUENCY).equals(value[i])) {
                XLog.d("SettingsActivity find ... ");
                getPreferenceManager().findPreference(KEY_SERVER_FREQUENCY).setSummary(entry[i]);
                break;
            }
        }
    }

    public static boolean isVibrateOpen(Context context){
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_UPLOAD_VIBRATE, true);
    }

    public static boolean isSoundOpen(Context context){
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_UPLOAD_SOUND, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        XLog.d("SettingsActivity onActivityResult " + requestCode + "," + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            finish();
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        XLog.d("SettingsActivity onPreferenceClick ... " + preference.getTitle());
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        XLog.d("SettingsActivity onSharedPreferenceChanged ... " + key);
        if(key.equals(KEY_SERVER_FREQUENCY)){
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        XLog.d("SettingsActivity onPreferenceChange ... " + preference.getKey() + "," + newValue);
        if (preference instanceof ListPreference) {
//            boolean isHour = KEY_SERVER_FREQUENCY_HOUR.equals(preference.getKey());
//            String[] value = getResources().getStringArray(isHour ? R.array.hours_value : R.array.minutes_value);
//            String[] entry = getResources().getStringArray(isHour ? R.array.hours : R.array.minutes);
            String[] value = getResources().getStringArray(R.array.frequency_values);
            String[] entry = getResources().getStringArray(R.array.frequency_entries);
            for (int i = 0; i < value.length; i++) {
                if (newValue.equals(value[i])) {
                    XLog.d("SettingsActivity find ... " + i + "," + newValue);
                    preference.setSummary(entry[i]);
                    break;
                }
            }
        }else{
            preference.setSummary("" + newValue);
        }
        return true;
    }
}
