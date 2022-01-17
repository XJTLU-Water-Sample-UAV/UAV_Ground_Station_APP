package com.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 本类用于存取应用参数
 */
public class AccessParameter {
    private final Context context;
    private final String fileName;

    /**
     * 构造函数
     *
     * @param context  context对象
     * @param fileName 需要储存的文件名称
     */
    public AccessParameter(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    /**
     * 本方法用于储存参数
     *
     * @param name  参数名称
     * @param value 参数值
     */
    public void storageParameters(String name, int value) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    /**
     * 本方法用于取出参数
     *
     * @param name 参数名称
     * @return 参数值
     */
    public int readParameters(String name) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return preferences.getInt(name, 0xFFFFFF);
    }
}
