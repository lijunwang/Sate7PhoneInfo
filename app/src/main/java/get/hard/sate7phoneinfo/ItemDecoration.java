package get.hard.sate7phoneinfo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class ItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private final int PADDING_TOP_BOTTOM = 30;
    private final int DIVIDE_HEIGHT = 6;

    public ItemDecoration() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GRAY);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, PADDING_TOP_BOTTOM, 0, PADDING_TOP_BOTTOM);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            c.drawLine(0,
                    parent.getChildAt(i).getBottom() - DIVIDE_HEIGHT / 2 + PADDING_TOP_BOTTOM, c.getWidth(),
                    parent.getChildAt(i).getBottom() + DIVIDE_HEIGHT / 2 + PADDING_TOP_BOTTOM, mPaint);
        }
    }
}
