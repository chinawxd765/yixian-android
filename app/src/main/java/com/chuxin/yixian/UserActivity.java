package com.chuxin.yixian;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.yixian.enumType.EducationEnum;
import com.chuxin.yixian.enumType.IncomeEnum;
import com.chuxin.yixian.enumType.SexEnum;
import com.chuxin.yixian.framework.BaseActivity;
import com.chuxin.yixian.framework.Constant;
import com.chuxin.yixian.framework.LogUtil;
import com.chuxin.yixian.framework.MyApplication;
import com.chuxin.yixian.framework.NoHttpUtil;
import com.chuxin.yixian.model.UserImage;
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

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends BaseActivity {

    private static final int WHAT_USER = 0;  // 加载用户信息标志
    private static final int WHAT_USER_HEAD_IMAGE = 2;  // 加载用户头像标志
    private static final int WHAT_USER_IMAGE = 3;  // 加载用户图片标志

    // 参数名称
    public static final String USER_ID = "userId";

    // 用户图片位图列表
    protected static final List<Bitmap> userImageBitmapList = new ArrayList<>();

    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    /**
     * 启动当前活动
     * @param context 环境
     * @param userId 用户ID
     * @param options 转场动画
     */
    protected static void start(Context context, long userId, ActivityOptionsCompat options) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(USER_ID, userId);
        context.startActivity(intent, options.toBundle());
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

        userImageBitmapList.clear();

        recyclerView = (RecyclerView) findViewById(R.id.user_image_recycler_view);
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        requestQueue = NoHttpUtil.newRequestQueue(this.getApplication());

        String url = Constant.APP_SERVER_IP
                .concat(Constant.APP_ROOT_PATH)
                .concat("/json/user/findUserById.action");
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(url, RequestMethod.GET);
        request.add("userId", getIntent().getLongExtra(USER_ID, 0));

        // 发起请求
        requestQueue.add(WHAT_USER, request, onResponseListener);
    }

    /**
     * 回调对象，接受请求结果
     */
    private OnResponseListener<JSONObject> onResponseListener = new OnResponseListener<JSONObject>() {

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {

            if (what == WHAT_USER) {

                try {
                    JSONObject result = response.get();// 响应结果
                    JSONObject userJsonObject = result.getJSONObject("user");

                    // 标题：昵称
                    CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                    if (toolBarLayout != null) {
                        toolBarLayout.setTitle(userJsonObject.getString("nickName"));
                    }

                    // 头像
                    String headImageSrc = Constant.APP_SERVER_IP
                            .concat(Constant.RESOURCE_ROOT_PATH)
                            .concat(userJsonObject.getString("headImageSrc"));
                    Request<Bitmap> imageRequest = NoHttp.createImageRequest(headImageSrc);
                    imageRequest.setCacheMode(CacheMode.NONE_CACHE_REQUEST_NETWORK);
                    requestQueue.add(WHAT_USER_HEAD_IMAGE, imageRequest, new SimpleResponseListener<Bitmap>() {

                        @Override
                        public void onSucceed(int i, Response<Bitmap> response) {
                            if (response.get() != null) {
                                ImageView headImageView = (ImageView) findViewById(R.id.head_image);
                                headImageView.setImageBitmap(response.get());
                            }
                        }

                        @Override
                        public void onFailed(int i, String s, Object o, Exception e, int i1, long l) {
                        }
                    });

                    // 用户图片
                    JSONArray userImageArray = result.getJSONArray("userImageList");
                    if (userImageArray.length() > 0) {
                        recyclerViewAdapter.addUserImageToList(userImageArray);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    // 性别图标
                    ImageView sexIconView = (ImageView) findViewById(R.id.sex_icon);
                    if (SexEnum.BOY.name().equals(userJsonObject.getString("sex"))) {
                        sexIconView.setImageBitmap(MyApplication.getBoyIcon());
                    } else if (SexEnum.GIRL.name().equals(userJsonObject.getString("sex"))) {
                        sexIconView.setImageBitmap(MyApplication.getGirlIcon());
                    }

                    // 年龄
                    if (userJsonObject.get("age") instanceof Integer) {
                        TextView ageView = (TextView) findViewById(R.id.age);
                        ageView.setText(userJsonObject.getString("age"));
                    }

                    // 生日
                    if (userJsonObject.get("birthday") instanceof String) {
                        TextView birthdayView = (TextView) findViewById(R.id.birthday);
                        birthdayView.setText("生日：".concat(userJsonObject.getString("birthday")));
                    }

                    // 个性签名
                    if (userJsonObject.get("sign") instanceof String) {
                        TextView signView = (TextView) findViewById(R.id.sign);
                        signView.setText(userJsonObject.getString("sign"));
                    }

                    // 家乡
                    if (userJsonObject.get("hometownArea") instanceof String) {
                        TextView hometownView = (TextView) findViewById(R.id.hometown);
                        hometownView.setText("家乡：".concat(userJsonObject.getString("hometownArea")));
                    }

                    // 学校、专业、年级
                    String school = "";
                    if (userJsonObject.get("school") instanceof String) {
                        school = userJsonObject.getString("school").concat("  ");
                    }
                    if (userJsonObject.get("major") instanceof String) {
                        school = school.concat(userJsonObject.getString("major")).concat("  ");
                    }
                    if (userJsonObject.get("grade") instanceof String) {
                        school = school.concat(userJsonObject.getString("grade")).concat("级");
                    }
                    if (!school.isEmpty()) {
                        TextView schoolView = (TextView) findViewById(R.id.school);
                        schoolView.setText("学校：".concat(school));
                    }

                    // 学历
                    if (userJsonObject.get("education") instanceof String) {
                        TextView educationView = (TextView) findViewById(R.id.education);
                        EducationEnum educationEnum = EducationEnum.valueOf(userJsonObject.getString("education"));
                        educationView.setText("学历：".concat(educationEnum.getDescription()));
                    }

                    // 职业
                    if (userJsonObject.get("profession") instanceof String) {
                        TextView professionView = (TextView) findViewById(R.id.profession);
                        professionView.setText("职业：".concat(userJsonObject.getString("profession")));
                    }

                    // 收入
                    if (userJsonObject.get("income") instanceof String) {
                        TextView incomeView = (TextView) findViewById(R.id.income);
                        IncomeEnum incomeEnum = IncomeEnum.valueOf(userJsonObject.getString("income"));
                        incomeView.setText("年收入：".concat(incomeEnum.getDescription()));
                    }

                    // 现居住地
                    if (userJsonObject.get("residenceArea") instanceof String) {
                        TextView residenceView = (TextView) findViewById(R.id.residence);
                        residenceView.setText("现居住地：".concat(userJsonObject.getString("residenceArea")));
                    }

                    // 性格描述
                    if (userJsonObject.get("personality") instanceof String) {
                        TextView personalityView = (TextView) findViewById(R.id.personality);
                        personalityView.setText(userJsonObject.getString("personality"));
                    }

                    // 兴趣爱好
                    if (userJsonObject.get("hobbies") instanceof String) {
                        TextView hobbiesView = (TextView) findViewById(R.id.hobbies);
                        hobbiesView.setText(userJsonObject.getString("hobbies"));
                    }

                    // 事业展望
                    if (userJsonObject.get("expectation") instanceof String) {
                        TextView expectationView = (TextView) findViewById(R.id.expectation);
                        expectationView.setText(userJsonObject.getString("expectation"));
                    }

                    // 个人说明
                    if (userJsonObject.get("description") instanceof String) {
                        TextView descriptionView = (TextView) findViewById(R.id.description);
                        descriptionView.setText(userJsonObject.getString("description"));
                    }
                } catch(Exception e) {
                    Toast.makeText(getBaseContext(), "绑定用户信息异常。", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getBaseContext(), "加载用户信息失败。", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        NoHttpUtil.stopRequestQueue(requestQueue);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NoHttpUtil.stopRequestQueue(requestQueue);
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

    /**
     * RecyclerView适配器
     */
    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<UserImage> userImageList = new ArrayList<>();  // 用户图片列表

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_content_image_item, parent, false);
            return new UserImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof UserImageViewHolder) {
                final UserImageViewHolder userImageViewHolder = (UserImageViewHolder) holder;
                final UserImage userImage = userImageList.get(position);
                userImageViewHolder.userImage = userImage;

                // 加载用户图片
                final String userImageSrc = Constant.APP_SERVER_IP
                        .concat(Constant.RESOURCE_ROOT_PATH)
                        .concat(userImage.getImageSrc());
                Request<Bitmap> imageRequest = NoHttp.createImageRequest(userImageSrc);
                imageRequest.setCacheMode(CacheMode.NONE_CACHE_REQUEST_NETWORK);
                requestQueue.add(WHAT_USER_IMAGE, imageRequest, new SimpleResponseListener<Bitmap>() {

                    @Override
                    public void onSucceed(int i, Response<Bitmap> response) {
                        if (response.get() != null) {
                            Bitmap userImageBitmap = response.get();
                            userImageViewHolder.userImageView.setImageBitmap(userImageBitmap);
                            userImageBitmapList.add(userImageBitmap);
                        } else {
                            userImageBitmapList.add(MyApplication.getDefaultUserImage());
                        }

                        // 设置图片高度与宽度相等
                        ViewGroup.LayoutParams layoutParams = userImageViewHolder.userImageView.getLayoutParams();
                        layoutParams.width = userImageViewHolder.userImageView.getWidth();
                        layoutParams.height = layoutParams.width;
                        userImageViewHolder.userImageView.setLayoutParams(layoutParams);
                    }

                    @Override
                    public void onFailed(int i, String s, Object o, Exception e, int i1, long l) {
                    }
                });

                // 单击事件
                userImageViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 转场动画，从当前View拉升到下一个活动
                        // 在API 21以上生效
                        ActivityOptionsCompat options =
                                ActivityOptionsCompat.makeScaleUpAnimation(
                                        v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);
                        UserImageActivity.start(v.getContext(), position, options);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return userImageList.size();
        }

        /**
         * 将用户图片Json数组转换后加入用户图片列表
         * @param userImageArray 用户图片Json数组
         */
        public void addUserImageToList(JSONArray userImageArray) {

            try {
                for (int i = 0; i < userImageArray.length(); i++) {
                    JSONObject userJsonOnject = userImageArray.getJSONObject(i);
                    UserImage userImage = new UserImage();

                    userImage.setId(userJsonOnject.getLong("id"));
                    userImage.setImageSrc(userJsonOnject.getString("imageSrc"));

                    userImageList.add(userImage);
                }
            } catch(Exception e) {
                LogUtil.e("error", "将用户图片Json数组转换后加入用户图片列表异常。");
            }
        }

        /**
         * 用户图片项
         */
        public class UserImageViewHolder extends RecyclerView.ViewHolder {
            public final View view;
            public final ImageView userImageView;
            public UserImage userImage;

            public UserImageViewHolder(View view) {
                super(view);
                this.view = view;
                this.userImageView = (ImageView) view.findViewById(R.id.user_image);
            }
        }
    }

}
