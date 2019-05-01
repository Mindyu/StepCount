package com.mindyu.step.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mindyu.step.R;
import com.mindyu.step.adapter.CommonAdapter;
import com.mindyu.step.adapter.CommonViewHolder;
import com.mindyu.step.step.bean.StepCountData;
import com.mindyu.step.step.service.StepService;
import com.orhanobut.logger.Logger;

import org.litepal.LitePal;

import java.util.List;


/**
 * 历史记录页面
 */
public class HistoryFragment extends Fragment {

    private ListView history_lv;
    private SwipeRefreshLayout refresh_layout;
    private CommonAdapter<StepCountData> commonAdapter;

    private void initViews(View view) {
        history_lv = view.findViewById(R.id.lv);
        refresh_layout = view.findViewById(R.id.swiperefreshlayout);
    }

    public HistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initViews(view);
        initData();
        return view;
    }

    private void initData() {
        setEmptyView(history_lv);
        List<StepCountData> stepCountData = LitePal.order("today desc").find(StepCountData.class);
        Logger.d("stepDatas=" + stepCountData);

        commonAdapter = new CommonAdapter<StepCountData>(this.getContext(), stepCountData, R.layout.item) {
            @Override
            protected void convertView(View item, StepCountData stepCountData) {
                TextView tv_date = CommonViewHolder.get(item, R.id.tv_date);
                TextView tv_step = CommonViewHolder.get(item, R.id.tv_step);
                tv_date.setText(stepCountData.getToday());
                tv_step.setText(stepCountData.getStep() + "步");
            }
        };
        history_lv.setAdapter(commonAdapter);

        refresh_layout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
        // 给 swipeRefreshLayout 绑定刷新监听
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //设置2秒的时间来执行以下事件
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        List<StepCountData> stepCountDataList = commonAdapter.getDatas();
                        stepCountDataList.get(0).setStep(""+StepService.CURRENT_STEP);
                        commonAdapter.notifyDataSetChanged();
                        refresh_layout.setRefreshing(false);    // 显示或隐藏刷新进度条
                    }
                }, 1000);
            }
        });

    }

    protected <T extends View> T setEmptyView(ListView listView) {
        TextView emptyView = new TextView(this.getContext());
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无数据！");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
        return (T) emptyView;
    }
}