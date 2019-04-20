package com.mindyu.step.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mindyu.step.R;
import com.mindyu.step.activity.UserActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    private LinearLayout user_management;
    private LinearLayout date_update;
    private LinearLayout soft_update;
    private LinearLayout sensor_test;
    private LinearLayout about_system;

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
        user_management = view.findViewById(R.id.user_management);
        date_update = view.findViewById(R.id.date_update);
        soft_update = view.findViewById(R.id.soft_update);
        sensor_test = view.findViewById(R.id.sensor_test);
        about_system = view.findViewById(R.id.about_system);
    }

    public void initEvent() {
        user_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                startActivity(intent);
            }
        });
        date_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                startActivity(intent);
            }
        });
        soft_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                startActivity(intent);
            }
        });
        sensor_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                startActivity(intent);
            }
        });
        about_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                startActivity(intent);
            }
        });
    }
}
