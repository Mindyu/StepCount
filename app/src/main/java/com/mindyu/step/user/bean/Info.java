package com.mindyu.step.user.bean;

import java.io.Serializable;
import java.util.Date;

public class Info implements Serializable {
    private Integer id;//
    private Integer userId;//
    private Double height;//
    private Integer stepPlan;//
    private Double weight;//
    private Date birthday;//
    private String phone;//
    private String address;//
    private String extra;//
    private String intro;//
    private Integer status;//
    private Date createdAt;//
    private Date updatedAt;//

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

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getStepPlan() {
        return stepPlan;
    }

    public void setStepPlan(Integer stepPlan) {
        this.stepPlan = stepPlan;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Info{" +
                "id=" + id +
                ", userId=" + userId +
                ", height=" + height +
                ", stepPlan=" + stepPlan +
                ", weight=" + weight +
                ", birthday=" + birthday +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", extra='" + extra + '\'' +
                ", intro='" + intro + '\'' +
                '}';
    }
}
