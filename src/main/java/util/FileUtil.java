package util;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

public class FileUtil {

    public static boolean isFileType(@Nullable PsiFile file,@Nullable String extension) {
        try {
            return file.getFileType().getDefaultExtension().toLowerCase().equals(extension.toLowerCase()) || extension.toLowerCase().equals(file.getVirtualFile().getExtension().toLowerCase());
        } catch(NullPointerException npe) {
            return false;
        }
    }
}
