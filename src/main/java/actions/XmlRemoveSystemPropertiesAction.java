package actions;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import util.MgnlUtil;

import java.util.Objects;

import static util.FileUtil.isFileType;

/**
 * Collects all the system properties used in the XML file. The user can then choose which system properties should
 * be removed in a dialog. The XML tags containing chosen properties will be removed from the file.
 */
public class XmlRemoveSystemPropertiesAction extends RemoveChosenElementsAction implements DumbAware {

    private static final Logger LOG = Logger.getInstance(XmlRemoveSystemPropertiesAction.class);
    protected final String message = "Remove system properties...";
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
    protected String getSavePrefix() {
        return "remove-system-property";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return isFileType(file, "xml");
    }

    @Override
    protected boolean isChoosable(PsiElement element) {
        if (element instanceof XmlTag) {
            XmlTag tag = (XmlTag) element;
            if (tag.getName().equals("sv:property")) {
                if (tag.getAttribute("sv:name") instanceof XmlAttribute) {
                    try {
                        XmlAttribute name = tag.getAttribute("sv:name");
                        if (MgnlUtil.isSystemProperty(Objects.requireNonNull(name).getValue())) {
                            return true;
                        }
                    } catch (NullPointerException npe) {
                        LOG.debug("Tag " + tag + " either has no attribute \'sv:name\' or it has no value.");
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets the identifier (i.e. sv:name attribute) of the XmlTag @param element.
     *
     * @param element Element to get Identifier of. Should always be of type XmlTag.
     */
    @Override
    protected String getIdentifier(PsiElement element) {
        if (element instanceof XmlTag) {
            XmlTag tag = (XmlTag) element;
            if (tag.getName().equals("sv:property")) {
                if (tag.getAttribute("sv:name") instanceof XmlAttribute) {
                    try {
                        XmlAttribute name = tag.getAttribute("sv:name");
                        return Objects.requireNonNull(name).getValue();
                    } catch (NullPointerException npe) {
                        LOG.debug("Tag " + tag + " either has no attribute \'sv:name\' or it has no value.");
                        return "sv:name is null";
                    }
                }
            }
        }
        LOG.error("Property to get identifier of is not an XML tag, this should never happen!");
        return "ERROR – not an XML tag";
    }
}
