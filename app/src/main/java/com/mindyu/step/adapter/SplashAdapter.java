package com.mindyu.step.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mindyu.step.R;
import com.mindyu.step.util.CommonUtil;

//SplashAdapter适配器
public class SplashAdapter extends RecyclerView.Adapter<SplashAdapter.ViewHolder> {

    private int imgWidth;

    /*定义一个内部类ViewHolder继承RecyclerView.ViewHolder*/
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView item_bg;

        public ViewHolder(final View itemView) {
            super(itemView);
            item_bg = itemView.findViewById(R.id.item_bg);
        }

    }

    public SplashAdapter(Context context) {
        imgWidth = CommonUtil.getScreenWidth(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_splash, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

}