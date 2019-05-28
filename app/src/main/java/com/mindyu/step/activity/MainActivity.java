package com.mindyu.step.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hjm.bottomtabbar.BottomTabBar;
import com.mindyu.step.R;
import com.mindyu.step.fragment.HistoryFragment;
import com.mindyu.step.fragment.HotTopicFragment;
import com.mindyu.step.fragment.StepFragment;
import com.mindyu.step.fragment.UserFragment;

/**
 * 记步主页
 */
public class MainActivity extends AppCompatActivity {

    private BottomTabBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomBar =findViewById(R.id.bottom_bar);
        mBottomBar.init(getSupportFragmentManager(), 1080, 1920)

                .addTabItem("首页", R.mipmap.ic_common_tab_index_select, R.mipmap.ic_common_tab_index_unselect, StepFragment.class)
                .addTabItem("历史", R.mipmap.ic_common_tab_publish_select, R.mipmap.ic_common_tab_publish_unselect, HistoryFragment.class)
                .addTabItem("发现", R.mipmap.ic_common_tab_hot_select, R.mipmap.ic_common_tab_hot_unselect, HotTopicFragment.class)
                .addTabItem("我的", R.mipmap.ic_common_tab_user_select, R.mipmap.ic_common_tab_user_unselect, UserFragment.class)

                .setOnTabChangeListener(new BottomTabBar.OnTabChangeListener() {
                    @Override
                    public void onTabChange(int position, String name, View view) {
                        mBottomBar.setSpot(position, false);
                    }
                });
                // .setSpot(2, true);
    }

    public void setShowTabBar(boolean isShow){
        if (isShow){
            mBottomBar.getTabBar().setVisibility(View.VISIBLE);
        }else {
            mBottomBar.getTabBar().setVisibility(View.GONE);
        }
    }

    // 提示小红点
    public void setBottomBarSpot(int index){
        mBottomBar.setSpot(index, true);
    }

}
