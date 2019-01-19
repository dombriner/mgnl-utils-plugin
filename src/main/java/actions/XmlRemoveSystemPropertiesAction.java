package actions;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.lang.ASTNode;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static util.FileUtil.isFileType;

public class XmlRemoveSystemPropertiesAction extends BaseIntentionAction {

    private final String message = "Remove system properties...";
    private String familyName = "Mgnl Utils";


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

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        ArrayList<XmlTag> propertyTags = new ArrayList<>();
        Set<String> systemPropertyNames = new HashSet<>();


        PsiRecursiveElementWalkingVisitor fileVisitor = new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(final PsiElement element) {
                super.visitElement(element);
                if (element instanceof XmlTag) {
                    XmlTag tag = (XmlTag) element;
                    if (tag.getName().equals("sv:property")) {
                        if (tag.getAttribute("sv:name") instanceof XmlAttribute) {
                            XmlAttribute name = tag.getAttribute("sv:name");
                            if (MgnlUtil.isSystemProperty(name.getValue())) {
                                propertyTags.add(tag);
                                systemPropertyNames.add(name.getValue());
                            }
                        }
                    }
                }
            }
        };

        fileVisitor.visitFile(file);

        ChooseValuesDialog choosePropertiesDialog = new ChooseValuesDialog(systemPropertyNames, project);
        choosePropertiesDialog.pack();
        if (!choosePropertiesDialog.showAndGet())
            return;

        ArrayList<String> chosenProperties = choosePropertiesDialog.getChosenValues();
        for (XmlTag propTag:propertyTags) {
            try {
                if (chosenProperties.contains(propTag.getAttribute("sv:name").getValue()))
                    propTag.delete();
            }
            // Deletion not possible
            catch (IncorrectOperationException exc) {
                exc.printStackTrace();
            }
        }
    }
}
