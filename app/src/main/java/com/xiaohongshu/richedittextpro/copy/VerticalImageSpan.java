package com.xiaohongshu.richedittextpro.copy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by wupengjian on 17/1/16.
 */
public class VerticalImageSpan extends ImageSpan {

    public VerticalImageSpan(Drawable d, String source, int verticalAlignment) {
        super(d, source, verticalAlignment);
    }

    public VerticalImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        // image to draw
        Drawable drawable = getDrawable();
//        drawable.setBounds(0, 0, 33, 33);
        // font metrics of text to be replaced
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.getBounds().bottom / 2;

        canvas.save();
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }
}
