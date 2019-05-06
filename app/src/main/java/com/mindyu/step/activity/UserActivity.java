package com.mindyu.step.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mindyu.step.R;

public class UserActivity extends AppCompatActivity {

    private Button quitBtn;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initViews();
        initEvents();
    }

    private void initViews(){
        quitBtn = findViewById(R.id.quit);
    }

    private void initEvents(){
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getSharedPreferences("userInfo", 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("direct_login", false);
                editor.commit();

                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                startActivity(intent);
                UserActivity.this.finish();    // 退出之后不能回退
            }
        });
    }
}
