package com.mindyu.step.parameter;

import com.mindyu.step.user.bean.Info;
import com.mindyu.step.user.bean.User;

public class SystemParameter {

    public static User user;
    public static Info info;

    // true: 使用本地sqlite存储，  false：使用服务端mysql存储
    public final static boolean use_local_storage = false;
    public final static String ip = "http://188.131.213.13:9000";
}
