package org.mazhuang.blackholeanimation;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by mazhuang on 2017/1/24.
 */

public class ViewUtils {
    public static int dp2px(Context context,float dp) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int getColorFromRes(Context context, int resId) {
        return context.getResources().getColor(resId);
    }
}
