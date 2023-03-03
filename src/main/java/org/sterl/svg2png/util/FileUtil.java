package org.sterl.svg2png.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class FileUtil {

    public static File newFile(String path) {
        if (path.startsWith("file:/")) {
            return new File(URI.create(path));
        } else {
            return new File(path);
        }
    }
    
    public static void recreateNewFile(File outputFile) throws IOException {
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();
    }
}
