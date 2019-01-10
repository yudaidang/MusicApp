package com.example.cpu11268.musicapp.Views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class ItemWaveView {
    private int left, right, top, bottom, maxheight;
    private Paint paint;
    private boolean isUp = true;

    public ItemWaveView(int left, int right, int top, int bottom, Paint paint, int maxHeight, Canvas canvas) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.paint = paint;
        this.maxheight = maxHeight;
        UpdateUi(canvas);
    }

    public void UpdateUi(Canvas canvas){
        if (top < 0) {
            isUp = true;
        } else if (top > maxheight) {
            isUp = false;
        }
        if (!isUp) {
            top--;
        } else {
            top++;
        }
        RectF rect = new RectF();
        rect.left = left;
        rect.right = right;
        rect.top = top;
        rect.bottom = bottom;
        canvas.drawRoundRect(rect, 10, 10, paint);
    }

}
