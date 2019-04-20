package actions;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public abstract class RemoveChosenElementsAction extends OperateOnChosenElementsAction implements DumbAware {

    private static final Logger LOG = Logger.getInstance(RemoveChosenElementsAction.class);

    @NotNull
    @Override
    public String getText() {
        return "Remove elements of this file...";
    }

    protected  String getSavePrefix() {
        return "remove";
    }


    /**
     * Remove every chosen @param element
     */
    protected void operateOn(PsiElement element) {
        try {
            element.delete();
        } catch (IncorrectOperationException exc) {
            LOG.info("Could not delete element " + element, exc);
        }
    }
}
