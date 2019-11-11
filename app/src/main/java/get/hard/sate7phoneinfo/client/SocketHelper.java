package get.hard.sate7phoneinfo.client;

import android.util.Log;

import com.blankj.utilcode.util.NetworkUtils;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import get.hard.sate7phoneinfo.XLog;

public class SocketHelper extends SocketActionAdapter {
    private final String IP = "104.238.184.237";
    private final int PORT = 8080;
    private static IConnectionManager connectionManager;
    private static SocketHelper mInstance = null;

    private SocketHelper() {
        ConnectionInfo connectionInfo = new ConnectionInfo(IP, PORT);
        connectionManager = OkSocket.open(connectionInfo);
        connectionManager.registerReceiver(this);
        OkSocketOptions options = connectionManager.getOption();
        OkSocketOptions.Builder builder = new OkSocketOptions.Builder(options);
        builder.setConnectionHolden(true);
        connectionManager.option(builder.build());
        connectionManager.connect();
    }

    public static SocketHelper getInstance() {
        if (mInstance == null) {
            synchronized (SocketHelper.class) {
                if (mInstance == null) {
                    mInstance = new SocketHelper();
                }
            }
        }
        return mInstance;
    }

    public void sendData(final byte[] data) {
        XLog.dReport("sendData ... " + data);
        connectionManager.send(new ISendable() {
            @Override
            public byte[] parse() {
                return data;
            }
        });
    }

    @Override
    public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
        super.onSocketConnectionSuccess(info, action);
        connectionManager.send(new HandShakeBean());
        XLog.dReport("onSocketConnectionSuccess ... " + action);
    }

    @Override
    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
        super.onSocketConnectionFailed(info, action, e);
        XLog.dReport("onSocketConnectionFailed ... " + action);
    }

    @Override
    public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
        super.onSocketDisconnection(info, action, e);
        XLog.dReport("onSocketDisconnection ... " + action);
    }

    @Override
    public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
        super.onSocketReadResponse(info, action, data);
        String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
        XLog.dReport("onSocketReadResponse ... " + action + "," + str);
    }

    @Override
    public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
        super.onSocketWriteResponse(info, action, data);
        XLog.dReport("onSocketWriteResponse ... " + action + "," + new String(data.parse()));
    }

    private class MsgString implements ISendable {
        protected String content;

        public MsgString(String msg) {
            content = msg;
        }

        public MsgString() {
        }

        @Override
        public byte[] parse() {
            byte[] body = content.getBytes(Charset.defaultCharset());
            ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
            bb.order(ByteOrder.BIG_ENDIAN);
            bb.putInt(body.length);
            bb.put(body);
            return bb.array();
        }
    }

    private class HandShakeBean extends MsgString {

        public HandShakeBean() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmd", 54);
                jsonObject.put("handshake", "Hello the OkSocket");
                content = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
