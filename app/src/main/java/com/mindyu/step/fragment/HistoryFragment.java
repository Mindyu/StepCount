package com.mindyu.step.fragment;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mindyu.step.R;
import com.mindyu.step.adapter.CommonAdapter;
import com.mindyu.step.adapter.CommonViewHolder;
import com.mindyu.step.parameter.SystemParameter;
import com.mindyu.step.step.bean.StepCountData;
import com.mindyu.step.step.service.StepService;
import com.mindyu.step.user.bean.Info;
import com.mindyu.step.user.bean.StepCount;
import com.orhanobut.logger.Logger;

import org.litepal.LitePal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 历史记录页面
 */
public class HistoryFragment extends Fragment {

    private final static String TAG = "HistoryFragment";

    private ListView history_lv;
    private SwipeRefreshLayout refresh_layout;
    private CommonAdapter commonAdapter;

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
        if (SystemParameter.use_local_storage) {
            initDataFromSqllite();
        } else {
            initDataFromMysql();
        }
        return view;
    }

    private void initDataFromSqllite() {
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
                        stepCountDataList.get(0).setStep("" + StepService.CURRENT_STEP);
                        commonAdapter.notifyDataSetChanged();
                        refresh_layout.setRefreshing(false);    // 显示或隐藏刷新进度条
                    }
                }, 1000);
            }
        });
    }

    private void initDataFromMysql() {
        setEmptyView(history_lv);
        if (SystemParameter.user != null)
            new UserStepCountTask().execute(SystemParameter.user.getId());

        commonAdapter = new CommonAdapter<StepCount>(this.getContext(), new ArrayList<StepCount>(), R.layout.item) {
            @Override
            protected void convertView(View item, StepCount stepCount) {
                TextView tv_date = CommonViewHolder.get(item, R.id.tv_date);
                TextView tv_step = CommonViewHolder.get(item, R.id.tv_step);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(stepCount.getDate());
                tv_date.setText(date);
                tv_step.setText(stepCount.getStepCount() + "步");
            }
        };
        history_lv.setAdapter(commonAdapter);

        refresh_layout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
        // 给 swipeRefreshLayout 绑定刷新监听
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //设置1秒的时间来执行以下事件
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        List<StepCount> stepCountList = commonAdapter.getDatas();
                        if (stepCountList != null && stepCountList.size() > 0 &&
                                sdf.format(stepCountList.get(0).getDate()).equals(StepService.CURRENT_DATE)) {
                            stepCountList.get(0).setStepCount(StepService.CURRENT_STEP);
                        } else {
                            StepCount stepCount = new StepCount();
                            stepCount.setDate(new Date());
                            stepCount.setStepCount(StepService.CURRENT_STEP);
                            if (stepCountList == null) stepCountList = new ArrayList<>();
                            stepCountList.add(stepCount);
                            commonAdapter.setDatas(stepCountList);
                        }
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

    @SuppressLint("StaticFieldLeak")
    public class UserStepCountTask extends AsyncTask<Integer, Void, List<StepCount>> {

        @Override
        protected List<StepCount> doInBackground(Integer... userId) {
            OkHttpClient okHttpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(SystemParameter.ip + "/count/" + userId[0])
                    .build();
            Log.d(TAG, "request url: " + request);
            Call call = okHttpClient.newCall(request);
            try {
                Response response = call.execute();
                if (response.body() == null) {
                    Log.d(TAG, "onResponse: 获取用户步数信息失败");
                    return null;
                }
                String data = response.body().string();
                Log.d(TAG, "onResponse: " + data);

                Gson gson = new Gson();
                List<StepCount> result = gson.fromJson(data, new TypeToken<List<StepCount>>() {
                }.getType());

                if (result != null) {
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<StepCount> result) {
            if (result == null)
                Toast.makeText(getContext(), "获取步数数据失败", Toast.LENGTH_SHORT).show();
            commonAdapter.setDatas(result);
            commonAdapter.notifyDataSetChanged();
        }
    }
}