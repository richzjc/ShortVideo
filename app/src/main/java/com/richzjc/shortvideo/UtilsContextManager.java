package com.richzjc.shortvideo;

import android.app.Activity;
import android.app.Application;

/**
 * Created by zhangyang on 16/6/24.
 */
public class UtilsContextManager {
    private Application application;
    private static UtilsContextManager instance;

    // 采用双重检查加锁实例化单件
    public static UtilsContextManager getInstance() {
        // 第一次检查
        if (instance == null) {
            synchronized (UtilsContextManager.class) {
                // 第二次检查
                if (instance == null) {
                    instance = new UtilsContextManager();
                }
            }
        }
        return instance;
    }

    private UtilsContextManager() {

    }

    public synchronized UtilsContextManager init(Application application) {
        if (this.application == null)
            this.application = application;
        return this;
    }

    public Application getApplication() {
        return application;
    }
}
