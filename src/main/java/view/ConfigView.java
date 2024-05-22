package view;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

@Getter
@Setter
public class ConfigView {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    public ConfigView() {
        setContentPane(contentPane);

    }

    private void onOK() {
        // add your code here
    }

    private void onCancel() {
        // add your code here if necessary
    }

    public JPanel getContentPane() {
        return contentPane;
    }

    public void setContentPane(JPanel contentPane) {
        this.contentPane = contentPane;
    }
}
