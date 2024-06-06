package com.ck.window;

import cn.hutool.core.util.StrUtil;
import com.ck.dot.TransitionDto;
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
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.swing.text.StyleConstants.getComponent;

public class JiraWindow implements ToolWindowFactory {

    private final JiraConfig jiraConfig = ServiceManager.getService(CommonSettings.class).getState();
    private final JiraService jiraService = new JiraService(jiraConfig);
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraWindow.class);

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // 创建表格列名
        String[] columns = {"BugId", "Summer","Transaction","解决方案","修复版本","影响范围","解决"};
        List<JiraVo> issueList = jiraService.getIssueList();
        Object[][] data = new Object[issueList.size()][7];
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
        repairPlan(table);
        dynamicTransitions(issueList,table);
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
                CustomTableModel model = (CustomTableModel) table.getModel();
                StringBuilder rowData = new StringBuilder();
                for (int cl = 0; cl < model.getColumnCount(); cl++) {
                    rowData.append(model.getValueAt(row, cl)).append(" ");
                }
                System.out.println(rowData.toString());
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

    /**
     * 修复方案
     */
    private void repairPlan(JTable table){
        // 为 "Gender" 列创建一个下拉选择框
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"无", "已解决", "不修复", "延迟修复", "重复的BUG", "无效的BUG"});
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));
    }

    private void dynamicTransitions(List<JiraVo> issueList,JTable table) {
        table.getColumnModel().getColumn(3).setCellEditor(new CustomCellEditor(issueList));
        table.getColumnModel().getColumn(3).setCellRenderer(new CustomCellRenderer(issueList));
    }
}

class CustomTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Name", "Options"};
    private final Object[][] data = {
            {"Item 1", "Option 1A"},
            {"Item 2", "Option 2A"},
            {"Item 3", "Option 3A"},
            {"Item 4", "Option 4A"},
    };

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1; // Only the "Options" column is editable
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data[rowIndex][columnIndex] = aValue;
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}

class CustomCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final Map<Integer, JComboBox<String>> comboBoxes = new HashMap<>();
    private JComboBox<String> currentComboBox;
    public CustomCellEditor(List<JiraVo> issueList) {
        for (int i = 0; i < issueList.size(); i++) {
            comboBoxes.put(i, new JComboBox<>(issueList.get(i).getDtos().stream().map(TransitionDto::getName).toArray(String[]::new)));
        }
    }

    @Override
    public Object getCellEditorValue() {
        return currentComboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentComboBox = comboBoxes.get(row);
        currentComboBox.setSelectedItem(value);
        return currentComboBox;
    }
}
class CustomCellRenderer extends JComboBox<String> implements TableCellRenderer {
    private List<JiraVo> issueList;
    public CustomCellRenderer(List<JiraVo> issueList) {
        this.issueList = issueList;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        removeAllItems();
        for (int i = 0; i < issueList.size(); i++) {
            issueList.get(i).getDtos().stream().map(TransitionDto::getId).forEach(this::addItem);
        }
        setSelectedItem(value);
        return this;
    }
}
