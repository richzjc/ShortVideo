package com.tbruyelle.rxpermissions3;

import android.content.Context;
public class SpUtil {
    public static int getCount(Context context, String keyPermission){
        return context.getSharedPreferences("permission_config", Context.MODE_PRIVATE).getInt(keyPermission, 0);
    }

    public static void saveCount(Context context, String keyPermission){
        int count = getCount(context, keyPermission);
        context.getSharedPreferences("permission_config", Context.MODE_PRIVATE).edit().putInt(keyPermission, count + 1).apply();
    }
}
