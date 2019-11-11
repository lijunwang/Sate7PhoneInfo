package get.hard.sate7phoneinfo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BootShutRecordHelper extends SQLiteOpenHelper {
    private static final String TAG = "BootShutRecordHelper";
    private static final int VERSION = 1;
    public static final int TYPE_ON = 0;
    public static final int TYPE_OFF = 1;
    private static final String DB_NAME = "boot_shut_record";
    private static final String TABLE_NAME = "boot_record";
    public static final String CONTENT_TYPE = "type";
    public static final String CONTENT_DATE_TEXT = "date_text";
    public static final String CONTENT_DATE_TS = "date_int";
    private final String CREATE_TABLE_REBOOT = "create table if not exists " + TABLE_NAME + "(" +
            BaseColumns._ID + " integer primary key autoincrement," + CONTENT_TYPE + " integer," + CONTENT_DATE_TEXT + " text," +
            CONTENT_DATE_TS + " TimeStamp" + ")";
    //    String ss = "create table if not exists " + TABLE_NAME + "('_id' integer primary key autoincrement,'name' text,'facedata' blob,'codenumber' integer,'createtime' + TimeStamp DEFAULT(datetime('now', 'localtime')))";
    private final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public BootShutRecordHelper(Context context) {
        this(context, DB_NAME, null, VERSION);
    }

    public BootShutRecordHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        this(context, DB_NAME, null, VERSION, new DatabaseErrorHandler() {
            @Override
            public void onCorruption(SQLiteDatabase dbObj) {
                Log.d(TAG, "BootShutRecordHelper onCorruption .. " + dbObj);
            }
        });

    }

    public BootShutRecordHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate ... ");
        db.execSQL(CREATE_TABLE_REBOOT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade ... ");
        if (newVersion > oldVersion) {
            db.execSQL(DELETE_TABLE);
        }
    }

    public long insert(ContentValues values) {
        long id = getWritableDatabase().insert(TABLE_NAME, null, values);
        Log.d(TAG, "insert ... " + id + " --- " + values);
        return id;
    }

    public Cursor queryAll() {
//        asc
        return getWritableDatabase().query(TABLE_NAME, new String[]{CONTENT_TYPE, CONTENT_DATE_TEXT, CONTENT_DATE_TS}, null, null, null, null, CONTENT_DATE_TS +  " DESC");
    }

    public Cursor queryPowerOn() {
        return getWritableDatabase().query(TABLE_NAME, new String[]{CONTENT_TYPE, CONTENT_DATE_TEXT, CONTENT_DATE_TS}, CONTENT_TYPE + " = ?", new String[]{"" + TYPE_ON}, null, null, CONTENT_DATE_TS +  " DESC");
    }

    public Cursor queryPowerOff() {
        return getWritableDatabase().query(TABLE_NAME, new String[]{CONTENT_TYPE, CONTENT_DATE_TEXT, CONTENT_DATE_TS}, CONTENT_TYPE + " = ?", new String[]{"" + TYPE_OFF}, null, null, CONTENT_DATE_TS +  " DESC");
    }


    public long insertPowerOn(){
        ContentValues values = new ContentValues();
        values.put(BootShutRecordHelper.CONTENT_TYPE,BootShutRecordHelper.TYPE_ON);
        values.put(BootShutRecordHelper.CONTENT_DATE_TEXT,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString());
        values.put(BootShutRecordHelper.CONTENT_DATE_TS,System.currentTimeMillis());
        return insert(values);
    }

    public long insertPowerOff(){
        ContentValues values = new ContentValues();
        values.put(BootShutRecordHelper.CONTENT_TYPE,BootShutRecordHelper.TYPE_OFF);
        values.put(BootShutRecordHelper.CONTENT_DATE_TEXT,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString());
        values.put(BootShutRecordHelper.CONTENT_DATE_TS,System.currentTimeMillis());
        return insert(values);
    }

    public void clean(){
        getWritableDatabase().delete(TABLE_NAME,null,null);
    }
}
