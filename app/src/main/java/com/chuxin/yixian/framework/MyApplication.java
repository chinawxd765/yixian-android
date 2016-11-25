package com.chuxin.yixian.framework;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.chuxin.yixian.R;

/**
 * Created by wujunda on 2016/11/25.
 */
public class MyApplication extends Application {

    /**
     * 男女性别图标
     */
    private static Bitmap boyIcon;
    private static Bitmap girlIcon;

    @Override
    public void onCreate() {

        super.onCreate();

        Resources resouces = getResources();
        boyIcon = BitmapFactory.decodeResource(resouces, R.drawable.icon_boy);
        girlIcon = BitmapFactory.decodeResource(resouces, R.drawable.icon_girl);

    }

    public static Bitmap getBoyIcon() {
        return boyIcon;
    }

    public static Bitmap getGirlIcon() {
        return girlIcon;
    }

}
