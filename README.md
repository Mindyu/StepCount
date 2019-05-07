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
- [ ] 健康资讯模块（API）
- [ ] GPS轨迹绘制
- [ ] 周、月维度运动报告
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



后端系统

- Spring Boot微服务架构 整合 Spring Data JPA
- MySQL 数据库



遇到过的问题

1. Android 用户详细信息Info存储时，因生日为 java.util.date 类型，Android 端使用 Gson 对结构信息序列化，后端采用 Jackson 解析。导致日期解析失败。
2. 头像图片的上传和显示优化





解决方法

1. 构建 Gson 对象时，设置日期格式化的样式即可

```
Gson gson=new GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create();
```

2. 减少服务器的请求次数和图片压缩两种方案