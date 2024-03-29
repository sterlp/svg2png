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
    private boolean allowExternalResource = false;
    private String noAlpha = null;
    private boolean contentsJson = false;

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

    /**
     * @param width -1 will be ignored
     * @param height -1 will be ignored
     */
    public void applyOutputSize(int width, int height) {
        for (FileOutput fileOutput : files) {
            if (width > -1) fileOutput.setWidth(width);
            if (height > -1) fileOutput.setHeight(height);
        }
    }

    public void newOutput() {
        this.files.add(new FileOutput());
    }

    public void applyFileName(String name) {
        for (FileOutput f : files) {
            f.setName(name);
        }
    }

    public void applyBackgroundColor(String color) {
        for (FileOutput f : files) {
            f.setBackgroundColor(color);
        }
    }

    public void enableForceTransparentWhite() {
        for (FileOutput f : files) {
            f.setForceTransparentWhite(true);
        }
    }
}