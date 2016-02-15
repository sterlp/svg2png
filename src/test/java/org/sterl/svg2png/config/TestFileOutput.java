package org.sterl.svg2png.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import org.sterl.svg2png.config.FileOutput;
import org.sterl.svg2png.config.OutputConfig;
import org.sterl.svg2png.util.FileUtil;

public class TestFileOutput {

    @Test
    public void testNaming() throws Exception {
        Path tmpDirFoooo = Files.createTempDirectory("Foooo");
        tmpDirFoooo.toFile().deleteOnExit();
        
        
        Path tmpDirBaaaar = Files.createTempDirectory("Baaaar");
        tmpDirBaaaar.toFile().deleteOnExit();

        OutputConfig cfg = OutputConfig.fromPath(getClass().getResource("/sample.svg").toString());
        assertEquals(1, cfg.getFiles().size());
        
        FileOutput outFile = cfg.getFiles().get(0);
        
        File srcFile = FileUtil.newFile(cfg.getInputFile());
        assertEquals("./sample.png", outFile.toOutputFile(srcFile, ".", null).toString());
        assertEquals("/tmp/sample.png", outFile.toOutputFile(srcFile, "/tmp", null).toString());
        System.out.println(outFile.toOutputFile(srcFile, "somesubdir", "hallo.png").toString());
        
        outFile.setDirectory(tmpDirFoooo.toAbsolutePath().toString());
        assertTrue(outFile.toOutputFile(srcFile, null, null).getAbsolutePath().endsWith("/sample.png"));
        assertTrue(outFile.toOutputFile(srcFile, null, "bar").getAbsolutePath().length() > 12);
        assertTrue(outFile.toOutputFile(srcFile, null, "bar").getAbsolutePath().endsWith("/bar.png"));
    }
    
    @Test
    public void testCommonName() {
        FileOutput fileOutput = new FileOutput();
        fileOutput.setName("ic_launcher.png");
        fileOutput.setDirectory("mipmap-xxhdpi");

        assertTrue(fileOutput.toOutputFile(new File("foo.svg"), null, null).getAbsolutePath().endsWith("mipmap-xxhdpi/ic_launcher.png"));
    }

}