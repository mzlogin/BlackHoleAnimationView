package org.mazhuang.blackholeanimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mazhuang on 2017/1/24.
 */

public class BlackHoleView extends View {

    private Paint mPaint;

    private int mCircleCount;
    private int mCircleGap;
    private int mInmostRadius;

    private int mRotate = 30;

    private int mGradientStartColor;
    private int[] mColorArrays;

    public BlackHoleView(Context context) {
        this(context, null);
    }

    public BlackHoleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ViewUtils.dp2px(context, 3.0f));

        mCircleCount = 7;
        mCircleGap = ViewUtils.dp2px(context, 10.f);
        mInmostRadius = ViewUtils.dp2px(context, 60.f);

        mGradientStartColor = ViewUtils.getColorFromRes(context, R.color.gradient_start);
        mColorArrays = new int[mCircleCount];
        mColorArrays[0] = ViewUtils.getColorFromRes(context, R.color.gradient_end_1);
        mColorArrays[1] = ViewUtils.getColorFromRes(context, R.color.gradient_end_2);
        mColorArrays[2] = ViewUtils.getColorFromRes(context, R.color.gradient_end_3);
        mColorArrays[3] = ViewUtils.getColorFromRes(context, R.color.gradient_end_4);
        mColorArrays[4] = ViewUtils.getColorFromRes(context, R.color.gradient_end_5);
        mColorArrays[5] = ViewUtils.getColorFromRes(context, R.color.gradient_end_6);
        mColorArrays[6] = ViewUtils.getColorFromRes(context, R.color.gradient_end_7);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerH = getWidth() / 2;
        int centerV = getHeight() / 2;

        int radius = mInmostRadius;

        int[] colors = new int[2];
        for (int i = 0; i < this.mCircleCount; i++) {
            colors[0] = mGradientStartColor;
            colors[1] = this.mColorArrays[i];
            SweepGradient sweepGradient = new SweepGradient(centerH, centerV, colors, null);
            mPaint.setShader(sweepGradient);
            canvas.rotate(-mRotate, centerH, centerV);

            canvas.drawCircle(centerH, centerV, radius, mPaint);

            radius += mCircleGap;
        }
    }
}
