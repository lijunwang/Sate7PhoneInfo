package get.hard.sate7phoneinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class DetailActivity extends AppCompatActivity {
    private RecyclerView mDetailInfoRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DetailInfoAdapter mDetailInfoAdapter;
    public static final String DETAIL_TYPE = "detail_type";
    public static final int DETAIL_TYPE_CALL_LOG = 0x10;
    private static final int REQUEST_PERMISSION_CALL_LOG = 123;
    private ArrayList<CallLogInfo> mCallLogs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initViews();
        int type = getIntent().getIntExtra(DETAIL_TYPE, DETAIL_TYPE_CALL_LOG);
        if (type == DETAIL_TYPE_CALL_LOG) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_PERMISSION_CALL_LOG);
            } else {
                getCallLog();
            }
        }
    }

    private void getCallLog() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Uri callLog = CallLog.Calls.CONTENT_URI;
        @SuppressLint("MissingPermission") Cursor cursor = getContentResolver().query(callLog,
                new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DURATION, CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE},
                CallLog.Calls.DURATION + ">" + 0, null, "date DESC");
        mCallLogs.clear();
        if (cursor.moveToFirst()) {
            do {
                String number = cursor.getString(0);
                int type = cursor.getInt(1);
                long duration = cursor.getLong(2);
                String name = cursor.getString(3);
                String date = cursor.getString(4);
                String dateConvert = simpleDateFormat.format(Long.parseLong(date));
                XLog.d("info detail ----" + number + "," + type + "," + duration + "," + name + "," + dateConvert);
                mCallLogs.add(new CallLogInfo(number, name, dateConvert, duration, type));
            } while (cursor.moveToNext());
            cursor.close();
        }
        mDetailInfoAdapter.notifyDataSetChanged();
        XLog.d("DetailActivity mCallLogs :");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        XLog.d("detail onRequestPermissionsResult:" + requestCode + "," + Arrays.toString(permissions) + "," + Arrays.toString(grantResults));
        if (requestCode == REQUEST_PERMISSION_CALL_LOG && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCallLog();
        }
    }

    private void initViews() {
        XLog.d("DetailActivity initViews ... ");
        mDetailInfoRecyclerView = findViewById(R.id.detailInfo);
        mDetailInfoAdapter = new DetailInfoAdapter();
        mLayoutManager = new LinearLayoutManager(this);
        mDetailInfoRecyclerView.setLayoutManager(mLayoutManager);
        mDetailInfoRecyclerView.addItemDecoration(new ItemDecoration());
        mDetailInfoRecyclerView.setAdapter(mDetailInfoAdapter);
    }

    private static class CallLogInfo {
        String number;
        String name;
        String date;
        long duration;
        int type;

        public CallLogInfo(String number, String name, String date, long duration, int type) {
            this.number = number;
            this.name = name;
            this.date = date;
            this.duration = duration;
            this.type = type;
        }

        @Override
        public String toString() {
            return "" + number + "," + name + "," + date + "," + type;
        }
    }

    private class DetailInfoAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new DetailHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.call_log_item, null));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            XLog.d("DetailActivity onBindViewHolder : " + mCallLogs.size());
            DetailHolder holder = (DetailHolder) viewHolder;
            holder.mNub.setText(mCallLogs.get(i).number);
            holder.mName.setText(mCallLogs.get(i).name);
            holder.mDate.setText(mCallLogs.get(i).date);
            holder.mDuration.setText(getResources().getString(R.string.call_duration, mCallLogs.get(i).duration));
        }

        @Override
        public int getItemCount() {
            XLog.d("DetailActivity getItemCount : " + mCallLogs.size());
            return mCallLogs.size();
        }
    }

    private class DetailHolder extends RecyclerView.ViewHolder {
        public TextView mNub;
        TextView mName;
        TextView mDate;
        TextView mDuration;

        public DetailHolder(@NonNull View itemView) {
            super(itemView);
            mNub = itemView.findViewById(R.id.callLogNub);
            mName = itemView.findViewById(R.id.callLogName);
            mDate = itemView.findViewById(R.id.callLogDate);
            mDuration = itemView.findViewById(R.id.callLogDuration);
        }
    }
}
