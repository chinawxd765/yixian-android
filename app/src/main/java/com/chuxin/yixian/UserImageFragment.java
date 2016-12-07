package com.chuxin.yixian;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by wujunda on 2016/12/6.
 */
public class UserImageFragment extends Fragment {

    private static final String POSITION_ID = "positionId";

    public static UserImageFragment newInstance(int positionId) {
        UserImageFragment fragment = new UserImageFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION_ID, positionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_image, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.user_image);
        imageView.setImageBitmap(UserActivity.userImageBitmapList.get(getArguments().getInt(POSITION_ID)));

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return rootView;
    }
}
