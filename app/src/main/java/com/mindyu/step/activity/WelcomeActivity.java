package com.mindyu.step.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.mindyu.step.R;
import com.mindyu.step.adapter.SplashAdapter;
import com.mindyu.step.view.scroll.ScollLinearLayoutManager;

public class WelcomeActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Button go_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        go_btn = findViewById(R.id.go_btn);
        go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                WelcomeActivity.this.finish();
            }
        });
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setAdapter(new SplashAdapter(WelcomeActivity.this));
        mRecyclerView.setLayoutManager(new ScollLinearLayoutManager(WelcomeActivity.this));
        mRecyclerView.smoothScrollToPosition(Integer.MAX_VALUE / 2);
    }
}
