package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.util.StringUtils;

/**
 * Simple util for getting id and extension from owner id data.
 *
 * @author ykalapusha
 */
public final class PhotoStorageUtil {

    private PhotoStorageUtil() {}

    /**
     * Get id from owner id.
     *
     * @param name  owner id
     * @return      id of photo
     */
    public static String getId(String name) {
        String fileName = stripExtension(name);
        return StringUtils.isEmpty(fileName) ? "" : fileName;
    }

    private static String stripExtension (String str) {
        if (str == null)
            return null;

        int pos = str.lastIndexOf(".");
        if (pos == -1)
            return str;

        return str.substring(0, pos);
    }

    /**
     * Get extension from owner id.
     *
     * @param name  owner id
     * @return      extension of photo.
     */
    public static String getExtension(String name) {
        if (name == null)
            return null;

        int pos = name.lastIndexOf(".");
        if (pos == -1)
            return "";

        return name.substring(pos);
    }

    public static long getOriginalId(String name) {
        if (StringUtils.isEmpty(name))
            return -1;


        int pos = name.indexOf("_");
        if (pos == -1) {
            String id = getId(name);
            return StringUtils.isEmpty(id) ? -1 : Long.parseLong(id);
        }

        return Long.parseLong(name.substring(0, pos));
    }
}
