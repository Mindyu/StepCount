package com.mindyu.step.util;

import android.content.Context;
import android.os.Build;
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

    /*DecimalFormat 类主要靠#和0两种占位符号来指定数字长度
    * 0表示如果位数不足则以0填充
    * #表示只要有可能就把数字拉上这个位置*/
    public static String formatFloat(double value){
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
        return df.format(value);
    }

    /*获取手机型号*/
    public static String getMobileType() {
        return Build.MANUFACTURER;
    }

}
