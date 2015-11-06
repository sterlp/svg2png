package org.sterl.encoding.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sterl.encoding.util.FileUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class OutputConfig {
    private String inputDirectory;
    private String inputFile;
    private String outputName;
    private String outputDirectory;

    private List<FileOutput> files = new ArrayList<>();
    
    public static OutputConfig fromPath(String file) {
        OutputConfig result = new OutputConfig();
        File f = FileUtil.newFile(file);
        if (!f.exists()) throw new IllegalArgumentException(file + " not found!");
        
        if (f.isFile()) result.setInputFile(f.getAbsolutePath());
        else result.setInputDirectory(f.getAbsolutePath());

        
        result.setOutputDirectory("."); // all into the current directory
        result.addOutput(); // one result
        return result;
    }

    public void addOutput() {
        files.add(new FileOutput());
    }
}