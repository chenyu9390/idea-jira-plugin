package com.ck.config;

/**
 * @author kangChen
 * @Classname JriaConfig
 * @Description TODO
 * @Version 1.0.0
 * @Date 2024/5/22 23:32
 */
public class JiraConfig {

    private String url;

    private String userName;

    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
