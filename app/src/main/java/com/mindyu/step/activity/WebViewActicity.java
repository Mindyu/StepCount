package com.mindyu.step.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mindyu.step.R;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

public class WebViewActicity extends SwipeBackActivity {

    private CommonTitleBar topbar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        initView();
        initData();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String url = bundle.getString("url");
            webView.loadUrl(url);
        } else {
            this.finish();
        }
    }

    private void initView() {
        webView = findViewById(R.id.web_view);
        topbar = findViewById(R.id.topbar);
        topbar.setBackgroundResource(R.drawable.shape_gradient);
    }

    private void initData() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return true;
                }return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {  // goBack()表示返回WebView的上一页面
            if (webView.canGoBack()){
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
