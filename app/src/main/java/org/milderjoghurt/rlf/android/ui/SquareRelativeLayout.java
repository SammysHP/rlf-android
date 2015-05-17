package org.milderjoghurt.rlf.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * A square (relative) layout.
 *
 * @see <a href="http://stackoverflow.com/questions/2948212/android-layout-with-sqare-buttons">Stack Overflow - Android layout with sqare buttons</a>
 */
public class SquareRelativeLayout extends RelativeLayout {

    public SquareRelativeLayout(Context context) {
        super(context);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (width > (int) (height + 0.5)) {
            width = (int) (height + 0.5);
        } else {
            height = (int) (width + 0.5);
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        );
    }

}
