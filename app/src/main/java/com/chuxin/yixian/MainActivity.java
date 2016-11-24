package com.chuxin.yixian;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.yixian.framework.BaseActivity;
import com.chuxin.yixian.model.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements UserListFragment.OnListFragmentInteractionListener {

    /**
     * Tab标题数组
     */
    private String[] tabTitles = new String[] {"信息", "好友", "发现", "我"};

    /**
     * Tab图片数组
     */
    private int[] tabImgs = new int[] {
            R.drawable.selector_tab_weixin,
            R.drawable.selector_tab_friends,
            R.drawable.selector_tab_find,
            R.drawable.selector_tab_me
    };

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentAdapter fragmentAdapter;

    // ViewPage选项卡页面集合
    private List<Fragment> fragmentList;

    // Tab标题集合
    private List<String> tabTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabTitleList = new ArrayList<>();
        for (int i = 0; i < tabTitles.length; i++) {
            tabTitleList.add(tabTitles[i]);
        }

        fragmentList = new ArrayList<>();
        fragmentList.add(MainTabFragment.newInstance(0));
        fragmentList.add(MainTabFragment.newInstance(1));
        fragmentList.add(UserListFragment.newInstance());
        fragmentList.add(MainTabFragment.newInstance(3));

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList, tabTitleList);
        viewPager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来
        tabLayout.setSelectedTabIndicatorHeight(0);  // 设置选中标签下方导航条的高度为0

        for (int i = 0; i < tabTitleList.size(); i++) {

            //获得到对应位置的Tab
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                //设置自定义的标题
                tab.setCustomView(R.layout.main_tab_item);
                TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tab_title);
                textView.setText(tabTitleList.get(i));
                ImageView imageView= (ImageView) tab.getCustomView().findViewById(R.id.tab_img);
                imageView.setImageResource(tabImgs[i]);
            }
        }
        tabLayout.getTabAt(0).getCustomView().setSelected(true);
        setTextColor(tabLayout.getTabAt(0), R.color.colorTabSelected);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTextColor(tab, R.color.colorTabSelected);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setTextColor(tab, R.color.colorTabUnselected);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置Tab按钮文字颜色
     * @param tab tab按钮
     * @param color 颜色
     */
    private void setTextColor(TabLayout.Tab tab, int color) {
        TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tab_title);
        textView.setTextColor(ContextCompat.getColor(getBaseContext(), color));
    }

    /**
     * Fragment切换适配器
     */
    private class FragmentAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList;
        private List<String> titleList;

        /**
         * 构造方法
         */
        public FragmentAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList, List<String> titleList) {
            super(fragmentManager);
            this.fragmentList = fragmentList;
            this.titleList = titleList;
        }

        /**
         * 返回显示的Fragment总数
         */
        @Override
        public int getCount() {
            return fragmentList.size();
        }

        /**
         * 返回要显示的Fragment的某个实例
         */
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        /**
         * 返回每个Tab的标题
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }

    /**
     * 实现FindUserListFragment中定义的OnListFragmentInteractionListener接口中的方法
     * @param user 用户
     */
    @Override
    public void onListFragmentInteraction(User user) {
        Toast.makeText(this, user.getNickName(), Toast.LENGTH_SHORT).show();
    }
}
