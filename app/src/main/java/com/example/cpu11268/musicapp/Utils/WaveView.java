package com.example.cpu11268.musicapp.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class WaveView extends View {
    private boolean isFirst = true;
    private int height = 0, height1 = 0, height2 = 0, height3 = 0;
    private boolean run = false, run1 = false, run2 = false, run3 = false;
    private Random rand = new Random();
    private boolean isPlay = true;

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

        if (isFirst) {
            height = maxheight / 2;
            height1 = maxheight / 3;
            height2 = maxheight;
            height3 = maxheight / 2;
            isFirst = false;
        } else {
            if (height < 0) {
                run = true;
            } else if (height > maxheight) {
                run = false;
            }
            if (!run) {
                height --;
            } else {
                height ++;
            }
            //
            if (height1 < 0) {
                run1 = true;
            } else if (height1 > maxheight) {
                run1 = false;
            }
            if (!run1) {
                height1 --;
            } else {
                height1 ++;
            }
            //
            if (height2 < 0) {
                run2 = true;
            } else if (height2 > maxheight) {
                run2 = false;
            }
            if (!run2) {
                height2 --;
            } else {
                height2 ++;
            }
            //
            if (height3 < 0) {
                run3 = true;
            } else if (height3 > maxheight) {
                run3 = false;
            }
            if (!run3) {
                height3 --;
            } else {
                height3 ++;
            }
        }
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(10);
        paint.setColor(Color.MAGENTA);
        int width = getWidth() / 11;
        RectF rect = new RectF();
        rect.left = 0;
        rect.right = width * 2;
        rect.top = height;
        rect.bottom = maxheight;

        RectF rect1 = new RectF();
        rect1.left = width * 3;
        rect1.right = width * 5;
        rect1.top = height1;
        rect1.bottom = maxheight;

        RectF rect2 = new RectF();
        rect2.left = width * 6;
        rect2.right = width * 8;
        rect2.top = height2;
        rect2.bottom = maxheight;

        RectF rect3 = new RectF();
        rect3.left = width * 9;
        rect3.right = width * 11;
        rect3.top = height3;
        rect3.bottom = maxheight;

        canvas.drawRoundRect(rect, 10, 10, paint);
        canvas.drawRoundRect(rect1, 10, 10, paint);
        canvas.drawRoundRect(rect2, 10, 10, paint);
        canvas.drawRoundRect(rect3, 10, 10, paint);
        if (isPlay) {
            invalidate();
        }
    }
}
