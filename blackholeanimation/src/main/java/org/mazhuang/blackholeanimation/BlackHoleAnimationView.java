package org.mazhuang.blackholeanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mazhuang on 2017/1/24.
 */

public class BlackHoleAnimationView extends FrameLayout {
    private Context mContext;

    private BlackHoleView mBlackHoleView;
    private FrameLayout mIconsFrame;

    private Animation mRotateAnimation;
    private ScaleAnimation mScaleAnimation;
    private long mIconScaleTime = 200L;
    private int mIconLeftPadding;

    private List<ImageView> mIconList;
    private AnimatorSet mIconsAnimatorSet;

    private long mCleanTime = 1000L;

    private boolean mIsActing = false;

    public BlackHoleAnimationView(Context context) {
        this(context, null);
    }

    public BlackHoleAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        View root = LayoutInflater.from(context).inflate(R.layout.blackhole_animation_view, this);
        mBlackHoleView = (BlackHoleView) root.findViewById(R.id.black_hole);
        mIconsFrame = (FrameLayout) root.findViewById(R.id.icons_frame);

        mIconLeftPadding = ViewUtils.dp2px(context, 16.0f);

        initAnimation(context);
    }

    public void setIcons(int[] iconResIds) {

        if (mIconList == null) {
            mIconList = new ArrayList<>();
        }

        mIconsFrame.removeAllViews();

        int iconSize = ViewUtils.dp2px(mContext, 32.f);

        for (int i = 0; i < iconResIds.length; i++) {
            PointF pointF = getIconPoint(i);
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(iconSize, iconSize));
            imageView.setX(pointF.x);
            imageView.setY(pointF.y);
            imageView.setBackgroundResource(iconResIds[i]);
            mIconsFrame.addView(imageView);
            mIconList.add(imageView);
        }
    }

    public void startIconsAnimation() {
        if (mIconList == null || mIconList.size() == 0) {
            return;
        }

        mIconsAnimatorSet = new AnimatorSet();

        ArrayList<Animator> animators = new ArrayList<>();

        Iterator iter = mIconList.iterator();
        int i = 1;
        while (iter.hasNext()) {
            ImageView view = (ImageView) iter.next();
            ValueAnimator animator = getIconAnimator(view, i);
            animator.setStartDelay(50L * i);
            animators.add(animator);
            i += 1;
        }

        mIconsAnimatorSet.playTogether(animators);
        mIconsAnimatorSet.setDuration(mCleanTime);
        mIconsAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                resetIcons();
                startIconsAnimation();
            }
        });

        mIconsAnimatorSet.start();
    }

    public void resetIcons() {
        for (int i = 0; i < mIconList.size(); i++) {
            ImageView imageView = mIconList.get(i);
            PointF pointF = getIconPoint(i);
            imageView.setX(pointF.x);
            imageView.setY(pointF.y);
        }
    }

    private ValueAnimator getIconAnimator(final View view, int pos) {
        int i = mIconList.size() + 1;
        float rate = 1.f * pos / i;
        float x = view.getX();
        float y = view.getY();
        float halfW = view.getLayoutParams().width / 2;
        float halfH = view.getLayoutParams().height / 2;
        BezierEvaluator bezierEvaluator = new BezierEvaluator(rate);
        Object[] values = new Object[2];
        values[0] = new PointF(x, y);
        values[1] = new PointF(getWidth() / 2 - halfW, getHeight() / 2 - halfH);
        ValueAnimator animator = ValueAnimator.ofObject(bezierEvaluator, values);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PointF pointF = (PointF) valueAnimator.getAnimatedValue();
                float fraction = valueAnimator.getAnimatedFraction();
                view.setX(pointF.x);
                view.setY(pointF.y);
                view.setScaleX(1.0f - fraction);
                view.setScaleY(1.0f - fraction);
                view.setRotation(fraction * 360.f);
            }
        });

        return animator;
    }

    public void startAnimation(final long cleanTime) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsActing = true;
                mCleanTime = cleanTime;

                setBackgroundColor(ViewUtils.getColorFromRes(mContext, R.color.blackhole_bg));

                if (mIconList != null && mIconList.size() > 0) {
                    startIconsAnimation();
                }

                mBlackHoleView.setVisibility(View.VISIBLE);
                if (mRotateAnimation != null) {
                    mBlackHoleView.startAnimation(mRotateAnimation);
                }
            }
        }, 200L);
    }

    private void initAnimation(Context context) {
        mRotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_around_center);
        mRotateAnimation.setInterpolator(new LinearInterpolator());

        mScaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 1, 0.5f, 1, 0.5f);
        mScaleAnimation.setDuration(mIconScaleTime);
    }

    private PointF getIconPoint(int pos) {
        PointF pointF = new PointF();
        pointF.x = mIconLeftPadding;
        pointF.y = ViewUtils.dp2px(mContext, 150.f + 48.f * pos);
        return pointF;
    }

    static class BezierEvaluator implements TypeEvaluator<PointF> {
        private PointF mPointF = new PointF();
        private float mStartRate = 1.f;

        public BezierEvaluator(float startRate) {
            mStartRate = startRate;
        }

        @Override
        public PointF evaluate(float fraction, PointF startPoint, PointF endPoint) {
            double disX = Math.abs(endPoint.x - startPoint.x);
            double disY = Math.abs(endPoint.y - startPoint.y);
            double remainDis = Math.sqrt(disX * disX + disY * disY) * (1.f - fraction);
            float radians = 2.f * (1.f - mStartRate);
            mPointF.x = ((float)(endPoint.x - remainDis * Math.cos(fraction * (Math.PI * radians))));
            mPointF.y = ((float)(endPoint.y - remainDis * Math.sin(fraction * (Math.PI * radians))));
            return mPointF;
        }
    }
}
