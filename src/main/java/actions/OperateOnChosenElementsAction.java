package actions;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.util.IncorrectOperationException;
import dialogs.ChooseValuesDialog;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public abstract class OperateOnChosenElementsAction extends BaseIntentionAction implements DumbAware {

    private static final Logger LOG = Logger.getInstance(OperateOnChosenElementsAction.class);
    protected Project project;


    @NotNull
    @Override
    public String getText() {
        return "Operate on elements of this file...";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Mgnl Utils";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return false;
    }

    /**
     * @return whether or not the choices of the user should be saved for the next invocation of this action
     */
    protected boolean saveChoices() {
        return true;
    }

    protected abstract String getSavePrefix();

    /**
     * Runs through all PsiElements on @param file and collects them based on {@link #isChoosable(PsiElement)}. The
     * identifiers of the collected choosable elements {@link #getIdentifier(PsiElement)} are displayed to the user and
     * he can select the identifiers in a dialog. On the elements corresponding to the identifiers, the operation
     * {@link #operateOn(PsiElement)} is called.
     */
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        this.project = project;
        ApplicationManager.getApplication().invokeLater(
                () -> {
                    // List of elements the user may remove
                    ArrayList<PsiElement> choosableElements = new ArrayList<>();
                    // List of IDs to display to the user as selectable options
                    Set<String> choosableElementsIds = new TreeSet<>(String::compareToIgnoreCase);

                    collectChoosableElements(file, choosableElements, choosableElementsIds);
                    ChooseValuesDialog chooseElementsDialog = new ChooseValuesDialog(choosableElementsIds, project, saveChoices(), getSavePrefix());
                    chooseElementsDialog.pack();

                    // Return iff dialog was cancelled
                    if (!chooseElementsDialog.showAndGet())
                        return;


                    ArrayList<String> chosenElementIds = chooseElementsDialog.getChosenValues();

                    operateOnChosenElements(chosenElementIds, choosableElements);
                });
    }

    /**
     * Puts all the elements {@link #isChoosable(PsiElement)} applies to into @param choosableElements and their
     * identifiers into choosableElementIds.
     *
     * @param file                PsiFile to be queried for choosable elements
     * @param choosableElements   Collection to put the property Tags in there
     * @param choosableElementIds Collection to put the system properties in
     */
    protected void collectChoosableElements(PsiFile file, Collection<PsiElement> choosableElements, Collection<String> choosableElementIds) {
        PsiRecursiveElementWalkingVisitor fileVisitor = new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(final PsiElement element) {
                super.visitElement(element);
                if (isChoosable(element)) {
                    choosableElements.add(element);
                    choosableElementIds.add(getIdentifier(element));
                }
            }
        };

        LOG.debug("Looking for choosable elements in " + file.getName() + "...");
        fileVisitor.visitFile(file);
        LOG.debug(choosableElements.size() + " elements found with " + choosableElementIds.size() + " different "
                + "identifiers found in file " + file + ".");
    }


    /**
     * Decides whether this @param element should be choosable by the user, i.e. its identifier should be displayed to
     * him in the ChooseValuesDialog
     */
    protected abstract boolean isChoosable(final PsiElement element);

    /**
     * Calls {@link #operateOn(PsiElement)} on all elements in @param elements whose identifier is in @param chosenElementIds
     *
     * @param chosenElementIds The identifiers which were chosen by the user
     * @param elements         The elements which may be operateOn, depending on whether the user selects them
     */
    protected void operateOnChosenElements(ArrayList<String> chosenElementIds, ArrayList<PsiElement> elements) {
        ArrayList<PsiElement> chosenElements = new ArrayList<>();

        for (PsiElement element : elements) {
            if (chosenElementIds.contains(getIdentifier(element)))
                chosenElements.add(element);
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (PsiElement chosen : chosenElements) {
                operateOn(chosen);
            }
        });
    }

    /**
     * @return The identifier corresponding to this element.
     * The identifier is used to display to the user.
     */
    protected abstract String getIdentifier(PsiElement element);

    /**
     * The operation to be performed on every chosen @param element
     */
    protected abstract void operateOn(PsiElement element);
}
