package get.hard.sate7phoneinfo.util;

public class Base64Tool {

    public native String nativeEncode(byte[] data, int dataLen);
    public native byte[] nativeDecode(String buf, int bufLen);

    static {
        //System的S是大写
        System.loadLibrary("base64tool");
    }
}
