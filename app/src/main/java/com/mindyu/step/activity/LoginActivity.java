package com.mindyu.step.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends SwipeBackActivity {

    private static final int REQUEST_SIGNUP = 1;
    private UserLoginTask mAuthTask = null;

    private EditText email_edit;
    private EditText password_edit;
    private CheckBox cbRememberPass;
    private CheckBox autologin;
    private SharedPreferences sp;
    private Button signBtn;
    private ProgressDialog progressDialog;
    private TextView signup_tv;
    // private CommonTitleBar topbar;

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
        email_edit = findViewById(R.id.email);
        password_edit = findViewById(R.id.password);
        cbRememberPass = findViewById(R.id.cbRememberPass);
        autologin = findViewById(R.id.autologin);
        signBtn = findViewById(R.id.sign_in_button);
        signup_tv = findViewById(R.id.link_signup);
        //topbar = findViewById(R.id.topbar);
        //topbar.setBackgroundResource(R.drawable.shape_gradient);
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
                // startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        /*topbar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_RIGHT_BUTTON) {
                    Toast.makeText(LoginActivity.this, "搜索", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void initText() {
        sp = getSharedPreferences("userInfo", 0);
        String name = sp.getString("user_name", "");
        String pass = sp.getString("password", "");

        boolean choseRemember = sp.getBoolean("remember_pass", false);
        boolean choseAutoLogin = sp.getBoolean("auto_login", false);
        boolean directLogin = sp.getBoolean("direct_login", false);

        email_edit.setText(name);
        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
        if (choseRemember) {
            password_edit.setText(pass);
            cbRememberPass.setChecked(true);

            //如果上次登录选了自动登录，那进入登录页面也自动勾选自动登录
            if (choseAutoLogin) {
                autologin.setChecked(true);
                if (directLogin)
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
                email_edit.setText(username);
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

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = email_edit.getText().toString();
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
        boolean valid = true;

        String username = email_edit.getText().toString();
        String password = password_edit.getText().toString();

        if (username.isEmpty()) {
            email_edit.setError("enter a valid username");
            valid = false;
        } else {
            email_edit.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            email_edit.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            email_edit.setError(null);
        }

        return valid;
    }

    // 用户登录的异步任务
    public class UserLoginTask extends AsyncTask<Void, Void, Result<User>> {

        private final String mName;
        private final String mPassword;
        private String errorMassage;

        UserLoginTask(String email, String password) {
            mName = email;
            mPassword = password;
        }

        @Override
        protected Result<User> doInBackground(Void... params) {

//            UserDao userDao = UserDao.getInstance();
//            Result result = userDao.login(mName, mPassword);
//            errorMassage = result.getMessage();

            OkHttpClient httpclient = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            User user = new User();
            user.setUserName(mName);
            user.setPassword(mPassword);
            Gson gson = new Gson();
            //将对象转换为诶JSON格式字符串
            String jsonStr = gson.toJson(user);
            RequestBody body = RequestBody.create(JSON, jsonStr);
            Request request = new Request.Builder()
                    .url(SystemParameter.ip + "/user/login")
                    .post(body)
                    .build();
            try {
                Log.d("Request:", request.toString());
                Response response = httpclient.newCall(request).execute();
                String data = response.body().string();

                Log.d("Response:", data);
                return parseJSonWithGSON(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

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
                SharedPreferences.Editor editor = sp.edit();
                //保存用户名和密码
                editor.putString("user_name", mName);
                editor.putString("password", mPassword);
                //是否记住密码
                editor.putBoolean("remember_pass", cbRememberPass.isChecked());
                //是否自动登录
                editor.putBoolean("auto_login", autologin.isChecked());
                if (autologin.isChecked())
                    editor.putBoolean("direct_login", true);
                editor.apply();

                SystemParameter.user = result.getData();
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

