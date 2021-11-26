package util;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

public class FileUtil {

    public static boolean isFileType(@Nullable PsiFile file,@Nullable String extension) {
        try {
            return file.getFileType().getDefaultExtension().equalsIgnoreCase(extension) || extension.equalsIgnoreCase(file.getVirtualFile().getExtension());
        } catch(NullPointerException npe) {
            return false;
        }
    }
}
