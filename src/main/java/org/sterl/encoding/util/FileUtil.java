package org.sterl.encoding.util;

import java.io.File;
import java.net.URI;

public class FileUtil {

    public static File newFile(String path) {
        if (path.startsWith("file:/")) {
            return new File(URI.create(path));
        } else {
            return new File(path);
        }
    }
}
