package com.gmail.hecarson3.velocimeter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

public class VelocityDisplayView extends View {

    private int frame = 0;
    Paint circlePaint = new Paint();
    Paint textPaint = new Paint();

    CompassInfo compassInfo;

    public VelocityDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        circlePaint.setColor(getResources().getColor(R.color.white, context.getTheme()));

        textPaint.setColor(getResources().getColor(R.color.white, context.getTheme()));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(50.0f);
    }

    public void setCompassInfo(CompassInfo compassInfo) {
        this.compassInfo = compassInfo;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(this.getWidth() / 2.0f, this.getHeight() / 2.0f);

        // haha circle
        float angle = frame++ / 30.0f;
        float radius = 300.0f;
        float x = radius * (float)Math.cos(angle);
        float y = radius * (float)Math.sin(angle);
        canvas.drawRect(x - 30, y - 30, x + 30, y + 30, circlePaint);

        // this allows Android Studio UI designer to render this view
        if (compassInfo == null)
            return;

        float headingRadians = compassInfo.getHeading();
        float headingDegrees = 180.0f / (float)Math.PI * headingRadians;
        canvas.drawText(String.format(Locale.ENGLISH, "%.1f", headingDegrees), 0.0f, 0.0f, textPaint);
    }

}