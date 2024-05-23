package com.ck.view.jira;

import com.intellij.openapi.components.ServiceManager;
import com.ck.config.JiraConfig;
import com.ck.settings.CommonSettings;

import javax.swing.*;

public class jiraView extends JDialog {
    private JPanel contentPane;
    private JTextField url;
    private JTextField textField1;
    private JTextField textField2;
    private JTable table;

    private JiraConfig config = ServiceManager.getService(CommonSettings.class).getState();

    public jiraView() {
        setContentPane(contentPane);
        setModal(true);
    }
}
