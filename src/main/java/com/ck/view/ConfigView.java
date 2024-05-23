package com.ck.view;

import com.intellij.openapi.components.ServiceManager;
import com.ck.config.JiraConfig;
import com.ck.settings.CommonSettings;

import javax.swing.*;

public class ConfigView {
    private JPanel contentPane;
    private JLabel url;
    private JTextField jiraUrl;
    private JTextField userName;
    private JPasswordField passWord;
    private JRadioButton radioButton1;

    private JiraConfig config = ServiceManager.getService(CommonSettings.class).getState();

    public ConfigView() {
        setContentPane(contentPane);
        jiraUrl.setText(config.getUrl());
        userName.setText(config.getUserName());
        passWord.setText(config.getPassword());
    }

    public JPanel getContentPane() {
        return contentPane;
    }

    public void setContentPane(JPanel contentPane) {
        this.contentPane = contentPane;
    }

    public JLabel getUrl() {
        return url;
    }

    public void setUrl(JLabel url) {
        this.url = url;
    }

    public JTextField getJiraUrl() {
        return jiraUrl;
    }

    public void setJiraUrl(JTextField jiraUrl) {
        this.jiraUrl = jiraUrl;
    }

    public JTextField getUserName() {
        return userName;
    }

    public void setUserName(JTextField userName) {
        this.userName = userName;
    }

    public JPasswordField getPassWord() {
        return passWord;
    }

    public void setPassWord(JPasswordField passWord) {
        this.passWord = passWord;
    }
}
