package actions;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

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
        return isXmlFile(file);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {

    }

    private boolean isXmlFile(PsiFile file) {
        return file.getVirtualFile().getExtension().toLowerCase().equals("xml") || file.getFileType().getDefaultExtension().toLowerCase().equals("xml");
    }
}
