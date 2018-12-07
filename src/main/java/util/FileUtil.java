package util;

import com.intellij.psi.PsiFile;

public class FileUtil {

    public static boolean isFileType(PsiFile file, String extension) {
        return file.getFileType().getDefaultExtension().toLowerCase().equals(extension.toLowerCase()) || file.getVirtualFile().getExtension().toLowerCase().equals(extension.toLowerCase());
    }
}
