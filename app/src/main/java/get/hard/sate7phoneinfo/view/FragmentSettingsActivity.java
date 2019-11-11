package get.hard.sate7phoneinfo.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.contrarywind.view.WheelView;

import get.hard.sate7phoneinfo.R;

public class FragmentSettingsActivity extends AppCompatActivity {

    private WheelView mHour;
    private WheelView mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_settings);

        mHour = findViewById(R.id.frequency_hour);
        mMinute = findViewById(R.id.frequency_minute);
    }
}
