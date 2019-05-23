package com.mindyu.step.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mindyu.step.R;
import com.mindyu.step.parameter.SystemParameter;
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

public class EditPasswordActivity extends SwipeBackActivity {

    private String TAG = "EditPasswordActivity";
    private EditText origin_et;
    private EditText password_1_et;
    private EditText password_2_et;
    private Button saveBtn;
    private CommonTitleBar topbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        initViews();
        initEvents();
    }

    private void initViews(){
        origin_et = findViewById(R.id.origin_pass_et);
        password_1_et = findViewById(R.id.pass_1_et);
        password_2_et = findViewById(R.id.pass_2_et);
        saveBtn = findViewById(R.id.save_btn);
        topbar = findViewById(R.id.topbar);
        topbar.setBackgroundResource(R.drawable.shape_gradient);
    }

    private void initEvents(){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password1 = password_1_et.getText().toString();
                String password2 = password_2_et.getText().toString();
                if (!password1.equals(password2)){
                    Toast.makeText(EditPasswordActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                SystemParameter.user.setPassword(password2);
                new UserSaveTask().execute(SystemParameter.user);

                Intent intent = new Intent(EditPasswordActivity.this, LoginActivity.class);
                         // .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class UserSaveTask extends AsyncTask<User, Void, Boolean> {
        @Override
        protected Boolean doInBackground(User... users) {
            OkHttpClient okHttpClient = new OkHttpClient();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            //将对象转换为诶JSON格式字符串
            String jsonStr = gson.toJson(users[0]);
            RequestBody body = RequestBody.create(JSON, jsonStr);

            Request request = new Request.Builder()
                    .url(SystemParameter.ip + "/user/" + SystemParameter.user.getId())
                    .put(body)
                    .build();
            Log.d(TAG, "request url: " + request);
            Call call = okHttpClient.newCall(request);
            try {
                Response response = call.execute();
                if (response.body() == null) {
                    Log.d(TAG, "onResponse: 修改密码失败");
                    return null;
                }
                String data = response.body().string();
                Log.d(TAG, "onResponse: " + data);

                Result result = gson.fromJson(data, new TypeToken<Result>() {
                }.getType());
                if (result.getCode() == 200) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(EditPasswordActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(EditPasswordActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }
}
