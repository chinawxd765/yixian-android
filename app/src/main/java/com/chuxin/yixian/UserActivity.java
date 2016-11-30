package com.chuxin.yixian;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.yixian.enumType.EducationEnum;
import com.chuxin.yixian.enumType.IncomeEnum;
import com.chuxin.yixian.enumType.SexEnum;
import com.chuxin.yixian.framework.Constant;
import com.chuxin.yixian.framework.LogUtil;
import com.chuxin.yixian.framework.MyApplication;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.CacheMode;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserActivity extends AppCompatActivity {

    private static final int WHAT_USER = 0;  // 加载用户信息标志
    private static final int WHAT_USER_HEAD_IMAGE = 2;  // 加载用户头像标志

    // 参数名称
    public static final String USER_ID = "userId";

    private RequestQueue requestQueue;

    /**
     * 启动当前活动
     * @param context 环境
     * @param userId 用户ID
     */
    protected static void start(Context context, long userId) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(USER_ID, userId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        NoHttp.initialize(this.getApplication());
        requestQueue = NoHttp.newRequestQueue();

        try {
            String url = Constant.APP_SERVER_IP
                    .concat(Constant.APP_ROOT_PATH)
                    .concat("/json/user/findUserById.action");
            Request<JSONObject> request = NoHttp.createJsonObjectRequest(url, RequestMethod.GET);
            request.add("userId", getIntent().getLongExtra(USER_ID, 0));

            // 发起请求
            requestQueue.add(WHAT_USER, request, onResponseListener);

        } catch (Exception e) {
            LogUtil.e("error", e.toString());
            finish();
        }
    }

    /**
     * 回调对象，接受请求结果
     */
    private OnResponseListener<JSONObject> onResponseListener = new OnResponseListener<JSONObject>() {

        @SuppressWarnings("unused")
        @Override
        public void onSucceed(int what, Response<JSONObject> response) {

            if (what == WHAT_USER) {

                try {
                    JSONObject result = response.get();// 响应结果
                    JSONObject userJsonOnject = result.getJSONObject("user");

                    // 标题：昵称
                    CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                    if (toolBarLayout != null) {
                        toolBarLayout.setTitle(userJsonOnject.getString("nickName"));
                    }

                    // 头像
                    String headImageSrc = Constant.APP_SERVER_IP
                            .concat(Constant.RESOURCE_ROOT_PATH)
                            .concat(userJsonOnject.getString("headImageSrc"));
                    Request<Bitmap> imageRequest = NoHttp.createImageRequest(headImageSrc);
                    imageRequest.setCacheMode(CacheMode.NONE_CACHE_REQUEST_NETWORK);
                    requestQueue.add(WHAT_USER_HEAD_IMAGE, imageRequest, new SimpleResponseListener<Bitmap>() {

                        @Override
                        public void onSucceed(int i, Response<Bitmap> response) {
                            if (response.get() != null) {
                                ImageView headImageView = (ImageView) findViewById(R.id.headImage);
                                headImageView.setImageBitmap(response.get());
                            }
                        }

                        @Override
                        public void onFailed(int i, String s, Object o, Exception e, int i1, long l) {
                        }
                    });

                    // 性别图标
                    ImageView sexIconView = (ImageView) findViewById(R.id.sexIcon);
                    if (SexEnum.BOY.name().equals(userJsonOnject.getString("sex"))) {
                        sexIconView.setImageBitmap(MyApplication.getBoyIcon());
                    } else if (SexEnum.GIRL.name().equals(userJsonOnject.getString("sex"))) {
                        sexIconView.setImageBitmap(MyApplication.getGirlIcon());
                    }

                    // 年龄
                    TextView ageView = (TextView) findViewById(R.id.age);
                    if (userJsonOnject.getInt("age") > 0) {
                        ageView.setText(userJsonOnject.getString("age"));
                    }

                    // 个性签名
                    TextView signView = (TextView) findViewById(R.id.sign);
                    signView.setText(userJsonOnject.getString("sign"));

                    // 家乡
                    TextView hometownView = (TextView) findViewById(R.id.hometown);
                    hometownView.setText("家乡：".concat(userJsonOnject.getString("hometownArea")));

                    // 学校、专业、年级
                    TextView schoolView = (TextView) findViewById(R.id.school);
                    String school = userJsonOnject.getString("school").concat("  ");
                    school = school.concat(userJsonOnject.getString("major")).concat("  ");
                    school = school.concat(userJsonOnject.getString("grade")).concat("级");
                    schoolView.setText("学校：".concat(school));

                    // 学历
                    TextView educationView = (TextView) findViewById(R.id.education);
                    EducationEnum educationEnum = EducationEnum.valueOf(userJsonOnject.getString("education"));
                    educationView.setText("学历：".concat(educationEnum.getDescription()));

                    // 职业
                    TextView professionView = (TextView) findViewById(R.id.profession);
                    professionView.setText("职业：".concat(userJsonOnject.getString("profession")));

                    // 收入
                    TextView incomeView = (TextView) findViewById(R.id.income);
                    IncomeEnum incomeEnum = IncomeEnum.valueOf(userJsonOnject.getString("income"));
                    incomeView.setText("年收入：".concat(incomeEnum.getDescription()));

                    // 现居住地
                    TextView residenceView = (TextView) findViewById(R.id.residence);
                    residenceView.setText("现居住地：".concat(userJsonOnject.getString("residenceArea")));

                    // 性格描述
                    TextView personalityView = (TextView) findViewById(R.id.personality);
                    personalityView.setText(userJsonOnject.getString("personality"));

                    // 兴趣爱好
                    TextView hobbiesView = (TextView) findViewById(R.id.hobbies);
                    hobbiesView.setText(userJsonOnject.getString("hobbies"));

                    // 事业展望
                    TextView expectationView = (TextView) findViewById(R.id.expectation);
                    expectationView.setText(userJsonOnject.getString("expectation"));

                    // 个人说明
                    TextView descriptionView = (TextView) findViewById(R.id.description);
                    descriptionView.setText(userJsonOnject.getString("description"));

                } catch(Exception e) {
                    Toast.makeText(getBaseContext(), "加载用户信息异常。", Toast.LENGTH_SHORT).show();
                    LogUtil.e("error", "加载用户信息异常。");
                    finish();
                }
            }
        }

        @Override
        public void onStart(int what) {}

        @Override
        public void onFinish(int what) {}

        @Override
        public void onFailed(int what, String url, Object tag, Exception e, int resCode, long ms) {
            // 请求失败
            String errorMsg = String.valueOf(what).concat(",").concat(url);
            if (tag != null) {
                errorMsg = errorMsg.concat(",").concat(tag.toString());
            }
            errorMsg = errorMsg.concat(",").concat(e.toString()).concat(",").concat(String.valueOf(resCode));
            LogUtil.e("onFailed", errorMsg);

            Toast.makeText(getBaseContext(), "加载用户信息失败。", Toast.LENGTH_SHORT).show();
            LogUtil.d("onFailed", "加载用户信息失败。");
            finish();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        doBeforeLeave();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doBeforeLeave();
    }

    /**
     * 退出活动时释放部分资源
     */
    private void doBeforeLeave() {

        if (requestQueue != null) {
            requestQueue.cancelAll();// 退出活动时停止所有请求
            requestQueue.stop();// 退出活动时停止队列
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
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
}
