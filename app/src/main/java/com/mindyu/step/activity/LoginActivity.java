package com.mindyu.step.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mindyu.step.R;
import com.mindyu.step.parameter.SystemParameter;
import com.mindyu.step.user.bean.Result;
import com.mindyu.step.user.bean.User;
import com.mindyu.step.user.dao.UserDao;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private UserLoginTask mAuthTask = null;

    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox cbRememberPass;
    private CheckBox autologin;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initText();
    }

    private void initViews() {
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.action_sign_in || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        cbRememberPass = (CheckBox) findViewById(R.id.cbRememberPass);
        autologin = (CheckBox) findViewById(R.id.autologin);

        Button mNameSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mNameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void initText() {
        sp = getSharedPreferences("userInfo", 0);
        String name = sp.getString("USER_NAME", "");
        String pass = sp.getString("PASSWORD", "");

        boolean choseRemember = sp.getBoolean("cbRememberPass", false);
        boolean choseAutoLogin = sp.getBoolean("autologin", false);
        boolean directLogin = sp.getBoolean("directlogin", false);

        mUserNameView.setText(name);
        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
        if (choseRemember) {
            mPasswordView.setText(pass);
            cbRememberPass.setChecked(true);

            //如果上次登录选了自动登录，那进入登录页面也自动勾选自动登录
            if (choseAutoLogin) {
                autologin.setChecked(true);
                if (directLogin)
                    attemptLogin();
            }
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    // 验证用户信息、执行登录
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mUserNameView.setError(null);
        mPasswordView.setError(null);

        String email = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    // 密码至少6位
    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    // 显示异步登录进度条
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        mUserNameView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };
        int ADDRESS = 0;
    }

    // 用户登录的异步任务
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mPassword;
        private Result<User> result;
        private String errorMassage;

        UserLoginTask(String email, String password) {
            mName = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

//            UserDao userDao = UserDao.getInstance();
//            Result result = userDao.login(mName, mPassword);
//            errorMassage = result.getMessage();

            OkHttpClient httpclient = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            User user = new User();
            user.setUserName(mName);
            user.setPassword(mPassword);
            Gson gson=new Gson();
            //将对象转换为诶JSON格式字符串
            String jsonStr=gson.toJson(user);
            RequestBody body = RequestBody.create(JSON, jsonStr);
            Request request = new Request.Builder()
                    .url("http://188.131.213.13:9000/user/login")
                    .post(body)
                    .build();
            try {
                Log.d("Request:", request.toString());
                Response response = httpclient.newCall(request).execute();
                String data = response.body().string();

                Log.d("Response:", data);
                result = parseJSonWithGSON(data);
                errorMassage = result.getMessage();
                if (result.getCode() == 200){
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        private Result<User> parseJSonWithGSON(final String data) {
            Gson gson = new Gson();

            Result<User> result = gson.fromJson(data, new TypeToken<Result<User>>() {
            }.getType());

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success && result.getData()!=null) {
                SharedPreferences.Editor editor = sp.edit();
                //保存用户名和密码
                editor.putString("USER_NAME", mName);
                editor.putString("PASSWORD", mPassword);
                //是否记住密码
                editor.putBoolean("cbRememberPass", cbRememberPass.isChecked());
                //是否自动登录
                editor.putBoolean("autologin", autologin.isChecked());
                if (autologin.isChecked())
                    editor.putBoolean("directlogin", true);
                editor.commit();

                SystemParameter.user = result.getData();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", mName);
                intent.putExtras(bundle);
                startActivity(intent);
                LoginActivity.this.finish();    // 登录成功之后不能回退
            } else {
                mPasswordView.setError(errorMassage);
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

