package com.mindyu.step.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mindyu.step.R;
import com.mindyu.step.parameter.SystemParameter;
import com.mindyu.step.user.bean.Result;
import com.mindyu.step.user.bean.User;
import com.mindyu.step.util.SharedPreferencesUtils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends SwipeBackActivity {

    private static final int REQUEST_SIGNUP = 1;
    private UserLoginTask mAuthTask = null;

    private EditText name_edit;
    private EditText password_edit;
    private CheckBox cbRememberPass;
    private CheckBox autologin;
    private SharedPreferencesUtils sp;
    private Button signBtn;
    private ProgressDialog progressDialog;
    private TextView signup_tv;


    @Override
    protected boolean isSwipeBackEnable() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initEvent();
        initText();
    }

    private void initViews() {
        name_edit = findViewById(R.id.name_et);
        password_edit = findViewById(R.id.password_et);
        cbRememberPass = findViewById(R.id.cbRememberPass);
        autologin = findViewById(R.id.autologin);
        signBtn = findViewById(R.id.sign_in_button);
        signup_tv = findViewById(R.id.link_signup);
    }

    private void initEvent() {
        signBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        signup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                // Activity切换
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });
    }

    private void initText() {
        //从SharedPreferencesZ中读取用户信息
        sp = new SharedPreferencesUtils(this);
        String name = sp.getParam("user_name", "").toString();
        String pass = sp.getParam("password", "").toString();

        boolean choseRemember = (boolean) sp.getParam("remember_pass", false);
        boolean choseAutoLogin = (boolean) sp.getParam("auto_login", false);
        boolean directLogin = (boolean) sp.getParam("direct_login", false);

        //将用户名和密码设置到文本框中
        name_edit.setText(name);
        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
        if (choseRemember) {
            password_edit.setText(pass);
            cbRememberPass.setChecked(true);

            //如果上次登录选了自动登录，那进入登录页面也自动勾选自动登录
            if (choseAutoLogin) {
                autologin.setChecked(true);
                if (directLogin)
                    // callOnClick 不用用户手动点击直接触发view的点击事件
                    signBtn.callOnClick();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                String username = data.getStringExtra("username");
                String password = data.getStringExtra("password");
                name_edit.setText(username);
                password_edit.setText(password);
            }
        }
    }

    // 验证用户信息、执行登录
    private void attemptLogin() {

        if (!validate()) {
            onLoginFailed();
            return;
        }
        signBtn.setEnabled(false);

        //验证信息时弹出对话框
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = name_edit.getText().toString();
        String password = password_edit.getText().toString();

        mAuthTask = new UserLoginTask(email, password);
        mAuthTask.execute((Void) null);
    }

    private void dismissDialog() {
        progressDialog.dismiss();
        signBtn.setEnabled(true);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        signBtn.setEnabled(true);
    }

    public boolean validate() {

        String username = name_edit.getText().toString();
        String password = password_edit.getText().toString();

        if (username.isEmpty() || username.length() < 4) {
            name_edit.setError("enter a valid username");
            return false;
        } else {
            name_edit.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            password_edit.setError("between 4 and 10 alphanumeric characters");
            return false;
        } else {
            password_edit.setError(null);
        }

        return true;
    }

    // 用户登录的异步任务，自定义UserLoginTask
    /**/
    public class UserLoginTask extends AsyncTask<Void, Void, Result<User>> {

        private final String mName;
        private final String mPassword;
        private String errorMassage;

        UserLoginTask(String email, String password) {
            mName = email;
            mPassword = password;
        }

        //重写doInBackground方法，处理所有的耗时任务
        @Override
        protected Result<User> doInBackground(Void... params) {

            //创建一个OkHttpClient实例
            OkHttpClient httpclient = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            User user = new User();
            user.setUserName(mName);
            user.setPassword(mPassword);
            Gson gson = new Gson();
            //将对象转换为诶JSON格式字符串
            String jsonStr = gson.toJson(user);
            RequestBody body = RequestBody.create(JSON, jsonStr);
            //创建request对象
            Request request = new Request.Builder()
                    .url(SystemParameter.ip + "/user/login")
                    .post(body)
                    .build();
            try {
                Log.d("Request:", request.toString());
                //发送请求并获取服务器返回的数据
                Response response = httpclient.newCall(request).execute();
                String data = response.body().string();
                Log.d("Response:", data);
                return parseJSonWithGSON(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        //利用GSON开源库解析JSON数据
        private Result<User> parseJSonWithGSON(final String data) {
            Gson gson = new Gson();
            Result<User> result = gson.fromJson(data, new TypeToken<Result<User>>() {
            }.getType());
            return result;
        }


        @Override
        protected void onPostExecute(final Result<User> result) {
            mAuthTask = null;
            dismissDialog();

            if (result != null && result.getData() != null) {
                //保存用户名和密码
                sp.setParam("user_name", mName);
                sp.setParam("password", mPassword);

                //是否记住密码
                sp.setParam("remember_pass", cbRememberPass.isChecked());
                //是否自动登录
                sp.setParam("auto_login", autologin.isChecked());
                if (autologin.isChecked())
                    sp.setParam("direct_login", true);

                SystemParameter.user = result.getData();

                //传递字符串到MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", mName);
                intent.putExtras(bundle);
                startActivity(intent);

                LoginActivity.this.finish();    // 登录成功之后不能回退
            } else {
                password_edit.setError(result == null ? "登录异常" : result.getMessage());
                password_edit.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            dismissDialog();
        }
    }
}

