package com.mindyu.step.util;

import android.content.Context;
import android.view.WindowManager;

public class CommonUtil {

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    public static String formatFloat(double value){
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
        return df.format(value);
    }

}
