package com.ck.cell;

import com.ck.dot.TransitionDto;
import com.ck.vo.IssueVo;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final Map<Integer, JComboBox<TransitionDto>> comboBoxes = new HashMap<>();
    private JComboBox<TransitionDto> currentComboBox;
    public CustomCellEditor(List<IssueVo> issueList) {
        for (int i = 0; i < issueList.size(); i++) {
            comboBoxes.put(i, new JComboBox<>(issueList.get(i).getDtos().toArray(new TransitionDto[0])));
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
