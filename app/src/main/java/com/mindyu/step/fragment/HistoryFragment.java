package com.mindyu.step.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mindyu.step.R;
import com.mindyu.step.adapter.CommonAdapter;
import com.mindyu.step.adapter.CommonViewHolder;
import com.mindyu.step.step.bean.StepCountData;
import com.orhanobut.logger.Logger;

import org.litepal.LitePal;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private ListView lv;
//    private SwipeRefreshLayout swipeRefresh;

    private void assignViews(View view) {
        lv = view.findViewById(R.id.lv);
//        swipeRefresh = view.findViewById(R.id.swipe_refresh);
    }

    public HistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        assignViews(view);
        initData();
        return view;
    }

    private void initData() {
        setEmptyView(lv);
        List<StepCountData> stepCountData = LitePal.findAll(StepCountData.class);
        Logger.d("stepDatas=" + stepCountData);
        lv.setAdapter(new CommonAdapter<StepCountData>(this.getContext(), stepCountData, R.layout.item) {
            @Override
            protected void convertView(View item, StepCountData stepCountData) {
                TextView tv_date = CommonViewHolder.get(item, R.id.tv_date);
                TextView tv_step = CommonViewHolder.get(item, R.id.tv_step);
                tv_date.setText(stepCountData.getToday());
                tv_step.setText(stepCountData.getStep() + "步");
            }
        });

//        // 下拉刷新
//        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
//        swipeRefresh.setOnRefreshListener(this); // 设置刷新监听

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

//    @Override
//    public void onRefresh() {
//        swipeRefresh.postDelayed(new Runnable() { // 发送延迟消息到消息队列
//            @Override
//            public void run() {
//                swipeRefresh.setRefreshing(false); // 是否显示刷新进度;false:不显示
//            }
//        }, 3000);
//    }
}
