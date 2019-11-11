package get.hard.sate7phoneinfo.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;

import get.hard.sate7phoneinfo.PhoneInfoApp;
import get.hard.sate7phoneinfo.R;
import get.hard.sate7phoneinfo.SettingsActivity;

public class NotificationHelper {
    private static MediaPlayer mediaPlayer;

    public static void notifyReportSuccess() {
        Context context = PhoneInfoApp.getContext();
        if (SettingsActivity.isVibrateOpen(context)) {
            Vibrator vibrator = (Vibrator) PhoneInfoApp.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{500, 500, 500, 500, 500}, -1);
        }
        if (!SettingsActivity.isSoundOpen(context)) {
            return;
        }
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(PhoneInfoApp.getContext(), R.raw.success);
        }
        mediaPlayer.start();
    }
}
