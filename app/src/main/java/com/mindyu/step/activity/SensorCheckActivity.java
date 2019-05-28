package com.mindyu.step.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mindyu.step.R;
import com.mindyu.step.step.service.StepService;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

public class SensorCheckActivity extends SwipeBackActivity {

    private String TAG = "SensorCheckActivity";
    private CommonTitleBar topbar;
    private ImageView mCheckIv;
    private Button backBtn;
    private BroadcastReceiver mBatInfoReceiver;
    private int initialStepCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_check);

        initView();
        initEvents();
        initialStepCount = StepService.CURRENT_STEP;
        initBroadcastReceiver();
    }

    private void initView() {
        topbar = findViewById(R.id.topbar);
        backBtn = findViewById(R.id.back_btn);
        mCheckIv = findViewById(R.id.check_iv);
        topbar.setBackgroundResource(R.drawable.shape_gradient);
    }

    private void initEvents() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorCheckActivity.this.finish();
            }
        });
    }

    /**
     * 注册广播
     */
    private void initBroadcastReceiver() {
        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        // filter.addAction(Intent.ACTION_USER_PRESENT);
        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    Log.d(TAG, "screen on");
                    int interval = StepService.CURRENT_STEP - initialStepCount;
                    if (interval >= 1) {
                        mCheckIv.setBackground(getResources().getDrawable(R.mipmap.check_success));
                    } else {
                        mCheckIv.setBackground(getResources().getDrawable(R.mipmap.check_fail));
                    }
                    backBtn.setVisibility(View.VISIBLE);
                    Log.d(TAG, "interval:" + interval);
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    Log.d(TAG, "screen off");
                    initialStepCount = StepService.CURRENT_STEP;
                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBatInfoReceiver);
    }
}
