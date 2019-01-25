package util;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class FileUtil {

    public static boolean isFileType(PsiFile file, @NotNull String extension) {
        return file.getFileType().getDefaultExtension().toLowerCase().equals(extension.toLowerCase()) || extension.toLowerCase().equals(file.getVirtualFile().getExtension().toLowerCase());
    }
}
