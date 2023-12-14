package org.sterl.svg2png;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.sterl.svg2png.config.OutputConfig;
import org.sterl.svg2png.util.FileUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

public enum CliOptions {
    FILE("f", "file", true, "Source file to convert."),
    FOLDER("d", "in-dir", true, "Source directory with one or more files to convert."),
    OUTPUT("o", "out-dir", true, "Output directory where to put the file."),
    NAME("n", "name", true, "New name to use for all output files."),
    WIDTH("w", "width", true, "Width of the output file."),
    HEIGHT("h", "height", true, "Height of the output file."),
    CONFIG("c", "config", true, "JSON Config file for the file output."),
    ALLOW_EXTERNAL("e", "allow-external", false, "Allow external entities to be loaded by the SVG."),
    NO_ALPHA("a", "no-alpha", true, "Saves PNG without alpha channel and with specified background hex triplet. (Needed for iOS assets.)"),
    BACKGROUND_COLOR(null, "background-color", true, "Provide a custom background color to use for opaque image formats e.g. 0077FF."),

    FORCE_TRANSPARENT_WHITE(null, "transparent-white", false, "This is a trick so that viewers which do not support the alpha channel will see a white background (and not a black one)."),
    ANDROID(null, "android", false, "Android Icon 48dp mdpi 48x48 -> xxxhdpi 192x192."),
    ANDROID_LAUNCH(null, "android-launch", false, "Android Launcher Icon config mdpi 48x48 -> xxxhdpi 192x192."),
    ANDROID_ICON(null, "android-icon", false, "Android Icon (Action Bar, Dialog etc.)  config mdpi 36x36 -> xxxhdpi 128x128."),
    ANDROID_SMALL(null, "android-small", false, "Android Small default config from mdpi 24x24 -> xxxhdpi 96x96."),
    ANDROID_24dp(null, "android-24dp", false, "Android 24dp icons, with suffix _24dp -- mdpi 24x24 -> xxxhdpi 96x96."),
    ANDROID_36dp(null, "android-36dp", false, "Android 36dp icons, with suffix _36dp -- mdpi 36x36 -> xxxhdpi 144x144."),
    ANDROID_48dp(null, "android-48dp", false, "Android 48dp icons, with suffix _48dp -- mdpi 48x48 -> xxxhdpi 192x192."),
    IOS_ICONS(null, "ios", false, "iOS icons and Contents.json.")
    ;

    private final String shortName;
    private final String longName;
    private final boolean hasArg;
    private final String description;

    CliOptions(String shortName, String longName, boolean hasArg, String description) {
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

    private static String getValue(CommandLine cmd, CliOptions option) {
        if (option.shortName == null) return cmd.getOptionValue(option.longName);
        else return cmd.getOptionValue(option.shortName);
    }

    public static OutputConfig parse(CommandLine cmd) {
        OutputConfig result = selectPredefinedJsonConfig(cmd);
        
        if (cmd.hasOption(ALLOW_EXTERNAL.longName)) {
            result.setAllowExternalResource(true);
        }
        if (cmd.hasOption(NO_ALPHA.longName)) {
            String bg = getValue(cmd, NO_ALPHA);
            validateColor(bg, "--" + NO_ALPHA.longName);
            result.setNoAlpha(bg);
        }
        
        result.setInputFile(getValue(cmd, FILE));
        result.setInputDirectory(getValue(cmd, FOLDER));
        result.setOutputName(getValue(cmd, NAME));
        result.setOutputDirectory(getValue(cmd, OUTPUT));
        if (result.getOutputDirectory() == null) {
            result.setOutputDirectory(new File(".").getAbsolutePath());
        }

        if (result.getFiles().isEmpty()) result.newOutput();
        
        result.applyFileName(getValue(cmd, NAME));

        applyWidthAndHeigth(cmd, result);
        
        if (cmd.hasOption(BACKGROUND_COLOR.longName)) {
            String color = getValue(cmd, BACKGROUND_COLOR);
            validateColor(color, "--" + BACKGROUND_COLOR.longName);
            result.applyBackgroundColor(color);
        }

        if (cmd.hasOption(FORCE_TRANSPARENT_WHITE.longName)) {
            result.enableForceTransparentWhite();
        }
        return result;
    }

    private static void applyWidthAndHeigth(CommandLine cmd, OutputConfig out) {
        int width = -1;
        int height = -1;
        if (cmd.hasOption(WIDTH.shortName)) {
            width = Integer.parseInt(getValue(cmd, WIDTH));
        }
        if (cmd.hasOption(HEIGHT.shortName)) {
            height = Integer.parseInt(getValue(cmd, HEIGHT));
        }
        out.applyOutputSize(width, height);
    }

    private static void validateColor(String color, String name) {
        if (color == null || color.length() <= 5)
            throw new RuntimeException("Background must be specified as hex triplet e.g. " + name + " 2a5c8b");

        final Pattern pattern = Pattern.compile("[0-9a-f]{6}", Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(color);
        if (!matcher.find())
            throw new RuntimeException("Background must be specified as hex triplet e.g. " + name + " 2a5c8b");
    }

    private static OutputConfig selectPredefinedJsonConfig(CommandLine cmd) {
        OutputConfig result;
        final ObjectMapper m = new ObjectMapper();
        if (cmd.hasOption(CONFIG.shortName)) {
            try {
                result = m.readValue(FileUtil.newFile(cmd.getOptionValue(CONFIG.shortName)), OutputConfig.class);
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
        } else if (cmd.hasOption(ANDROID_ICON.longName)) {
            try {
                result = m.readerFor(OutputConfig.class).readValue(CliOptions.class.getResourceAsStream("/android-icon.json"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (cmd.hasOption(ANDROID_LAUNCH.longName)) {
            try {
                result = m.readerFor(OutputConfig.class).readValue(CliOptions.class.getResourceAsStream("/android-launcher.json"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (cmd.hasOption(ANDROID_24dp.longName)) {
            try {
                result = m.readerFor(OutputConfig.class).readValue(CliOptions.class.getResourceAsStream("/android-24dp.json"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (cmd.hasOption(ANDROID_36dp.longName)) {
            try {
                result = m.readerFor(OutputConfig.class).readValue(CliOptions.class.getResourceAsStream("/android-36dp.json"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (cmd.hasOption(ANDROID_48dp.longName)) {
            try {
                result = m.readerFor(OutputConfig.class).readValue(CliOptions.class.getResourceAsStream("/android-48dp.json"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (cmd.hasOption(IOS_ICONS.longName)) {
            if (!cmd.hasOption(NAME.shortName))
                throw new RuntimeException("-n name must be specified when --ios is used.");

            try {
                result = m.readerFor(OutputConfig.class).readValue(CliOptions.class.getResourceAsStream("/ios.json"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            result = new OutputConfig();
        }
        return result;
    }
}
