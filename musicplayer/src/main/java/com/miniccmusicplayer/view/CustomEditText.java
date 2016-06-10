package com.miniccmusicplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Hersch on 2016/6/10.
 */
public class CustomEditText extends EditText {
    private Paint paint;
    private final int OFFSET = 5;
    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    public CustomEditText(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(this.getLeft(), this.getBottom() - 5, this.getRight(), this.getBottom() - 5, paint);
        canvas.save();
    }
}
