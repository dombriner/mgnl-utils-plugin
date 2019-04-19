package dialogs;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

import static base.Constants.PLUGIN_NAME;
import static com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST;
import static org.apache.commons.lang.StringUtils.isEmpty;

public class ChooseValuesDialog extends DialogWrapper {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel propertiesPanel;
    private Collection<String> values;
    private Collection<ValueRow> valueRows = new ArrayList<>();
    private boolean save = false;
    private String savePrefix = PLUGIN_NAME;
    private PropertiesComponent propSaver = PropertiesComponent.getInstance();

    public ChooseValuesDialog(Collection<String> values, Project project, boolean save, @Nullable String savePrefix) {
        super(project);
        this.values = values;
        this.save = save;
        if (!isEmpty(savePrefix))
            this.savePrefix += "-" + savePrefix;
        init();
        createValuePanels();
        setModal(true);
    }


    public ChooseValuesDialog(Collection<String> values, Project project) {
        this(values, project, false, null);
    }

    public ArrayList<String> getChosenValues() {
        ArrayList<String> chosenVals = new ArrayList<>();
        for (ValueRow valueRow : valueRows) {
            if (valueRow.chosen())
                chosenVals.add(valueRow.getValue());
        }
        return chosenVals;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        if (save && isOKActionEnabled())
            saveChoices();
    }

    private void saveChoices() {
        if (save) {
            for (ValueRow valueRow : valueRows) {
                propSaver.setValue(getSavePropertyName(valueRow.getValue()), valueRow.chosen());
            }
        }
    }

    private String getSavePropertyName(String value) {
        return savePrefix + "-" + value;
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
        boolean chosen = save && propSaver.getBoolean(getSavePropertyName(value), false);
        ValueRow valueRow = new ValueRow(value, chosen);
        valueRows.add(valueRow);
        return valueRow.getPanel();
    }

    private class ValueRow {
        JPanel panel;
        JCheckBox chosen;
        JLabel valueLabel;

        public ValueRow(String value, boolean ticked) {
            panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            chosen = new JCheckBox();
            chosen.setSelected(ticked);
            panel.add(chosen);

            valueLabel = new JLabel(value);
            panel.add(valueLabel);
        }

        public ValueRow(String value) {
            this(value, false);
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
