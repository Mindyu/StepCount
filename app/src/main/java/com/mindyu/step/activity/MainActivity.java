package com.mindyu.step.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hjm.bottomtabbar.BottomTabBar;
import com.mindyu.step.R;
import com.mindyu.step.fragment.UserFragment;
import com.mindyu.step.fragment.StepFragment;
import com.mindyu.step.fragment.HotTopicFragment;
import com.mindyu.step.fragment.HistoryFragment;

/**
 * 记步主页
 */
public class MainActivity extends AppCompatActivity {

    private BottomTabBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//不显示程序的标题栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_main);
        mBottomBar = findViewById(R.id.bottom_bar);

        mBottomBar.init(getSupportFragmentManager(), 1080, 1920)
//                .setImgSize(50, 50)
//                .setFontSize(28)
//                .setTabPadding(10, 6, 4)
//                .setChangeColor(Color.parseColor("#2784E7"),Color.parseColor("#282828"))
                .addTabItem("首页", R.mipmap.ic_common_tab_index_select, R.mipmap.ic_common_tab_index_unselect, StepFragment.class)
                .addTabItem("历史", R.mipmap.ic_common_tab_publish_select, R.mipmap.ic_common_tab_publish_unselect, HistoryFragment.class)
                .addTabItem("发现", R.mipmap.ic_common_tab_hot_select, R.mipmap.ic_common_tab_hot_unselect, HotTopicFragment.class)
                .addTabItem("我的", R.mipmap.ic_common_tab_user_select, R.mipmap.ic_common_tab_user_unselect, UserFragment.class)
//                .isShowDivider(true)
//                .setDividerColor(Color.parseColor("#373737"))
//                .setTabBarBackgroundColor(Color.parseColor("#FFFFFF"))
                .setOnTabChangeListener(new BottomTabBar.OnTabChangeListener() {
                    @Override
                    public void onTabChange(int position, String name, View view) {
                        mBottomBar.setSpot(position, false);
                    }
                })
                .setSpot(2, true);
    }

    public void setShowTabBar(boolean isShow){
        if (isShow){
            mBottomBar.getTabBar().setVisibility(View.VISIBLE);
        }else {
            mBottomBar.getTabBar().setVisibility(View.GONE);
        }
    }

}
