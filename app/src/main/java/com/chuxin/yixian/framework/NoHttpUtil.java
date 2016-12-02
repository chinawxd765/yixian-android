package com.chuxin.yixian.framework;

import android.app.Application;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.RequestQueue;

/**
 * NoHttp工具类
 * Created by wujunda on 2016/12/1.
 */
public class NoHttpUtil {

    /**
     * 创建RequestQueue对象
     * @param application 应用类
     * @return RequestQueue对象
     */
    public static final RequestQueue newRequestQueue(Application application) {
        NoHttp.initialize(application);
        return NoHttp.newRequestQueue();
    }

    /**
     * 停止所有请求和队列
     * @param requestQueue RequestQueue对象
     */
    public static final void stopRequestQueue(RequestQueue requestQueue) {
        if (requestQueue != null) {
            requestQueue.cancelAll();// 停止所有请求
            requestQueue.stop();// 停止队列
        }
    }
}
