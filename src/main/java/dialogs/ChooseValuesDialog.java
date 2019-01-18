package dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;

import static com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST;

public class ChooseValuesDialog extends DialogWrapper {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel propertiesPanel;
    private Collection<String> values;
    private Collection<ValueRow> valueRows = new ArrayList<>();

    public ChooseValuesDialog(Collection<String> values, Project project) {
        super(project);
        this.values = values;
        //setContentPane(contentPane);
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

        /*
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        */

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public ArrayList<String> getChosenValues() {
        ArrayList<String> chosenVals = new ArrayList<>();
        for (ValueRow valueRow : valueRows) {
            if (valueRow.chosen())
                chosenVals.add(valueRow.getValue());
        }
        return chosenVals;
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
        ChooseValuesDialog dialog = new ChooseValuesDialog(list, null);
        dialog.pack();
        dialog.init();
        dialog.show();
        System.exit(0);
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
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

        return propertiesPanel;
    }

    private void createUIComponents() {
        createCenterPanel();
    }

    private Container createValuePanel(String value) {
        ValueRow valueRow = new ValueRow(value);
        valueRows.add(valueRow);
        return valueRow.getPanel();
    }

    private class ValueRow {
        JPanel panel;
        JCheckBox chosen;
        JLabel valueLabel;

        public ValueRow(String value) {
            panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            chosen = new JCheckBox();
            panel.add(chosen);

            valueLabel = new JLabel(value);
            panel.add(valueLabel);
        }

        public Container getPanel() {
            return panel;
        }

        public boolean chosen() {
            return chosen.isSelected();
        }

        public String getValue() {
            return valueLabel.getText();
        }
    }
}