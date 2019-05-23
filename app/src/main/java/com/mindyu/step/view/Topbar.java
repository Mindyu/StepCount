package com.mindyu.step.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mindyu.step.R;

/* 自定义控件 */
public class Topbar extends RelativeLayout {

    //自定义控件中的组件
    private TextView tvTitle;
    private ImageView rightImageView;

    //各组件的属性
    private String title;
    private float titleTextSize;
    private int titleTextColor;


    private Drawable rightbackground;

    private LayoutParams rightParam,titleParam;

    private topbarClickListener listener;

    public interface topbarClickListener{
        void rightClick();
    }

    //接口回调机制，实现组件的事件
    public void setTopbarClickListener(topbarClickListener listener){
        this.listener=listener;
    }

    public Topbar(Context context, AttributeSet attrs) {
        super(context, attrs);                                     //引用自定义的属性name
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Topbar);

        //获取到自定义的各个属性
        title=typedArray.getString(R.styleable.Topbar_title);
        titleTextColor=typedArray.getColor(R.styleable.Topbar_titleTextColor,0);
        titleTextSize=typedArray.getDimension(R.styleable.Topbar_titleTextSize,1);


        rightbackground = typedArray.getDrawable(R.styleable.Topbar_rightBackground);

        //调用结束后务必调用recycle()方法，否则这次的设定会对下次的使用造成影响
        typedArray.recycle();

        //定义各个组件
        tvTitle =new TextView(context);
        rightImageView = new ImageView(context);

        //为组件添加属性
        rightImageView.setBackground(rightbackground);

        tvTitle.setText(title);
        tvTitle.setTextColor(titleTextColor);
        tvTitle.setTextSize(titleTextSize);

        setBackgroundColor(getResources().getColor(R.color.white));

        //布局参数样式
        rightParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        rightParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,TRUE);

        addView(rightImageView,rightParam);

        titleParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        titleParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT,TRUE);

        addView(tvTitle,titleParam);

        //组件的点击事件
        rightImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.rightClick();
            }
        });

    }

    //其他事件的完善
    public void setRightImageViewVisable(boolean flag){
        if (flag){
            rightImageView.setVisibility(VISIBLE);
        }else {
            rightImageView.setVisibility(GONE);
        }
    }

}
