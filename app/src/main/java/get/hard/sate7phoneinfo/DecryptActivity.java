package get.hard.sate7phoneinfo;

import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.ihsg.patternlocker.OnPatternChangeListener;
import com.github.ihsg.patternlocker.PatternIndicatorView;
import com.github.ihsg.patternlocker.PatternLockerView;

import java.util.List;

import get.hard.sate7phoneinfo.pattern.PatternHelper;

public class DecryptActivity extends AppCompatActivity implements OnPatternChangeListener {

    private PatternLockerView mPatternLockerView;
    private PatternIndicatorView mIndicatorView;
    private PatternHelper mHelper = new PatternHelper();
    private TextView mTvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);
        mPatternLockerView = findViewById(R.id.pattern_lock_view);
        mIndicatorView = findViewById(R.id.pattern_indicator_view);
        mTvInfo = findViewById(R.id.info);
        mPatternLockerView.setOnPatternChangedListener(this);

//        mIndicatorView.setVisibility(mHelper.hasSetPwd() ? View.INVISIBLE : View.VISIBLE);
        if(mTvInfo.getVisibility() != View.VISIBLE){
            getSupportActionBar().setTitle(mHelper.hasSetPwd() ? R.string.guest_pwd_1: R.string.guest_pwd);
        }else{
            mTvInfo.setText(mHelper.hasSetPwd() ? R.string.guest_pwd_1: R.string.guest_pwd);
        }
    }

    @Override
    public void onChange(PatternLockerView patternLockerView, List<Integer> list) {
        XLog.d("Test onChange ... " + list);
    }

    @Override
    public void onClear(PatternLockerView patternLockerView) {
        XLog.d("Test onClear ... " + mHelper.isFinish() + "," + mHelper.isOk());
        if (mHelper.isFinish()) {
            if (mHelper.isOk()) {
                setResult(Activity.RESULT_OK);
            }
            finish();
        }
    }

    @Override
    public void onComplete(PatternLockerView patternLockerView, List<Integer> list) {
        XLog.d("Test onComplete ... " + list + "," + mHelper.hasSetPwd());
        if (mHelper.hasSetPwd()) {
            mHelper.validateForChecking(list);
        } else {
            mHelper.validateForSetting(list);
        }
        boolean isOk = mHelper.isOk();
        patternLockerView.updateStatus(!isOk);
        mIndicatorView.updateState(list, !isOk);
        updateMsg();
    }

    private void updateMsg() {
        mTvInfo.setText(mHelper.getMessage());
        mTvInfo.setTextColor(mHelper.isOk() ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.colorAccent));
        if(mTvInfo.getVisibility() != View.VISIBLE){
            getSupportActionBar().setTitle(mHelper.getMessage());
        }
    }

    @Override
    public void onStart(PatternLockerView patternLockerView) {
        XLog.d("Test onStart ... ");
    }
}
