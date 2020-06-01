package org.sterl.svg2png;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.junit.Test;
import org.sterl.svg2png.config.FileOutput;
import org.sterl.svg2png.config.OutputConfig;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CliOptionsTest {

    @Test
    public void test() throws Exception {
        Options options = new Options();
        CliOptions.addOptions(options);

        CommandLine cmd = new DefaultParser().parse(options, new String[]{"--android", "-f", "ic_launcher.svg"});
        OutputConfig cfg = CliOptions.parse(cmd);

        assertNull(cfg.getInputDirectory());
        assertEquals(new File(".").getAbsolutePath(), cfg.getOutputDirectory());
        assertNull(cfg.getOutputName());
        assertEquals(5, cfg.getFiles().size());
        assertEquals("ic_launcher.svg", cfg.getInputFile());

        for (FileOutput fo : cfg.getFiles()) {
            final String outPath = fo.toOutputFile(new File("ic_launcher.svg"), null, null).getAbsolutePath();
            System.out.println(outPath);
            assertTrue(!outPath.startsWith("null/"));
            assertTrue(outPath.endsWith("/ic_launcher.png".replace('/', File.separatorChar)));
        }
    }
}
