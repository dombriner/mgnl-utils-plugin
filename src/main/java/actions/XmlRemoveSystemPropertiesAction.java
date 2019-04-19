package actions;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import dialogs.ChooseValuesDialog;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import util.MgnlUtil;

import java.util.*;

import static util.FileUtil.isFileType;

public class XmlRemoveSystemPropertiesAction extends BaseIntentionAction {

    private static final Logger LOG = Logger.getInstance(XmlRemoveSystemPropertiesAction.class);
    private final String message = "Remove system properties...";
    private String familyName = "Mgnl Utils";
    private Project project;


    @NotNull
    @Override
    public String getText() {
        return message;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return familyName;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return isFileType(file, "xml");
    }

    /**
     * Collects all the system properties used in the XML file. The user can then choose which system properties should
     * be removed in a dialog. The XML tags containing chosen properties will be removed from the file.
     */
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        this.project = project;
        ApplicationManager.getApplication().invokeLater(
                () -> {
                    ArrayList<XmlTag> propertyTags = new ArrayList<>();
                    // Sort list system properties ignoring case
                    Set<String> systemPropertyNames = new TreeSet<>(String::compareToIgnoreCase);

                    collectSystemPropertyTags(file, propertyTags, systemPropertyNames);
                    ChooseValuesDialog choosePropertiesDialog = new ChooseValuesDialog(systemPropertyNames, project, false, "remove-xml-system-property");
                    choosePropertiesDialog.pack();

                    // Return iff dialog was cancelled
                    if (!choosePropertiesDialog.showAndGet())
                        return;


                    ArrayList<String> chosenProperties = choosePropertiesDialog.getChosenValues();

                    removeChosenTags(chosenProperties, propertyTags);
                });
    }

    /**
     * Puts all the system properties' tags of the file into propertyTags and the corresponding "sv:name" attribute
     * value into systemPropertyNames.
     *
     * @param file                PsiFile to be queried for system properties
     * @param propertyTags        Collection to put the property Tags in there
     * @param systemPropertyNames Collection to put the system properties in
     */
    private void collectSystemPropertyTags(PsiFile
                                                   file, Collection<XmlTag> propertyTags, Collection<String> systemPropertyNames) {
        PsiRecursiveElementWalkingVisitor fileVisitor = new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(final PsiElement element) {
                super.visitElement(element);
                if (element instanceof XmlTag) {
                    XmlTag tag = (XmlTag) element;
                    if (tag.getName().equals("sv:property")) {
                        if (tag.getAttribute("sv:name") instanceof XmlAttribute) {
                            try {
                                XmlAttribute name = tag.getAttribute("sv:name");
                                if (MgnlUtil.isSystemProperty(Objects.requireNonNull(name).getValue())) {
                                    propertyTags.add(tag);
                                    systemPropertyNames.add(name.getValue());
                                }
                            } catch (NullPointerException npe) {
                                LOG.debug("Tag " + tag + " either has no attribute \'sv:name\' or it has no value.");
                            }
                        }
                    }
                }
            }
        };

        LOG.debug("Looking for system properties in " + file.getName() + "...");
        fileVisitor.visitFile(file);
        LOG.debug(propertyTags.size() + " tags with one of " + systemPropertyNames.size() + " different "
                + "system properties found in file " + file + ".");
    }

    /**
     * Removes the XmlTags whose "sv:name" attribute value was chosen
     *
     * @param chosenProperties The attribute values of "sv:name" to be deleted
     * @param propertyTags     The XmlTags which may be deleted
     */
    private void removeChosenTags(ArrayList<String> chosenProperties, ArrayList<XmlTag> propertyTags) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (XmlTag propTag : propertyTags) {
                try {
                    if (chosenProperties.contains(Objects.requireNonNull(propTag.getAttribute("sv:name")).getValue()))
                        propTag.delete();
                }
                // Deletion not possible
                catch (IncorrectOperationException exc) {
                    LOG.info("Could not delete XML-tag " + propTag + " containing system property!", exc);
                }
                // Attribute is null
                catch (NullPointerException npe) {
                    LOG.info("Attribute \'sv:name\' of XML-tag " + propTag + " has no value.", npe);
                }
            }
        });
    }
}
