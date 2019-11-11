package get.hard.sate7phoneinfo.client;

import android.content.Context;

import get.hard.sate7phoneinfo.ReportService;

public class AdjustReporter extends AndroidV1Reporter{
    public AdjustReporter(Context context) {
        super(context);
    }

    @Override
    public void buildReportData() {

    }

    @Override
    public String buildMmsData() {
        return null;
    }


    @Override
    public int getMmsType() {
        return ReportService.REPORT_TYPE_ADJUST ;
    }
}
