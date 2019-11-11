package get.hard.sate7phoneinfo.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.ArrayList;

import get.hard.sate7phoneinfo.XLog;

public class MmsSender {
    private static final String TAG = "MmsSender";
    public static final String SMS_SEND_SUCCESS = "com.wlj.sms.send.result";
    public static final String SMS_DELIVERED_SUCCESS = "com.wlj.sms.delivered.result";

    public static void sendSms(Context context, String number, String content) {
        /*if(!DualSimHelper.isSimAvailable(context)){
            Toast.makeText(context,context.getResources().getString(R.string.start_fail_tip),Toast.LENGTH_SHORT).show();
            return;
        }*/
        Intent sendIntent = new Intent(SMS_SEND_SUCCESS);
        Intent deliveryIntent = new Intent(SMS_DELIVERED_SUCCESS);
        sendIntent.putExtra("phoneNumber", number);
        PendingIntent sendPI = PendingIntent.getBroadcast(context, 0, sendIntent, 0);
        PendingIntent deliveryPendingIntent = PendingIntent.getBroadcast(context, 0, deliveryIntent, 0);
        SmsManager smsManager = SmsManager.getDefault();

        if (content.length() >= 70) {
            ArrayList<String> msgs = smsManager.divideMessage(content);
            ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
            for (int i = 0; i < msgs.size(); i++) {
                sentIntents.add(sendPI);
                deliveryIntents.add(deliveryPendingIntent);
            }
//            smsManager.sendMultipartTextMessage(number, null, msgs, sentIntents, null);
            smsManager.sendMultipartTextMessage(number, null, msgs, sentIntents, deliveryIntents);
            XLog.d(TAG, "sendMultipartTextMessage ...");
        } else {
            smsManager.sendTextMessage(number, null, content, sendPI, deliveryPendingIntent);
            XLog.d(TAG, "sendTextMessage ...");
        }

    }
}
