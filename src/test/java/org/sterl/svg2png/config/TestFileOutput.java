package org.sterl.svg2png.config;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sterl.svg2png.AssertUtil.assertEndsWith;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.sterl.svg2png.util.FileUtil;

public class TestFileOutput {

    @Test
    public void testNaming() throws Exception {
        Path tmpDirFoooo = Files.createTempDirectory("Foooo");
        tmpDirFoooo.toFile().deleteOnExit();
        
        
        Path tmpDirBaaaar = Files.createTempDirectory("Baaaar");
        tmpDirBaaaar.toFile().deleteOnExit();

        OutputConfig cfg = OutputConfig.fromPath(getClass().getResource("/svgfolder/sample.svg").toString());
        assertEquals(1, cfg.getFiles().size());
        
        FileOutput outFile = cfg.getFiles().get(0);
        
        File srcFile = FileUtil.newFile(cfg.getInputFile());
        assertEquals("./sample.png".replace('/', File.separatorChar), outFile.toOutputFile(srcFile, ".", null).toString());
        assertEquals("/tmp/sample.png".replace('/', File.separatorChar), outFile.toOutputFile(srcFile, "/tmp", null).toString());
        System.out.println(outFile.toOutputFile(srcFile, "somesubdir", "hallo.png").toString());
        
        outFile.setDirectory(tmpDirFoooo.toAbsolutePath().toString());
        assertEndsWith(outFile.toOutputFile(srcFile, null, null).getAbsolutePath(), "/sample.png".replace('/', File.separatorChar));
        assertTrue(outFile.toOutputFile(srcFile, null, "bar").getAbsolutePath().length() > 12);
        assertEndsWith(outFile.toOutputFile(srcFile, null, "bar").getAbsolutePath(), "/bar.png".replace('/', File.separatorChar));
    }
    
    @Test
    public void testCommonName() {
        FileOutput fileOutput = new FileOutput();
        fileOutput.setName("ic_launcher.png");
        fileOutput.setDirectory("mipmap-xxhdpi");

        assertEndsWith(fileOutput.toOutputFile(new File("foo.svg"), null, null).getAbsolutePath(), "mipmap-xxhdpi/ic_launcher.png".replace('/', File.separatorChar));
    }

}