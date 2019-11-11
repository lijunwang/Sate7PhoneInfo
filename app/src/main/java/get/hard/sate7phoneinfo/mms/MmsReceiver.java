package get.hard.sate7phoneinfo.mms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import get.hard.sate7phoneinfo.XLog;
import get.hard.sate7phoneinfo.util.M2MHelper;
import get.hard.sate7phoneinfo.util.P1Helper;
import get.hard.sate7phoneinfo.util.V1MmsHelper;

public class MmsReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private final boolean M2M_FORWARD = false;
    private static final String TAG = "MmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        XLog.dMms("onReceive SMS_RECEIVED_ACTION 22 ... " + intent.getAction());
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            SmsMessage message = messages[0];
            final String address = message.getOriginatingAddress();
            final String body = message.getMessageBody();
            StringBuffer sb = new StringBuffer();
            for (SmsMessage smsMessage : messages) {
                sb.append(smsMessage.getMessageBody());
            }
            XLog.d("V1MmsHelper", "onReceive v1 ... " + body + "," + sb.toString());
//            V1MmsHelper.parseAndReportToServer(context, body);
            V1MmsHelper.parseAndReportToServer(context, sb.toString());
        }
        if (intent.getAction().equals(SMS_RECEIVED_ACTION) && M2M_FORWARD) {
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            SmsMessage message = messages[0];
            final String address = message.getOriginatingAddress();
            final String body = message.getMessageBody();
            XLog.dMms("body ww22 == " + body + ",address = " + address);
            String test = "Time:2019-10-11-17-41,IMEI:860106000336556,LNG:113.853443E,LAT:22.585754N";
            M2MHelper.send2Server(body, address);
            return;
        }
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            SmsMessage message = messages[0];
            final String address = message.getOriginatingAddress();
            final String body = message.getMessageBody();
            P1Helper.send2Server(body, address);
        }
    }
}
