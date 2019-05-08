# 运动管理系统

------

基于Android的运动管理系统



功能模块图：

![健康管理系统](./img/健康管理系统.png)



已实现功能：
- 计步功能
- 锻炼计划及提醒功能
- 用户注册登陆
- 用户信息显示
- 步数历史信息
- 自动登录记住密码



todo
- [x] 历史数据下拉刷新
- [x] 所有用户数据存储服务器
- [x] 头像上传存储
- [x] 运动图片轮显
- [x] 引导页、欢迎页面动画显示 <https://blog.csdn.net/h176nhx7/article/details/81571957?tdsourcetag=s_pctim_aiomsg> 
- [x] 健康资讯模块（API）+ 下拉刷新 + 网页跳转
- [ ] GPS轨迹绘制
- [x] 周、月维度运动报告
- [x] 自定义标题栏、标题栏渐变色
- [x] Activity 重写，滑动返回
- [ ] 跑步、步行、骑行多模式适配



使用技术

- SQLite数据库
- Litepal框架
- SharePerference
- 安卓网络编程
- 异步请求 AsyncTask
- RxJava 
- 头像图片选择
- Material Design 设计规范
- RecyclerView 
- DateTimePicker 封装
- 自定义 FitImageView ，自适应宽度
- StepArcView 步数圆弧显示
- Activity 传值 与 回传（相机、相册）
- 自定义发现页 item_view
- 网络复杂 json 解析 JsonParser
- 新闻详情页 webView 的使用



后端系统

- Spring Boot微服务架构 整合 Spring Data JPA
- MySQL 数据库



遇到过的问题

1. Android 用户详细信息Info存储时，因生日为 java.util.date 类型，Android 端使用 Gson 对结构信息序列化，后端采用 Jackson 解析。导致日期解析失败。
2. 头像图片的上传和显示优化
3. 使用 WebView 的时候，出现net::ERR_UNKNOWN_URL_SCHEME错误。
4. WebView 浏览页面的返回
5. 发现页预加载优化



解决方法

1. 构建 Gson 对象时，设置日期格式化的样式即可

```
	Gson gson=new GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create();
```

2. 减少服务器的请求次数和图片压缩两种方案

3. 原因是自定协议的URL，WebView无法识别。 解决办法：拦截所有非 http、https 的请求。

```java
	webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return true;
                }return false;
            }
        });
```

4. 重写 返回事件，可以返回上页则返回上一页， 而实际返回事件由 SwipeBackActivity 处理。

```java
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
```

