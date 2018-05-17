package cx.ath.matthew.utils;

/**
 * Helper to get the used operating system.
 *
 * @author hypfvieh
 * @since v0.8.4 - 2018-05-17
 */
public class OsHelper {

    public static boolean isMacOs() {
        String osName = System.getProperty("os.name");
        return osName == null ? false : osName.toLowerCase().startsWith("mac");
    }

    public static String getMacOsMajorVersion() {
        if (!isMacOs()) {
            return null;
        }

        String osVersion = System.getProperty("os.version");

        if (osVersion != null) {
            String[] split = osVersion.split("\\.");
            if (split.length >= 2) {
                return split[0] + "." + split[1];
            } else {
                return osVersion;
            }
        }

        return null;
    }
}
