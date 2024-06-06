package com.ck.cell;

import com.ck.dot.TransitionDto;
import com.ck.vo.IssueVo;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class CustomCellRenderer extends JComboBox<TransitionDto> implements TableCellRenderer {

    private List<IssueVo> issueList;
    public CustomCellRenderer(List<IssueVo> issueList) {
        this.issueList = issueList;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        removeAllItems();
        for (int i = 0; i < issueList.size(); i++) {
            issueList.get(i).getDtos().forEach(this::addItem);
        }
        setSelectedItem(value);
        return this;
    }
}
