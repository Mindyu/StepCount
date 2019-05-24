package com.mindyu.step.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mindyu.library.UpdateChecker;
import com.mindyu.step.R;
import com.mindyu.step.activity.AboutActivity;
import com.mindyu.step.activity.LimitSettingActivity;
import com.mindyu.step.activity.SensorCheckActivity;
import com.mindyu.step.activity.UserActivity;
import com.mindyu.step.activity.UserInfoActivity;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    private LinearLayout user_information;
    private LinearLayout user_management;
    private LinearLayout limit_setting;
    private LinearLayout soft_update;
    private LinearLayout sensor_test;
    private LinearLayout about_system;
    private CommonTitleBar topbar;

    public UserFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initView(view);
        initEvent();
        return view;
    }

    public void initView(View view) {
        user_information = view.findViewById(R.id.user_information);
        user_management = view.findViewById(R.id.user_management);
        limit_setting = view.findViewById(R.id.limit_setting);
        soft_update = view.findViewById(R.id.soft_update);
        sensor_test = view.findViewById(R.id.sensor_test);
        about_system = view.findViewById(R.id.about_system);
        topbar = view.findViewById(R.id.topbar);
        topbar.getLeftTextView().setText("我的");
        topbar.setBackgroundResource(R.drawable.shape_gradient);
    }

    public void initEvent() {

        user_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserInfoActivity.class);
                startActivity(intent);
            }
        });
        user_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                startActivity(intent);
            }
        });
        limit_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LimitSettingActivity.class);
                startActivity(intent);
            }
        });
        soft_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateChecker.checkForDialog(getContext());
            }
        });
        sensor_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SensorCheckActivity.class);
                startActivity(intent);
            }
        });
        about_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AboutActivity.class);
                startActivity(intent);
            }
        });
    }
}
