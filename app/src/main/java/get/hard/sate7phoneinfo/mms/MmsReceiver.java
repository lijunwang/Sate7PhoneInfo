package get.hard.sate7phoneinfo.mms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Base64;

import com.google.gson.Gson;

import get.hard.sate7phoneinfo.R;
import get.hard.sate7phoneinfo.XLog;
import get.hard.sate7phoneinfo.bean.V1ReportBean;
import get.hard.sate7phoneinfo.util.CompressUtils;
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
            XLog.dReport("onReceive v1 ... " + body + "," + sb.toString());
            boolean isV1 = false;
            try {
                Gson gson = new Gson();
                String afterUncompress = CompressUtils.uncompressToString(Base64.decode(sb.toString(), Base64.DEFAULT));
                V1ReportBean v1ReportBean = gson.fromJson(afterUncompress, V1ReportBean.class);
                isV1 = v1ReportBean.getType().equals(context.getResources().getString(R.string.device_type_v1));
                XLog.dReport("isV1 ... " + v1ReportBean.getType());
                V1MmsHelper.parseAndReportToServer(sb.toString());
            } catch (Exception e) {
                isV1 = false;
            }
            if (isV1) {
                return;
            }
        }
        if (intent.getAction().equals(SMS_RECEIVED_ACTION) && M2M_FORWARD) {
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            SmsMessage message = messages[0];
            final String address = message.getOriginatingAddress();
            final String body = message.getMessageBody();
            XLog.dMms("body ww22 == " + body + ",address = " + address);
            String test = "Time:2019-10-11-17-41,IMEI:860106000336556,LNG:113.853443E,LAT:22.585754N";
            M2MHelper.send2Server(body, address);
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
