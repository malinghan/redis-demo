package com.mlh.domain;

import java.io.Serializable;

/**
 * @author: linghan.ma
 * @DATE: 2018/12/12
 * @description: 序列化的对象,用于测试redis的序列化
 */

public class User implements Serializable {

    private static final long serialVersionUID = 4220515347228129741L;

    private Integer id;
    private String username;
    private Integer age;

    public User(Integer id, String username, Integer age) {
        this.id = id;
        this.username = username;
        this.age = age;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
