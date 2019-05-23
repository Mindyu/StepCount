package com.mindyu.step.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mindyu.step.R;
import com.mindyu.step.activity.WebViewActicity;
import com.mindyu.step.adapter.CommonAdapter;
import com.mindyu.step.adapter.CommonViewHolder;
import com.mindyu.step.util.SharedPreferencesUtils;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 发现页面
 */
public class HotTopicFragment extends Fragment {

    private final static String TAG = "HotTopicFragment";

    private ListView news_lv;
    private SwipeRefreshLayout refresh_layout;
    private CommonAdapter commonAdapter;
    private CommonTitleBar topbar;
    private SharedPreferencesUtils sp;

    public HotTopicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic, container, false);

        initView(view);
        initEvent(view);
        initData();
        return view;
    }

    private void initView(View view) {
        news_lv = view.findViewById(R.id.list_view);
        refresh_layout = view.findViewById(R.id.swiperefreshlayout);
        topbar = view.findViewById(R.id.topbar);
        topbar.getLeftTextView().setText("发现");
        topbar.setBackgroundResource(R.drawable.shape_gradient);
    }

    private void initEvent(View view) {
        topbar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_RIGHT_BUTTON) {
                    Toast.makeText(getContext(), "搜索", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class News implements Comparable {
        String title;
        String time;
        String src;
        String category;
        String pic;
        String weburl;

        @Override
        public String toString() {
            return "News{" +
                    "title='" + title + '\'' +
                    ", time='" + time + '\'' +
                    ", src='" + src + '\'' +
                    ", category='" + category + '\'' +
                    ", pic='" + pic + '\'' +
                    ", weburl='" + weburl + '\'' +
                    '}';
        }

        @Override
        public int compareTo(@NonNull Object o) {
            return ((News) o).time.compareTo(time);
        }
    }

    private void initData() {
        setEmptyView(news_lv);

        List<News> result;
        // 先从本地缓存中获取
        sp = new SharedPreferencesUtils(getContext());
        String data = sp.getParam("newsList", "").toString();
        result = parseComplexJsonFromJisuStr(data);

        // 异步获取新闻信息
        new NewsTask().execute();

        commonAdapter = new CommonAdapter<News>(this.getContext(), result, R.layout.item_news) {
            @Override
            protected void convertView(View item, final News news) {
                TextView title_tv = CommonViewHolder.get(item, R.id.title_tv);
                TextView source_tv = CommonViewHolder.get(item, R.id.source_tv);
                TextView category_tv = CommonViewHolder.get(item, R.id.category_tv);
                TextView publish_date_tv = CommonViewHolder.get(item, R.id.publish_date_tv);
                title_tv.setText(news.title);
                source_tv.setText(news.src);
                category_tv.setText(news.category);
                publish_date_tv.setText(news.time);
                title_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 跳转新闻详情页
                        // Toast.makeText(getContext(), news.link, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), WebViewActicity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("url", news.weburl);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        };
        news_lv.setAdapter(commonAdapter);

        refresh_layout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
        // 给 swipeRefreshLayout 绑定刷新监听
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new NewsTask().execute();
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

    public class NewsTask extends AsyncTask<Void, Void, List<News>> {

        @Override
        protected List<News> doInBackground(Void... voids) {
            OkHttpClient okHttpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    // .url("https://www.apiopen.top/journalismApi")
                    .url("https://api.jisuapi.com/news/get?channel=%E5%81%A5%E5%BA%B7&start=0&num=20&appkey=f3593fb14811078e")
                    .build();
            Call call = okHttpClient.newCall(request);
            try {
                Response response = call.execute();
                if (response.body() == null) {
                    Log.d(TAG, "onResponse: 获取新闻数据失败");
                    return null;
                }
                String data = response.body().string();
                Log.d(TAG, "onResponse: " + data);

                String originData = sp.getParam("newsList", "").toString();
                if (!originData.equals(data)) {
                    sp.setParam("newsList", data);
                }

                List<News> result = parseComplexJsonFromJisuStr(data);
                if (result != null) {
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<News> result) {
            refresh_layout.setRefreshing(false);    // 显示或隐藏刷新进度条
            if (result == null) {
                Toast.makeText(getContext(), "获取新闻数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
            List<News> origin = commonAdapter.getDatas();
            if (origin != null && origin.size() == result.size() && origin.size() > 0 && origin.get(0).time.equals(result.get(0).time)) {
                Log.d(TAG, "onPostExecute: 无新记录");
                return;
            }
            commonAdapter.setDatas(result);
            commonAdapter.notifyDataSetChanged();
        }
    }

    /*private List<News> parseComplexJsonStr(String jsonStr) {
        List<News> newsList = new ArrayList<News>();
        if (jsonStr == null || "".equals(jsonStr))
            return newsList;
        //最外层
        JsonObject jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
        //需要遍历的数组
        jsonObject = jsonObject.getAsJsonObject("result");
        if (jsonObject == null) return newsList;
        String[] classify = {"tech", "auto", "money", "sports", "toutiao", "dy", "war", "ent"};
        for (String str : classify) {
            JsonArray jsonArray = jsonObject.getAsJsonArray(str);
            if (jsonArray == null || jsonArray.size() == 0) continue;
            //循环遍历数组
            for (JsonElement info : jsonArray) {
                News news = new Gson().fromJson(info, new TypeToken<News>() {
                }.getType());
                if (news.title == null) continue;
                JsonArray picArray = info.getAsJsonObject().getAsJsonArray("picInfo");
                if (picArray != null && picArray.size() > 0)
                    news.pic_url = picArray.get(0).getAsJsonObject().get("url").toString();
                newsList.add(news);
            }
        }
        Collections.sort(newsList);     // 按照发布时间倒排
        return newsList;
    }*/


    private List<News> parseComplexJsonFromJisuStr(String jsonStr) {
        List<News> newsList = new ArrayList<News>();
        if (jsonStr == null || "".equals(jsonStr))
            return newsList;

        //拿到最外层对象
        JsonObject jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
        jsonObject = jsonObject.getAsJsonObject("result");

        if (jsonObject==null || !jsonObject.has("list"))
            return newsList;
        //需要遍历的数组
        JsonArray jsonArray = jsonObject.getAsJsonArray("list");
        for (JsonElement info : jsonArray) {
            News news = new Gson().fromJson(info, new TypeToken<News>() {
            }.getType());
            if (news.title == null) continue;
            newsList.add(news);
        }

        Collections.sort(newsList);     // 按照发布时间倒排
        return newsList;
    }
}
