package com.ck.window;

import cn.hutool.core.util.StrUtil;
import com.intellij.ide.todo.TodoView;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.ck.config.JiraConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ck.settings.CommonSettings;
import com.ck.jira.JiraService;
import com.ck.vo.JiraVo;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.List;

public class JiraWindow implements ToolWindowFactory {

    private final JiraConfig jiraConfig = ServiceManager.getService(CommonSettings.class).getState();
    private final JiraService jiraService = new JiraService(jiraConfig);
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraWindow.class);

    public void createToolWindowContent1(Project project, ToolWindow toolWindow) {
        // 创建表格数据
        Object[][] data = {
                {"Bug001", "John", 25, "Male"},
                {"Bug002", "Jane", 30, "Female"},
                {"Bug003", "Doe", 40, "Male"}
        };

        // 创建表格列名
        String[] columns = {"Bug ID", "Name", "Age", "Gender"};

        // 创建默认表格模型
        DefaultTableModel model = new DefaultTableModel(data, columns);

        // 创建 JTable
        JTable table = new JTable(model);

        // 为 "Gender" 列创建一个下拉选择框
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        TableColumn genderColumn = table.getColumnModel().getColumn(3); // Gender 列索引
        genderColumn.setCellEditor(new DefaultCellEditor(comboBox));

        // 创建 JScrollPane 来容纳 JTable，使得可以滚动显示
        JScrollPane scrollPane = new JScrollPane(table);

        // 创建内容面板并添加 JScrollPane
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建内容并将其添加到工具窗口
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // 创建表格列名
        String[] columns = {"BugId", "Summer","解决方案","修复版本","影响范围"};
        List<JiraVo> issueList = jiraService.getIssueList();
        Object[][] data = new Object[issueList.size()][5];
        if (!issueList.isEmpty()) {
            for (int i = 0; i < issueList.size(); i++) {
                data[i][0] = issueList.get(i).getBugId();
                data[i][1] = issueList.get(i).getSummary();
            }
        }
        // 创建默认的空表格模型
        DefaultTableModel model = new DefaultTableModel(data,columns);
        // 创建 JTable
        JTable table = new JTable(model);
        // 为 "Gender" 列创建一个下拉选择框
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"无", "已解决", "Other"});
        TableColumn genderColumn = table.getColumnModel().getColumn(2); // Gender 列索引
        genderColumn.setCellEditor(new DefaultCellEditor(comboBox));
        // 创建 JScrollPane 来容纳 JTable，使得可以滚动显示
        JScrollPane scrollPane = new JScrollPane(table);

        // 创建刷新按钮
        JButton refreshButton = new JButton("Refresh");

        // 添加按钮点击事件监听器
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        // 创建内容面板并添加 JScrollPane 和按钮
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);
        table.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 获取点击的行和列索引
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 0) {
                    // 获取点击的单元格数据
                    String bugId = (String) table.getValueAt(row, col);
                    try {
                        Desktop dp = Desktop.getDesktop();
                        if (dp.isSupported(Desktop.Action.BROWSE)) {
                            dp.browse(URI.create(jiraConfig.getUrl()+"browse/"+bugId));
                        }
                    } catch (Exception exception) {
                        LOGGER.error("open url failed", exception);
                    }
                }else if (col == 2){

                }
            }
        });
        // 创建内容并将其添加到工具窗口
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, "jira", false);
        toolWindow.getContentManager().addContent(content);
    }
}
