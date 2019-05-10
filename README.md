# 运动管理系统

------

基于Android的运动管理系统



## 功能模块图

![健康管理系统](./img/健康管理系统.png)



## 已实现功能

- 计步功能
- 锻炼计划及提醒功能
- 用户注册登陆
- 用户信息显示
- 步数历史信息
- 自动登录记住密码



## todo

- [x] 历史数据下拉刷新
- [x] 所有用户数据存储服务器
- [x] 头像上传存储
- [x] 运动图片轮显
- [x] 引导页、欢迎页面动画显示 <https://blog.csdn.net/h176nhx7/article/details/81571957?tdsourcetag=s_pctim_aiomsg> 
- [x] 健康资讯模块（API）+ 下拉刷新 + 网页跳转
- [x] 周、月维度运动报告
- [x] 自定义标题栏、标题栏渐变色
- [x] Activity 重写，滑动返回
- [ ] 跑步、步行、骑行多模式适配
- [ ] GPS 轨迹绘制



## 使用技术

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



## 后端系统

- Spring Boot微服务架构 整合 Spring Data JPA
- MySQL 数据库



## 遇到过的问题

1. Android 用户详细信息Info存储时，因生日为 java.util.date 类型，Android 端使用 Gson 对结构信息序列化，后端采用 Jackson 解析。导致日期解析失败。
2. 头像图片的上传和显示优化
3. 使用 WebView 的时候，出现 net::ERR_UNKNOWN_URL_SCHEME 错误。
4. WebView 浏览页面的返回
5. 发现页预加载优化



## 解决方法

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





## 计步服务的实现

​	使用传感器有计步传感器（TYPE_STEP_COUNTER）、步数监测传感器（TYPE_STEP_DETECTOR）、加速度传感器（TYPE_ACCELEROMETER）。

​	在Android 4.4之后的版本中，Android系统在硬件中内置计步传感器。例如QQ运动、微信运动，支付宝运动等常用运动计步软件都是直接调用Android中的Sensor传感器服务，从而获取到每日的步数。但是并非所有 4.4 版本之后的手机都有这个传感器。该传感器会针对检测到的每个步伐触发一个事件，但提供的步数是从设备开机启动之后，该传感器检测的总步数，在每次设备重启后会清零，所以需要应用程序做数据的持久化。该传感器返回一个浮点类型的值，1000步即1000.0，以此类推。该传感器需要硬件支持，同时功耗较低，准确度高，所以一般计步服务都是直接使用的该传感器的数据。相信你可能遇到过比如在微信运动上提示该手机不支持计步服务，原因就是因为该手机并没有该传感器，然后App为了实现简单，以及降低功耗则直接不支持计步。

​	对于步数监测传感器（TYPE_STEP_DETECTOR），它的作用是在用户每迈出一步，此传感器就会触发一个事件。对于每个用户步伐，此传感器提供一个返回值为 1.0 的事件和一个指示此步伐发生时间的时间戳。当用户在行走时，会产生一个加速度上的变化，从而出触发此传感器事件的发生。但是该传感器每次步伐，并不会提供总步伐数，所以如果需要使用该传感器作为计步服务，则需要我们自己在App中进行步数的记录。

​	最后如果手机上没有以上两种传感器，依然可以加速度传感器来实现计步服务，加速度传感器又称之为G-sensor，返回X、Y、Z三轴的加速度值， 该数值包含地心引力的影响，单位为米每二次方秒。比如手机的抬腕亮屏功能就通过该传感器实现。

​	本系统实现将以上三种传感器结合使用，首先判断手机的系统版本，如果为4.4版本之前，则直接采用第三种加速度传感器的方案，如果为4.4及以后的版本，则优先使用加速度传感器、次之步数监测传感器、最后使用加速度传感器。这样可以实现市面上绝大部分手机均可以使用该系统软件的计步服务。

​	对于前两种方案实现较为简单。注册对应的传感器，然后添加传感器监听，重写传感器onSensorChanged方法。实现运动步数的记录。

```java
@Override
 public void onSensorChanged(SensorEvent event) {
     if (stepSensorType == Sensor.TYPE_STEP_COUNTER) {
         // 获取当前传感器返回的临时步数         int tempStep = (int) event.values[0];
         // 首次如果没有获取手机系统中已有的步数则获取一次系统中APP还未开始记步的步数         if (!hasRecord) {
             hasRecord = true;
             hasStepCount = tempStep;
         } else {
             // 获取APP打开到现在的总步数=本次系统回调的总步数-APP打开之前已有的步数             int thisStepCount = tempStep - hasStepCount;
             // 本次有效步数=（APP打开后所记录的总步数-上一次APP打开后所记录的总步数）             int thisStep = thisStepCount - previousStepCount;
             // 总步数=现有的步数+本次有效步数             CURRENT_STEP += (thisStep);
             // 记录最后一次APP打开到现在的总步数             previousStepCount = thisStepCount;
         }
     } else if (stepSensorType == Sensor.TYPE_STEP_DETECTOR) {
         if (event.values[0] == 1.0) {
             CURRENT_STEP++;
         }
     }
 }
```

​	采用计步传感器时，每次需要增量更新当前步数信息（CURRENT_STEP），通过记录上次更新时传感器中的数据，与本次传感器中的数据进行相减，同时更新上次传感器数据为本次传感器数据。最后步数更新之后进行UI操作的回调。而对于步数监测传感器则直接每次对步数信息（CURRENT_STEP）进行加一操作即可。

​	而对于加速度传感器则需要相应的算法实现。根据加速度传感器获得的三轴加速度的值，通过三轴加速度的值计算矢量值（坐标轴上三个方向上对应的长方体的体对角线长），当人体运动时，该值呈现的是一个规则的波形图，近似于正弦函数的形式，然后我们可以获取其波峰与波谷，那么出现一次则可以作为一个步伐。这个过程中存在一个问题，就是不同人群的运动数据不尽相同、运动状态不同（走路、跑步等），而且手机的放置位置也会影响到该数据，对于这个问题，解决方案是设置动态阈值，根据用户前几次的运动波峰和波谷的差值计算一个动态阈值，并且梯度化阈值，然后当用户下一次的运动波峰波谷的差值大于当前的阈值是就作为一个步伐。这个动态阈值同时可以作为当前的运动状态的判断。

