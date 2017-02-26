package com.jeffjohnson.boojapp;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by jeffreyjohnson on 2/25/17.
 */

public class ViewUtils {

    public static float getPxFromDp(float dp, Context context){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

}
