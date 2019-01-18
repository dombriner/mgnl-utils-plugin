package dialogs;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST;

public class ChooseValues extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel propertiesPanel;
    private List<String> values;

    public ChooseValues(List<String> values) {
        this.values = values;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        ChooseValues dialog = new ChooseValues(list);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        propertiesPanel = new JPanel();
        propertiesPanel.setLayout(new GridLayoutManager(values.size(), 1));
        int row = 0;
        for (String value : values) {
            GridConstraints valueConstraint = new GridConstraints();
            valueConstraint.setAnchor(ANCHOR_NORTHWEST);
            valueConstraint.setRow(row);
            propertiesPanel.add(createValuePanel(value), valueConstraint, row);
            row++;
        }
    }

    private Container createValuePanel(String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JCheckBox valueChosen = new JCheckBox();
        panel.add(valueChosen);

        JLabel valueLabel = new JLabel(value);
        panel.add(valueLabel);

        return panel;
    }
}