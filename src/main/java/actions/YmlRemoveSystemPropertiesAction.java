package actions;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import util.MgnlUtil;

import static util.FileUtil.isFileType;

/**
 * Collects all the system properties used in the XML file. The user can then choose which system properties should
 * be removed in a dialog. The XML tags containing chosen properties will be removed from the file.
 */
public class YmlRemoveSystemPropertiesAction extends RemoveChosenElementsAction implements DumbAware {

    private static final Logger LOG = Logger.getInstance(YmlRemoveSystemPropertiesAction.class);
    protected final String message = "Remove system properties...";
    private String familyName = "Mgnl Utils";


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
    protected String getSavePrefix() {
        return "remove-yml-system-property";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return isFileType(file, "yml") || isFileType(file, "yaml");
    }

    /**
     * Element is be choosable by the user if its key is a system property
     */
    @Override
    protected boolean isChoosable(PsiElement element) {
        if (element instanceof YAMLKeyValue) {
            YAMLKeyValue keyValue = (YAMLKeyValue) element;
            String text = keyValue.getKeyText();
            if (StringUtils.isEmpty(text))
                return false;
            return MgnlUtil.isSystemProperty(text);
        }
        return false;
    }

    /**
     * Gets the identifier (i.e. key text) of the YAMLKeyValue @param element.
     *
     * @param element Element to get Identifier of. Should always be of type YAMLKeyValue.
     */
    @Override
    protected String getIdentifier(PsiElement element) {
        if (element instanceof YAMLKeyValue) {
            YAMLKeyValue keyValue = (YAMLKeyValue) element;
            return keyValue.getKeyText();
        }
        LOG.error("Property to get identifier of is not an YML key-value, this should never happen!");
        return "ERROR – not an YML key-value";
    }

    // The element does not include its surrounding whitespace or the EOL element. Therefore, we need to remove them here.
    @Override
    protected void operateOn(PsiElement element) {
        YAMLUtil.deleteSurroundingWhitespace(element);
        super.operateOn(element);
    }

}
