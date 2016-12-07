package com.chuxin.yixian;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.chuxin.yixian.framework.BaseActivity;

import java.util.List;

public class UserImageActivity extends BaseActivity {

    private static final String POSITION = "position";

    private ViewPager viewPager;
    private UserImageFragmentAdapter userImageFragmentAdapter;

    /**
     * 启动当前活动
     * @param context 环境
     * @param position 位置
     * @param options 转场动画
     */
    protected static void start(Context context, int position, ActivityOptionsCompat options) {
        Intent intent = new Intent(context, UserImageActivity.class);
        intent.putExtra(POSITION, position);
        context.startActivity(intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_image);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        userImageFragmentAdapter = new UserImageFragmentAdapter(getSupportFragmentManager(), UserActivity.userImageBitmapList);
        viewPager.setAdapter(userImageFragmentAdapter);
        viewPager.setCurrentItem(getIntent().getIntExtra(POSITION, 0));
    }

    /**
     * 用户图片适配器
     */
    public class UserImageFragmentAdapter extends FragmentStatePagerAdapter {

        private List<Bitmap> userImageBitmapList;  // 用户图片位图列表

        public UserImageFragmentAdapter(FragmentManager fm, List<Bitmap> userImageBitmapList) {
            super(fm);
            this.userImageBitmapList = userImageBitmapList;
        }

        @Override
        public Fragment getItem(int position) {
            return UserImageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return userImageBitmapList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
