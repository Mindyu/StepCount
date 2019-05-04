package com.mindyu.step.user.bean;

import java.io.Serializable;
import java.util.Date;

public class StepCount implements Serializable {

    Integer id;
    Integer userId;
    Date date;
    Integer stepCount;
    Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getStepCount() {
        return stepCount;
    }

    public void setStepCount(Integer stepCount) {
        this.stepCount = stepCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StepCount{" +
                "id=" + id +
                ", userId=" + userId +
                ", date=" + date +
                ", stepCount=" + stepCount +
                ", status=" + status +
                '}';
    }
}
