package completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLElementTypes;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import com.intellij.openapi.util.Pair;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/** TODO:
 * Documentation. Only comment is a TODOs comment, seriously.
 * Lombok support? Needs to be tested whether that works
 * $type support? $type does not work, only class
 * lists don't work - i.e. in WorkbenchDefinition, List<ContentViewDefinition> will break it, not giving the
 * ContentViewDefinition fields for the elements of the yaml list
 * Make limit (and therefore performance) configurable and maybe even add option to disable completion contributor
 */
public class DefinitionsClassBasedCompleter extends CompletionContributor {

    public DefinitionsClassBasedCompleter() {

        extend(null,
                psiElement().withElementType(YAMLElementTypes.TEXT_SCALAR_ITEMS).
                        withParent(psiElement().withElementType(YAMLElementTypes.SCALAR_PLAIN_VALUE)),
                new DefinitionsClassCompletionProvider()
                );

    }

    public class DefinitionsClassCompletionProvider extends CompletionProvider<CompletionParameters> {
        public DefinitionsClassCompletionProvider() {}

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
            PsiElement position = parameters.getPosition();
            Pair<YAMLKeyValue, String> classElement = getClassElement(position.getParent(), 5, "");
            if (classElement == null || classElement.getFirst() == null)
                return;

            String classClassifier = classElement.getFirst().getValueText();
            String path = classElement.getSecond();

            Project project = position.getProject();

            PsiClass definitionClass = JavaPsiFacade.getInstance(project).findClass(classClassifier, GlobalSearchScope.allScope(project));

            if (definitionClass == null)
                return;

            PsiClass realClass = navigateToClass(definitionClass, path);

            if (realClass == null)
                return;

            for (PsiField field : realClass.getAllFields()) {
                result.addElement(LookupElementBuilder.create(field.getName()));
            }
        }

        private PsiClass navigateToClass(PsiClass definitionClass, String path) {
            if (StringUtils.isEmpty(path))
                return definitionClass;
            String[] fieldNames = path.split("/");
            String firstFieldName = fieldNames[0];
            PsiField field = getFieldFromClass(firstFieldName, definitionClass);
            if (field == null)
                return null;
            String newPath = fieldNames.length <= 1 ? "" : path.substring(firstFieldName.length() + 1);
            return navigateToClass(PsiTypesUtil.getPsiClass(field.getType()), newPath);
        }

        private PsiField getFieldFromClass(String firstFieldName, PsiClass definitionClass) {
            if (definitionClass == null)
                return null;
            for (PsiField field : definitionClass.getAllFields()) {
                if (field.getName().equalsIgnoreCase(firstFieldName))
                    return field;
            }
            return getFieldFromClass(firstFieldName, definitionClass.getSuperClass());
        }

        private Pair<YAMLKeyValue, String> getClassElement(PsiElement element, int iterationsLeft, String path) {
            if (element instanceof YAMLKeyValue)
                path = (StringUtils.isEmpty(path) ? ((YAMLKeyValue) element).getKeyText() : ((YAMLKeyValue) element).getKeyText() + "/" + path);
            if (iterationsLeft <= 0 || element == null)
                return null;
            for (PsiElement child: element.getChildren()) {
                if (isClassElement(child))
                    return new Pair<>((YAMLKeyValue) child, removeLastLayer(path));
            }
            if (isClassElement(element))
                return new Pair<>((YAMLKeyValue) element, path);
            return getClassElement(element.getParent(), --iterationsLeft, path);
        }

        private String removeLastLayer(String path) {
            return path.substring(path.indexOf('/') + 1);
        }

        private boolean isClassElement(PsiElement element) {
            return element instanceof YAMLKeyValue
                    && ((YAMLKeyValue) element).getKeyText().equals("class");
        }
    }
}
