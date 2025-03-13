package com.richzjc.shortvideo.util;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

/**
 * Created by zhangjianchuan on 2016/6/23.
 */
public class SharedPrefsUtil {

    public static String SHARED_PREFS_FILE_NAME = "shared_preference_config";
    public static final String CHECK_HUAWEI_PRO="CHECK_HUAWEI_PRO";
    public static final String SHARE_PREFS_LOGIN = "login";

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }


    

    //Integers
    public static void saveInt(Context context, String key, int value) {
        getPrefs(context).edit().putInt(key, value).apply();
    }


    public static int getInt(Context context, String key, int defaultValue) {
        return getPrefs(context).getInt(key, defaultValue);
    }

    //Booleans
    public static void save(Context context, String key, boolean value) {
        getPrefs(context).edit().putBoolean(key, value).apply();
    }


    public static boolean getBoolean(Context context, String key) {
        return getPrefs(context).getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getPrefs(context).getBoolean(key, defaultValue);
    }

    public static void saveString(Context context, String key, String value) {
        getPrefs(context).edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key) {
        return getPrefs(context).getString(key, "");
    }

    public static String getString(Context context, String key, String defaultValue) {
        return getPrefs(context).getString(key, defaultValue);
    }


    public static int getInt(Context context, String key) {
        return getPrefs(context).getInt(key, 0);
    }


    //Floats
    public static void saveFloat(Context context, String key, float value) {
        getPrefs(context).edit().putFloat(key, value).apply();
    }

    public static float getFloat(Context context, String key) {
        return getPrefs(context).getFloat(key, 0);
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        return getPrefs(context).getFloat(key, defaultValue);
    }

    //Longs
    public static void saveLong(Context context, String key, long value) {
        getPrefs(context).edit().putLong(key, value).apply();
    }

    public static void saveLong(Context context, String prefName, String key, long value) {
        getPrefs(context).edit().putLong(key, value).apply();
    }

    public static void saveLongRightNow(Context context, String key, long value) {
        getPrefs(context).edit().putLong(key, value).commit();
    }
    

    public static long getLong(Context context, String key) {
        return getPrefs(context).getLong(key, 0);
    }

    public static long getLong(Context context, String key, long defaultValue) {
        return getPrefs(context).getLong(key, defaultValue);
    }

    //StringSets
    public static void saveStringSet(Context context, String key, Set<String> value) {
        getPrefs(context).edit().putStringSet(key, value).apply();
    }

    public static Set<String> getStringSet(Context context, String key, Set<String> defaultValue) {
        return getPrefs(context).getStringSet(key, defaultValue);
    }

    public static void saveBoolean(Context context, String key, Boolean value){
        getPrefs(context).edit().putBoolean(key, value).apply();
    }
}
