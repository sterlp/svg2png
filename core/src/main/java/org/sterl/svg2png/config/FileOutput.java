package org.sterl.svg2png.config;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Meta information where to output one converted file.
 */
@Data @NoArgsConstructor
public class FileOutput {
    private int height = -1;
    private int width = -1;
    private String name;
    private String directory;
    private String nameSuffix;
    private String namePrefix;
    private String backgroundColor = null;
    /**
     * https://xmlgraphics.apache.org/batik/javadoc/org/apache/batik/transcoder/image/ImageTranscoder.html#KEY_FORCE_TRANSPARENT_WHITE
     * PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE
     */
    private boolean forceTransparentWhite = false;

    public File toOutputFile(File source, String basePath, String outName) {
        // either a set path or the one of the parent
        String path = basePath != null ? basePath : source.getParent();
        if (path == null) path = "";
        // if the configured path is absolute we take it, otherwise we append it
        if (directory != null && directory.startsWith(File.separator)) {
            path = directory;
        }
        if (directory != null) {
            if (path.length() > 0) {
                path += File.separator + directory;
            } else {
                path += directory;
            }
        }
        // setting the name
        path += File.separator + buildName(FilenameUtils.getBaseName(source.getName()), name != null ? name : outName, namePrefix, nameSuffix);

        return new File(path);
    }

    static String buildName(String srcName, String outName, String prefix, String suffix) {
        String name = outName != null ? outName : srcName;
        String result;
        if (prefix != null && !name.startsWith(prefix)) result = prefix + name;
        else result = name;

        if (suffix != null && !result.endsWith(suffix)) result += suffix;
        if (!result.endsWith(".png")) result += ".png";
        return result;
    }
    
    public boolean hasBackgroundColor() {
        return backgroundColor != null && backgroundColor.length() > 0;
    }
}
