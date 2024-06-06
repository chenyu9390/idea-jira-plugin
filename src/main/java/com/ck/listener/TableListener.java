package com.ck.listener;

import com.ck.config.JiraConfig;
import com.ck.dot.TransitionDto;
import com.ck.window.JiraWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.net.URI;

public class TableListener extends MouseInputAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableListener.class);

    private final JTable table;
    private final JiraConfig jiraConfig;
    public TableListener(JTable table,JiraConfig config) {
        this.table = table;
        this.jiraConfig = config;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // 获取点击的行和列索引
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        if (col == 0) {
            // 获取点击的单元格数据
            String bugId = (String) table.getValueAt(row, col);
            bugUrl(bugId);
        }else if (col == 2){
            TransitionDto dto = (TransitionDto)table.getValueAt(row, 2);
        }
    }

    private void bugUrl(String bugId){
        try {
            Desktop dp = Desktop.getDesktop();
            if (dp.isSupported(Desktop.Action.BROWSE)) {
                dp.browse(URI.create(jiraConfig.getUrl()+"browse/"+bugId));
            }
        } catch (Exception exception) {
            LOGGER.error("open url failed", exception);
        }
    }
}
