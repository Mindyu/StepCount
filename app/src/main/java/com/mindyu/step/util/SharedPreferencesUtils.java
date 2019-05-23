package com.mindyu.step.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences的一个工具类，调用setParam就能保存String, Integer, Boolean, Float,
 * Long类型的参数 同样调用getParam就能获取到保存在手机里面的数据
 */
public class SharedPreferencesUtils {
    private Context context;

    /**
     * 保存在手机里面的文件名
     */
    private static String FILE_NAME = "share_date";

    public SharedPreferencesUtils(Context context) {
        this.context = context;
    }

    /**
     * 存储数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public void setParam(String key, Object object) {
        //获得当前对象的类型，返回源代码中给出的底层类的简称
        String type = object.getClass().getSimpleName();

        /*指定SharePreferences文件的名称和操作模式
         * 调用SharePreferences对象的edit()方法获取SharePreferences.Editor对象
         * 向SharePreferences.Editor对象中添加数据
         * 调用apply()方法将添加的数据提交，从而完成数据存储
         * */
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sp.edit();
        if ("String".equals(type)) {
            editor.putString(key, object.toString());
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }
        editor.apply();
    }

    /**
     * 得到存储数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对应的方法读取数据
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object getParam(String key, Object defaultObject) {
        //获得当前对象的类型，返回源代码中给出的底层类的简称
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * @param key
     * @return
     */

    // Delete
    public void remove(String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clear() {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}
