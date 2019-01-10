package com.example.cpu11268.musicapp.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class WaveView extends View {
    private boolean isFirst = true;
    private Integer height = 0, height1 = 0, height2 = 0, height3 = 0;
    private boolean run = false, run1 = false, run2 = false, run3 = false;
    private Random rand = new Random();
    private boolean isPlay = true;
    private ItemWaveView item1, item2, item3, item4;

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void pauseOrPlay(boolean is) {
        if (is) {
            invalidate();
            isPlay = is;
        } else {
            isPlay = is;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int maxheight = getHeight();
        int width = getWidth() / 11;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(10);
        paint.setColor(Color.MAGENTA);
        if (isFirst) {

            item1 = new ItemWaveView(0, width * 2, maxheight / 2, maxheight, paint, maxheight);
            item2 = new ItemWaveView(width * 3, width * 5, maxheight / 6, maxheight, paint, maxheight);
            item3 = new ItemWaveView(width * 6, width * 8, maxheight / 3, maxheight, paint, maxheight);
            item4 = new ItemWaveView(width * 9, width * 11, maxheight / 2, maxheight, paint, maxheight);
            isFirst = false;
        } else {
            item1.UpdateUi(canvas);
            item3.UpdateUi(canvas);
            item2.UpdateUi(canvas);
            item4.UpdateUi(canvas);
        }

        if (isPlay) {
            invalidate();
        }
    }
}
