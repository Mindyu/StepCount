package com.mindyu.step.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mindyu.step.R;
import com.mindyu.step.parameter.SystemParameter;
import com.mindyu.step.user.bean.Info;
import com.mindyu.step.user.bean.Result;
import com.mindyu.step.user.bean.User;
import com.mindyu.step.util.SharedPreferencesUtils;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserActivity extends SwipeBackActivity {

    private String TAG = "UserActivity";
    private EditText name_tv;
    private LinearLayout password_layout;
    private Button quitBtn;
    private SharedPreferencesUtils sp;
    private CommonTitleBar topbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initViews();
        initDatas();
        initEvents();
    }

    private void initViews(){
        name_tv = findViewById(R.id.name_tv);
        password_layout = findViewById(R.id.password);
        quitBtn = findViewById(R.id.quit);
        topbar = findViewById(R.id.topbar);
        topbar.setBackgroundResource(R.drawable.shape_gradient);
    }

    private void initEvents(){
        sp = new SharedPreferencesUtils(this);
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.setParam("direct_login", false);

                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                startActivity(intent);
                UserActivity.this.finish();    // 退出之后不能回退
            }
        });
        password_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, EditPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initDatas(){
        name_tv.setText(SystemParameter.user.getUserName());
    }
}
