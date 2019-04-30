package com.mindyu.step.user.dao;

import com.mindyu.step.user.bean.Result;
import com.mindyu.step.user.bean.User;

import org.litepal.LitePal;

import java.util.List;

public class UserDao {

    private static UserDao userDao = null;

    public UserDao(){
        LitePal.getDatabase();
    }

    public static UserDao getInstance(){
        if(userDao == null){
            userDao = new UserDao();
        }
        return userDao;
    }

    /**
     * 用户注册
     * @param username
     * @param password
     * @return
     */
    public Result register(String username, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user.save()? new Result(true, "注册成功"):new Result(false, "注册失败");
    }

    public Result login(String username, String password) {
        List<User> userList = LitePal.where("username = ?", username)
                .limit(1).find(User.class);
        if (userList == null || userList.size() == 0) {
            register(username, password);      // 不存在当前用户就注册一个
            return new Result(false, "用户不存在，成功注册");
        } else if (password.equals(userList.get(0).getPassword())) {    // 验证成功
            return new Result(true,"登录成功");
        }
        return new Result(false, "用户密码不正确");
    }

}
