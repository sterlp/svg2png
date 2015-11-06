package org.sterl.encoding;

import java.io.FileInputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.sterl.encoding.config.FileOutput;
import org.sterl.encoding.config.OutputConfig;
import org.sterl.encoding.util.FileUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

public enum CliOptions {
    FILE("f", null, true, "Source file to convert."),
    FOLDER("d", null, true, "Source directory with one or more files to convert."),
    OUTPUT("o", null, true, "Output directory where to put the file."),
    NAME("n", null, true, "New name to use for all output files."),
    WIDTH("w", null, true, "Width of the output file."),
    HEIGHT("h", null, true, "Height of the output file."),
    CONFIG("c", null, true, "JSON Config file for the file output."),
    
    ANDROID(null, "android", false, "Android default config from mdpi 48x48 -> xxxhdpi 192x192."),
    ANDROID_SMALL(null, "android-small", false, "Android Small default config from mdpi 24x24 -> xxxhdpi 96x96.")
    ;
    
    private final String shortName;
    private final String longName;
    private final boolean hasArg;
    private final String description;
    
    private CliOptions(String shortName, String longName, boolean hasArg, String description) {
        this.shortName = shortName;
        this.longName = longName;
        this.hasArg = hasArg;
        this.description = description;
    }
    
    public static void addOptions(Options options) {
        for (CliOptions o : CliOptions.values()) {
            options.addOption(o.shortName, o.longName, o.hasArg, o.description);
        }
    }
    
    public static OutputConfig parse(CommandLine cmd) {
        OutputConfig result;
        ObjectMapper m = new ObjectMapper();
        if (cmd.hasOption(CONFIG.shortName)) {
            try {
                result = m.readerFor(OutputConfig.class).readValue(IOUtils.toString(new FileInputStream(FileUtil.newFile(cmd.getOptionValue(CONFIG.shortName)))));
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse config '" + cmd.getOptionValue(CONFIG.shortName) + "'. " + e.getMessage(), e);
            }
        } else if (cmd.hasOption(ANDROID.longName)) {
            try {
                result = m.readerFor(OutputConfig.class).readValue(CliOptions.class.getResourceAsStream("/android.json"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (cmd.hasOption(ANDROID_SMALL.longName)) {
            try {
                result = m.readerFor(OutputConfig.class).readValue(CliOptions.class.getResourceAsStream("/android-small.json"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            result = new OutputConfig();
        }

        result.setInputFile(getValue(cmd, FILE));
        result.setInputDirectory(getValue(cmd, FOLDER));
        result.setOutputName(getValue(cmd, NAME));
        result.setOutputDirectory(getValue(cmd, OUTPUT));

        if (result.getFiles().isEmpty()) {
            FileOutput out = new FileOutput();
            if (cmd.hasOption(WIDTH.shortName)) {
                out.setWidth(Integer.parseInt(getValue(cmd, WIDTH)));
            }
            if (cmd.hasOption(HEIGHT.shortName)) {
                out.setHeight(Integer.parseInt(getValue(cmd, HEIGHT)));
            }
            result.getFiles().add(out);
        }

        return result;
    }
    
    private static String getValue(CommandLine cmd, CliOptions option) {
        if (cmd.hasOption(option.shortName)) return cmd.getOptionValue(option.shortName);
        else return null;
    }
}
