package com.tubesoft.moodrecorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by inotazo on 2016/05/06.
 */
public class DrawView extends View {
    private Paint paint;

    ArrayList<Path> pathList = new ArrayList<Path>();

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Path path : pathList) {
            canvas.drawPath(path, paint);
        }
    }
}
