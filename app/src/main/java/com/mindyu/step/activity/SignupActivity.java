package com.mindyu.step.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mindyu.step.R;
import com.mindyu.step.parameter.SystemParameter;
import com.mindyu.step.user.bean.Result;
import com.mindyu.step.user.bean.User;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends SwipeBackActivity {
    private static final String TAG = "SignupActivity";

    EditText email_edit;
    EditText password_edit;
    EditText re_password_edit;
    Button sign_btn;
    TextView login_tv;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initView();
        initEvent();
    }

    private void initView() {
        email_edit = findViewById(R.id.input_username);
        password_edit = findViewById(R.id.input_password);
        re_password_edit = findViewById(R.id.input_reEnterPassword);
        sign_btn = findViewById(R.id.btn_signup);
        login_tv = findViewById(R.id.link_login);
    }

    private void initEvent() {
        sign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        sign_btn.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String username = email_edit.getText().toString();
        String password = password_edit.getText().toString();

        new UserSignUpTask(username, password).execute((Void) null);
    }

    private void dismissDialog() {
        progressDialog.dismiss();
        sign_btn.setEnabled(true);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();
        sign_btn.setEnabled(true);
    }

    public void onSignupSuccess() {
        sign_btn.setEnabled(true);
        Intent intent = new Intent();
        intent.putExtra("username", email_edit.getText().toString());
        intent.putExtra("password", password_edit.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    public boolean validate() {
        boolean valid = true;

        String username = email_edit.getText().toString();
        String password = password_edit.getText().toString();
        String reEnterPassword = re_password_edit.getText().toString();

        if (username.isEmpty() || username.length() < 4) {
            email_edit.setError("at least 4 characters");
            valid = false;
        } else {
            email_edit.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            password_edit.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password_edit.setError(null);
        }
        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            re_password_edit.setError("Password Do not match");
            valid = false;
        } else {
            re_password_edit.setError(null);
        }
        return valid;
    }

    // 用户登录的异步任务
    public class UserSignUpTask extends AsyncTask<Void, Void, Result> {

        private final String mName;
        private final String mPassword;

        UserSignUpTask(String email, String password) {
            mName = email;
            mPassword = password;
        }

        @Override
        protected Result doInBackground(Void... params) {
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
                    .url(SystemParameter.ip + "/user/")
                    .post(body)
                    .build();
            try {
                Log.d("Request:", request.toString());
                Response response = httpclient.newCall(request).execute();
                if (response.body() == null) return null;
                String data = response.body().string();

                Log.d("Response:", data);
                Result result = gson.fromJson(data, new TypeToken<Result>() {
                }.getType());
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Result result) {
            dismissDialog();
            if (result != null && result.getCode() == 200) {
                onSignupSuccess();
                return;
            }
            Toast.makeText(getBaseContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled() {
            dismissDialog();
        }
    }
}
