package com.mindyu.step.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mindyu.step.R;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

/**
 * 发现页面
 */
public class HotTopicFragment extends Fragment {

    private CommonTitleBar topbar;

    public HotTopicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic, container, false);

        initView(view);
        initEvent(view);
        return view;
    }

    private void initView(View view) {
        topbar = view.findViewById(R.id.topbar);
        topbar.getLeftTextView().setText("发现");
        topbar.setBackgroundResource(R.drawable.shape_gradient);
    }
    private void initEvent(View view) {
        topbar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_RIGHT_BUTTON) {
                    Toast.makeText(getContext(), "搜索", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
