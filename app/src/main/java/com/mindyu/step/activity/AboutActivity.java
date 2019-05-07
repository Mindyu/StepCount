package com.mindyu.step.activity;

import android.app.Activity;
import android.os.Bundle;

import com.mindyu.step.R;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

public class AboutActivity extends SwipeBackActivity {

    private CommonTitleBar topbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initView();
    }

    private void initView(){
        topbar = findViewById(R.id.topbar);
        topbar.setBackgroundResource(R.drawable.shape_gradient);
    }
}
