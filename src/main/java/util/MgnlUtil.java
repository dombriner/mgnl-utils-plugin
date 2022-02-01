package util;


public class MgnlUtil {

    public static boolean isSystemProperty(String string) {
        return string.startsWith("mgnl:") || string.startsWith("jcr:");
    }
}
