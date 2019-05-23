package com.mindyu.step.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.mindyu.step.R;
import com.mindyu.step.util.SharedPreferencesUtils;
import com.mindyu.step.util.DateTimePickerUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;


public class SetPlanActivity extends SwipeBackActivity implements View.OnClickListener {

    private SharedPreferencesUtils sp;

    private EditText tv_step_number;
    private CheckBox cb_remind;
    private TextView tv_remind_time;
    private Button btn_save;
    private String walk_qty;
    private String remind;
    private String achieveTime;
    private CommonTitleBar topbar;

    private void initView() {
        tv_step_number = (EditText) findViewById(R.id.tv_step_number);
        cb_remind = (CheckBox) findViewById(R.id.cb_remind);
        tv_remind_time = (TextView) findViewById(R.id.tv_remind_time);
        btn_save = (Button) findViewById(R.id.btn_save);
        topbar = findViewById(R.id.topbar);
        topbar.setBackgroundResource(R.drawable.shape_gradient);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_exercise_plan);
        initView();
        initData();
        addListener();
    }

    public void initData() {//获取锻炼计划
        sp = new SharedPreferencesUtils(this);
        String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "10000");
        String remind = (String) sp.getParam("remind", "1");
        String achieveTime = (String) sp.getParam("achieveTime", "20:00");
        if (!planWalk_QTY.isEmpty()) {
            if ("0".equals(planWalk_QTY)) {
                tv_step_number.setText("10000");
            } else {
                tv_step_number.setText(planWalk_QTY);
            }
        }
        if (!remind.isEmpty()) {
            if ("0".equals(remind)) {
                cb_remind.setChecked(false);
            } else if ("1".equals(remind)) {
                cb_remind.setChecked(true);
            }
        }

        if (!achieveTime.isEmpty()) {
            tv_remind_time.setText(achieveTime);
        }

    }


    public void addListener() {
        btn_save.setOnClickListener(this);
        tv_remind_time.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                save();
                break;
            case R.id.tv_remind_time:
                DateTimePickerUtil.showTimeDialog(this, tv_remind_time);
                break;
        }
    }

    private void save() {
        walk_qty = tv_step_number.getText().toString().trim();
        if (cb_remind.isChecked()) {
            remind = "1";
        } else {
            remind = "0";
        }
        achieveTime = tv_remind_time.getText().toString().trim();
        if (walk_qty.isEmpty() || "0".equals(walk_qty)) {
            sp.setParam("planWalk_QTY", "10000");
        } else {
            sp.setParam("planWalk_QTY", walk_qty);
        }
        sp.setParam("remind", remind);

        if (achieveTime.isEmpty()) {
            sp.setParam("achieveTime", "21:00");
            this.achieveTime = "21:00";
        } else {
            sp.setParam("achieveTime", achieveTime);
        }
        finish();
    }

}

