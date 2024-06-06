package com.ck.window;

import com.ck.cell.CustomCellEditor;
import com.ck.cell.CustomCellRenderer;
import com.ck.dot.TransitionDto;
import com.ck.listener.TableListener;
import com.intellij.openapi.components.ServiceManager;
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
import com.ck.vo.IssueVo;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiraWindow implements ToolWindowFactory {

    private final String[] columns = {"BugId", "Summer","Transaction","解决方案","修复版本","影响范围","解决"};

    private final JiraConfig jiraConfig = ServiceManager.getService(CommonSettings.class).getState();
    private final JiraService jiraService = new JiraService(jiraConfig);
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        List<IssueVo> issueList = jiraService.getIssueList();
        // 创建 JTable
        JTable table = new JTable(getModel(issueList));
        //解决方案下拉选
        repairPlan(table);
        //动态事务
        dynamicTransitions(issueList,table);
        // 创建 JScrollPane 来容纳 JTable，使得可以滚动显示
        JScrollPane scrollPane = new JScrollPane(table);
        // 创建刷新按钮
        JButton refreshButton = new JButton("Refresh");
        // 添加按钮点击事件监听器
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<IssueVo> vos = jiraService.getIssueList();
                // 更新表格模型
                table.setModel(getModel(vos));
                // 更新列的编辑器和渲染器
                repairPlan(table);
                dynamicTransitions(vos, table);
            }
        });
        // 创建内容面板并添加 JScrollPane 和按钮
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);
        table.addMouseListener(new TableListener(table,jiraConfig));
        // 创建内容并将其添加到工具窗口
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, "jira", false);
        toolWindow.getContentManager().addContent(content);
    }

    public DefaultTableModel getModel(List<IssueVo> issueList){
        Object[][] data = new Object[issueList.size()][7];
        if (!issueList.isEmpty()) {
            for (int i = 0; i < issueList.size(); i++) {
                data[i][0] = issueList.get(i).getBugId();
                data[i][1] = issueList.get(i).getSummary();
                data[i][2] = issueList.get(i).getDtos().get(0);
            }
        }
        // 创建默认的空表格模型
        return new DefaultTableModel(data,columns){
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return TransitionDto.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
    }

    /**
     * 修复方案
     */
    private void repairPlan(JTable table){
        // 为 "Gender" 列创建一个下拉选择框
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"无", "已解决", "不修复", "延迟修复", "重复的BUG", "无效的BUG"});
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(comboBox));
    }

    private void dynamicTransitions(List<IssueVo> issueList, JTable table) {
        table.getColumnModel().getColumn(2).setCellEditor(new CustomCellEditor(issueList));
        table.getColumnModel().getColumn(2).setCellRenderer(new CustomCellRenderer(issueList));
    }
}
