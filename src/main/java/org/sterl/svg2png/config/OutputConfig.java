package org.sterl.svg2png.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sterl.svg2png.util.FileUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class OutputConfig {
    private String inputDirectory;
    private String inputFile;
    private String outputName;
    private String outputDirectory;
    /**
     * https://xmlgraphics.apache.org/batik/javadoc/org/apache/batik/transcoder/image/ImageTranscoder.html#KEY_FORCE_TRANSPARENT_WHITE
     * PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE
     */
    private boolean allowExternalResource = false;
    private boolean forceTransparentWhite = false;
    private String noAlpha = null;
    private boolean contentsJson = true;

    private List<FileOutput> files = new ArrayList<>();
    
    public static OutputConfig fromPath(String file) {
        OutputConfig result = new OutputConfig();
        File f = FileUtil.newFile(file);
        if (!f.exists()) throw new IllegalArgumentException(file + " not found!");
        
        if (f.isFile()) result.setInputFile(f.getAbsolutePath());
        else result.setInputDirectory(f.getAbsolutePath());

        
        result.setOutputDirectory(new File(".").getAbsolutePath()); 
        result.addOutput(); // one result
        return result;
    }

    public void addOutput() {
        files.add(new FileOutput());
    }
    
    public boolean hasDirectoryOrFile() {
        return inputFile != null || inputDirectory != null;
    }
}