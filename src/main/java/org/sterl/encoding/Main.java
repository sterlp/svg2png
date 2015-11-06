package org.sterl.encoding;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.sterl.encoding.config.OutputConfig;
import org.sterl.encoding.util.FileUtil;

//https://commons.apache.org/proper/commons-cli/usage.html
public class Main {

    static Options options = new Options();
    static HelpFormatter formatter = new HelpFormatter();
    
    static {
        CliOptions.addOptions(options);
    }

    public static void main(String[] args) {
        try {
            
            run(args);
        } catch (Exception e) {
            printHelp();
            System.out.println();
            System.err.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    static List<File> run(String[] args) throws ParseException, IOException, TranscoderException {
        OutputConfig cfg = null;
        if (null == args || args.length == 0) {
            printHelp();
            System.exit(0);
        } else if (args.length == 1) {
            cfg = OutputConfig.fromPath(args[0]);
        } else {
            CommandLine cmd = new DefaultParser().parse( options, args);
            cfg = CliOptions.parse(cmd);
        }
        
        // validation
        if ((cfg.getInputFile() == null && cfg.getInputDirectory() == null)
                || (cfg.getInputFile() != null && cfg.getInputDirectory() != null)) {
            throw new IllegalArgumentException("Pleace specify either a directory or a file to convert!");
        } else if (cfg.getInputFile() != null) {
            File f = FileUtil.newFile(cfg.getInputFile());
            if (!f.exists()) throw new IllegalArgumentException("File '" + cfg.getInputFile() + "' not found!");
            if (!f.isFile()) throw new IllegalArgumentException(cfg.getInputFile() + " is not a file!");
        } else if (cfg.getInputDirectory() != null) {
            File d = FileUtil.newFile(cfg.getInputDirectory());
            if (!d.exists()) throw new IllegalArgumentException("Directory " + cfg.getInputDirectory() + " not found!");
            if (!d.isDirectory()) throw new IllegalArgumentException(cfg.getInputFile() + " is not a directory!");
        }
        
        return new SvgToPng(cfg).convert();
    }
    
    private static void printHelp() {
        System.out.println(StringUtils.repeat("=", 80));
        System.out.println(StringUtils.center("SVG to PNG", 80));
        System.out.println();
        formatter.printHelp( "svgtopng", options );
        System.out.println();
        System.out.println("Examples:");
        System.out.println("---------");
        System.out.println("# just convert a file");
        System.out.println("svgtopng foo.svg");
        System.out.println("");
        System.out.println("# generate a PNG with a name");
        System.out.println("svgtopng -f foo.svg -n bar.png");
        System.out.println("");
        System.out.println("# convert all file in a directory");
        System.out.println("svgtopng -d /Picures/icons/svg -o /Pictures/icons/png");
        System.out.println("");
        System.out.println("# convert with a JSON configuration");
        System.out.println("svgtopng -d . -c my.json");
        System.out.println("");
        System.out.println("# convert SVG files using the default Android configuration");
        System.out.println("svgtopng -d . -o /dev/workset/android-project/app/src/main/res --android");
    }

}
