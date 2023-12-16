package org.sterl.svg2png.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class FileUtil {

    public static File newFile(String path) {
        File result = new File(path);
        if (!result.exists() && path.startsWith("file:/")) {
            result = new File(URI.create(path));
        }
        if (!result.exists() && FileUtil.class.getResource(path) != null) {
            result = new File(URI.create(FileUtil.class.getResource(path).toString()));
        }
        if (!result.exists()) {
            throw new IllegalArgumentException("File " + path + " not found!");
        }
        return result;
    }
    
    public static void recreateNewFile(File outputFile) throws IOException {
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();
    }
}
