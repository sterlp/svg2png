package org.sterl.encoding.config;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.sterl.encoding.util.FileUtil;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Meta information where to output one converted file.
 */
@Data @NoArgsConstructor
public class FileOutput {
    private int height = -1;
    private int width = -1;
    private String directory;
    private String nameSuffix;
    private String namePrefix;

    public File toOutputFile(File source, String basePath, String outName) {
        // either a set path or the one of the parent
        String path = basePath != null ? basePath : source.getParent();
        
        // if the configured path is absolute we take it, otherwise we append it
        if (directory != null && directory.startsWith("/")) {
            path = directory;
        } if (directory != null) {
            path += "/" + directory;
        }
        // setting the name
        path += "/" + buildName(FilenameUtils.getBaseName(source.getName()), outName, namePrefix, nameSuffix);
        
        return FileUtil.newFile(path);
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
}
