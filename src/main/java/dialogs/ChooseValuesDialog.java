package dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
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
        init();
        createValuePanels();
        setModal(true);
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

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return propertiesPanel;
    }

    private void createUIComponents() {
        propertiesPanel = new JPanel();
    }

    private void createValuePanels() {
        if (propertiesPanel == null)
            createUIComponents();
        if (valueRows == null)
            valueRows = new ArrayList<>();
        GridLayoutManager layout = new GridLayoutManager(this.values.size(), 1);
        propertiesPanel.setLayout(layout);
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
