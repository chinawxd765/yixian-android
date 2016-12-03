package com.chuxin.yixian;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuxin.yixian.enumType.SexEnum;
import com.chuxin.yixian.framework.Constant;
import com.chuxin.yixian.framework.LogUtil;
import com.chuxin.yixian.framework.MyApplication;
import com.chuxin.yixian.framework.NoHttpUtil;
import com.chuxin.yixian.model.User;
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
import java.util.HashMap;
import java.util.List;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UserListFragment extends Fragment {

    private static final int WHAT_USER_LIST_ONLOAD = 0;  // 初次加载用户列表标志
    private static final int WHAT_USER_LIST_UP_LOADMORE = 1;  // 上拉刷新加载用户列表标志
    private static final int WHAT_USER_HEAD_IMAGE = 2;  // 加载用户头像标志

    private int firstVisibleItemPosition = 0;    // 屏幕可见首个显示项目的序列号
    private int firstVisibleItemTop = 0;    // 屏幕可见首个显示项目与顶部的偏移
    private int lastVisibleItemPosition;    // 屏幕可见最后一个显示项目的序列号

    private int pageNo = 0;  // 当前页码
    private HashMap<String, Bitmap> headImageMap = new HashMap<>();  // 已加载的头像列表

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private RecyclerViewAdapter recyclerViewAdapter;
    private OnListFragmentInteractionListener mListener;

    /**
     * 创建新实例
     * @return FindUserListFragment实例
     */
    static UserListFragment newInstance() {
        return new UserListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = NoHttpUtil.newRequestQueue(this.getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.user_list_swipe_refresh_layout);
//        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.BLUE);
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.YELLOW, Color.RED);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                //TODO 下拉刷新
                LogUtil.i("recyclerView", "=======================下拉========================");
                swipeRefreshLayout.setRefreshing(false);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.user_list_recycler_view);
        if (recyclerViewAdapter == null) {

            // 首次载入的时候初始化适配器
            recyclerViewAdapter = new RecyclerViewAdapter();
            recyclerView.setAdapter(recyclerViewAdapter);

            // 首次载入的时候显示顶部刷新工具条
            swipeRefreshLayout.setRefreshing(true);

            pageNo = pageNo + 1;   // 当前页码加1
            setupRecyclerView(WHAT_USER_LIST_ONLOAD);
        } else {
            recyclerView.setAdapter(recyclerViewAdapter);

            // 第二次载入，直接定位到上一次离开时的位置
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            linearLayoutManager.scrollToPositionWithOffset(firstVisibleItemPosition, firstVisibleItemTop);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_SETTLING
                        && !recyclerViewAdapter.isLoading()  // 不是加载中状态
                        && (lastVisibleItemPosition + 1 == recyclerViewAdapter.getItemCount()    // 已显示到最后一项目，或，显示0个项目
                        || recyclerViewAdapter.getItemCount() == 0)) {

                    LogUtil.i("recyclerView", "=======================上拉========================");

                    // 上拉刷新的时候，userList加入null，显示底部Loading项
                    recyclerViewAdapter.addNullToLast();
                    recyclerViewAdapter.notifyDataSetChanged();

                    pageNo = pageNo + 1;  // 上拉刷新的时候，当前页码加1
                    setupRecyclerView(WHAT_USER_LIST_UP_LOADMORE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        doBeforeLeave();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doBeforeLeave();
    }

    /**
     * 记录当前屏幕相关信息及释放部分资源
     */
    private void doBeforeLeave() {

        mListener = null;
        NoHttpUtil.stopRequestQueue(requestQueue);

        // 获得屏幕可见首个显示项目的序列及偏移
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        View view = linearLayoutManager.findViewByPosition(firstVisibleItemPosition);
        if (view != null) {
            firstVisibleItemTop = view.getTop();
        }
    }

    /**
     * 查询当前页用户列表，并绑定到recyclerView
     */
    private void setupRecyclerView(int what) {

        try {
            String url = Constant.APP_SERVER_IP
                    .concat(Constant.APP_ROOT_PATH)
                    .concat("/json/user/findPageUserList.action");
            Request<JSONObject> request = NoHttp.createJsonObjectRequest(url, RequestMethod.GET);
            request.add("pageNo", pageNo);

            // 发起请求
            requestQueue.add(what, request, onResponseListener);

        } catch (Exception e) {
            LogUtil.e("error", e.toString());
        }
    }

    /**
     * 回调对象，接受请求结果
     */
    private OnResponseListener<JSONObject> onResponseListener = new OnResponseListener<JSONObject>() {

        @SuppressWarnings("unused")
        @Override
        public void onSucceed(int what, Response<JSONObject> response) {

            if (what == WHAT_USER_LIST_ONLOAD) {

                // 首次载入的时候会显示刷新工具条，此时需要隐藏刷新工具条
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                addToUserList(response);
            } else if (what == WHAT_USER_LIST_UP_LOADMORE) {

                // 上拉刷新的时候，为了显示底部Loading工具条，userList会加入null，此时需要删除null数据
                recyclerViewAdapter.removeNullFromLast();

                addToUserList(response);
            }
            recyclerViewAdapter.notifyDataSetChanged();
        }

        /**
         * 追加用户列表
         * @param response 远程请求响应结果
         */
        private void addToUserList(Response<JSONObject> response) {
            try {
                JSONObject result = response.get();// 响应结果
                JSONArray userArray = result.getJSONArray("userList");

                if (userArray.length() > 0) {
                    recyclerViewAdapter.addUserToList(userArray);
                } else {
                    Toast.makeText(getContext(), "没有了。", Toast.LENGTH_SHORT).show();
                }
            } catch(Exception e) {
                LogUtil.e("error", "绑定用户列表异常。");
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

            Toast.makeText(getContext(), "加载用户数据失败。", Toast.LENGTH_SHORT).show();

            if (what == WHAT_USER_LIST_ONLOAD) {

                // 首次载入的时候会显示刷新工具条，此时需要隐藏刷新工具条
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            } else if (what == WHAT_USER_LIST_UP_LOADMORE) {

                // 上拉刷新的时候，为了显示底部Loading工具条，userList会加入null，此时需要删除null数据
                recyclerViewAdapter.removeNullFromLast();
                recyclerViewAdapter.notifyDataSetChanged();
            }

            // 不翻页，保留当前页码
            pageNo = pageNo - 1;
        }
    };

    /**
     * RecyclerView适配器
     */
    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int USER_ITEM = 0;  // 用户项
        private static final int USER_LOADING = 1;  // “加载中”项

        private List<User> userList = new ArrayList<>();  // 用户列表

        @Override
        public int getItemViewType(int position) {
            if (userList.get(position) == null) {
                return USER_LOADING;
            } else {
                return USER_ITEM;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == USER_ITEM) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
                return new UserViewHolder(view);
            } else if (viewType == USER_LOADING) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_loading, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof UserViewHolder) {
                final UserViewHolder userViewHolder = (UserViewHolder) holder;
                final User user = userList.get(position);
                userViewHolder.user = user;

                userViewHolder.nickNameView.setText(user.getNickName());
                userViewHolder.signView.setText(user.getSign());
                userViewHolder.ageView.setText(user.getAge());

                // 设置性别图标
                if (SexEnum.BOY.name().equals(user.getSex())) {
                    userViewHolder.sexIconView.setImageBitmap(MyApplication.getBoyIcon());
                } else if (SexEnum.GIRL.name().equals(user.getSex())) {
                    userViewHolder.sexIconView.setImageBitmap(MyApplication.getGirlIcon());
                }

                // 加载头像
                final String headImageSrc = Constant.APP_SERVER_IP
                        .concat(Constant.RESOURCE_ROOT_PATH)
                        .concat(user.getHeadImageSrc());

                if (headImageMap.containsKey(headImageSrc)) {
                    userViewHolder.headImageView.setImageBitmap(headImageMap.get(headImageSrc));
                    LogUtil.d("headImage", String.valueOf(user.getId()) + "," + headImageSrc);
                } else {
                    // 加载默认头像
                    userViewHolder.headImageView.setImageResource(R.drawable.default_head_image);

                    Request<Bitmap> imageRequest = NoHttp.createImageRequest(headImageSrc);
                    imageRequest.setCacheMode(CacheMode.NONE_CACHE_REQUEST_NETWORK);
                    requestQueue.add(WHAT_USER_HEAD_IMAGE, imageRequest, new SimpleResponseListener<Bitmap>() {

                        @Override
                        public void onSucceed(int i, Response<Bitmap> response) {
                            if (response.get() != null) {
                                userViewHolder.headImageView.setImageBitmap(response.get());
                                headImageMap.put(headImageSrc, response.get());
                                LogUtil.d("headImage", String.valueOf(user.getId()) + "," + headImageSrc);
                            }
                        }

                        @Override
                        public void onFailed(int i, String s, Object o, Exception e, int i1, long l) {
                        }
                    });
                }

                // 单击事件
                userViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (null != mListener) {
                            // Notify the active callbacks interface (the activity, if the
                            // fragment is attached to one) that an item has been selected.
                            mListener.onListFragmentInteraction(userViewHolder.user, userViewHolder.headImageView);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        /**
         * 将用户Json数组转换后加入用户列表
         * @param userArray 用户Json数组
         */
        public void addUserToList(JSONArray userArray) {

            try {
                for (int i = 0; i < userArray.length(); i++) {
                    JSONObject userJsonObject = userArray.getJSONObject(i);
                    final User user = new User();

                    user.setId(userJsonObject.getLong("id"));
                    user.setNickName(userJsonObject.getString("nickName"));
                    user.setSex(userJsonObject.getString("sex"));
                    user.setHeadImageSrc(userJsonObject.getString("headImageSrc"));

                    if (userJsonObject.get("sign") instanceof String) {
                        user.setSign(userJsonObject.getString("sign"));
                    }

                    if (userJsonObject.get("age") instanceof Integer) {
                        user.setAge(userJsonObject.getString("age"));
                    }

                    userList.add(user);
                }
            } catch(Exception e) {
                LogUtil.e("error", "将用户Json数组转换后加入用户列表异常。");
            }
        }

        /**
         * 将Null对象加入用户列表末尾
         */
        public void addNullToLast() {
            userList.add(null);
        }

        /**
         * 将Null对象从用户列表末尾移除
         */
        public void removeNullFromLast() {
            if(isLoading()) {
                userList.remove(getItemCount() - 1);
            }
        }

        /**
         * 判断当前是否载入中
         * @return 当前是否载入中标志
         */
        public boolean isLoading() {
            return (getItemCount() > 0 && userList.get(getItemCount() - 1) == null);
        }

        /**
         * 用户项
         */
        public class UserViewHolder extends RecyclerView.ViewHolder {
            public final View view;
            public final ImageView headImageView;
            public final TextView nickNameView;
            public final ImageView sexIconView;
            public final TextView signView;
            public final TextView ageView;
            public User user;

            public UserViewHolder(View view) {
                super(view);
                this.view = view;
                this.headImageView = (ImageView) view.findViewById(R.id.head_image);
                this.nickNameView = (TextView) view.findViewById(R.id.nick_name);
                this.sexIconView = (ImageView) view.findViewById(R.id.sex_icon);
                this.signView = (TextView) view.findViewById(R.id.sign);
                this.ageView = (TextView) view.findViewById(R.id.age);
            }
        }

        /**
         * “加载中”项
         */
        public class LoadingViewHolder extends RecyclerView.ViewHolder {
            public LoadingViewHolder(View view) {
                super(view);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(User user, View view);
    }
}
