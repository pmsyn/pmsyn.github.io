package com.shiro.pojo;

import java.util.List;
import java.util.Map;

/**
 * @author:pms
 * @createtime:2018/6/19-15:57
 * @qq 718195578
 * @since 1.0
 */
public class User {
    private String id;
    private String account;
    private String password;
    private String userName;
    private List<Map<String, Object>> role;
    private List<Map<String, Object>> permission;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Map<String, Object>> getRole() {
        return role;
    }

    public void setRole(List<Map<String, Object>> role) {
        this.role = role;
    }

    public List<Map<String, Object>> getPermission() {
        return permission;
    }

    public void setPermission(List<Map<String, Object>> permission) {
        this.permission = permission;
    }
}
