package org.sterl.svg2png;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.sterl.svg2png.AssertUtil.assertEndsWith;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sterl.svg2png.config.OutputConfig;

public class Svg2PngTest {

    File tmpDir;

    @Before
    public void before() {
        tmpDir = new File("./tmp1");
        tmpDir.mkdirs();
        tmpDir.deleteOnExit();
    }
    @After
    public void after() throws Exception {
        FileUtils.deleteDirectory(tmpDir);
    }

    @Test
    public void testConversionOfOneFile() throws Exception {
        OutputConfig cfg = OutputConfig.fromPath(svgPath("sample.svg"));

        List<File> convert = new Svg2Png(cfg).convert();
        System.out.println(convert);
        assertEquals(1, convert.size());
        assertTrue(convert.get(0).exists());
        assertTrue(convert.get(0).isFile());
        assertTrue(convert.get(0).getTotalSpace() > 1);

        convert.get(0).delete();
    }

    @Test
    public void testFileFormCmd() throws Exception {
        List<File> files = Main.run(new String[]{svgPath("sample.svg")});
        System.out.println(files);
        assertEquals(1, files.size());
        files.get(0).delete();
    }

    @Test
    public void testCliSingleFile() throws Exception {
        List<File> files = Main.run(new String[]{ "-f", svgPath("sample.svg")});
        System.out.println(files);
        assertEquals(1, files.size());
        assertEquals("sample.png", files.get(0).getName());
        files.get(0).delete();
    }

    @Test
    public void testNameConversionFile() throws Exception {
        List<File> files = Main.run(new String[]{
                "-n", "testConversion.png",
                "-f", svgPath("sample.svg")
        });
        System.out.println(files);
        assertEquals(1, files.size());
        assertEquals("testConversion.png", files.get(0).getName());
        files.get(0).delete();
    }

    @Test
    public void testNameMultipleFilesOutputs() throws Exception {
        List<File> files = Main.run(new String[]{
                "--android-launch",
                "-n","testConversion.png",
                "-f", svgPath("sample.svg")
        });
        assertEquals(6, files.size());
        for (File file : files) {
            assertEquals("testConversion.png", file.getName());
            file.deleteOnExit();
        }
    }

    @Test
    public void testNameMultipleFilesDefaultName() throws Exception {
        List<File> files = Main.run(new String[]{
                "--android-launch",
                "-f", svgPath("sample.svg")
        });
        assertEquals(6, files.size());
        files.forEach((f) -> f.deleteOnExit());

        for (File file : files) {
            assertEquals("sample.png", file.getName());
        }
    }

    @Test
    public void testConversionDirectory() throws Exception {
        OutputConfig cfg = OutputConfig.fromPath(new File(getClass().getResource("/svgfolder/sample.svg").toURI()).getParent());
        List<File> convert = new Svg2Png(cfg).convert();
        convert.stream().forEach(f -> f.deleteOnExit());

        assertEquals(2, convert.size());
        assertTrue("sample.png not found",
                convert.stream().filter(f -> "sample.png".equals(f.getName())).findFirst().isPresent());
        assertTrue("sample2.png not found",
                convert.stream().filter(f -> "sample2.png".equals(f.getName())).findFirst().isPresent());

        convert.stream().forEach(f -> f.delete());
    }

    @Test
    public void testWithConfig() throws Exception {
        List<File> files = Main.run(new String[] {
                "-f",
                svgPath("sample.svg"),
                "-c",
                getClass().getResource("/android.json").toURI().toString(),
                "-o",
                tmpDir.getAbsolutePath()
        });
        assertEquals(5, files.size());
        assertEndsWith(files.get(0).getAbsolutePath(), "/tmp1/drawable-xxxhdpi/sample.png".replace('/', File.separatorChar));
        assertEndsWith(files.get(1).getAbsolutePath(), "/tmp1/drawable-xxhdpi/sample.png".replace('/', File.separatorChar));
        assertEndsWith(files.get(2).getAbsolutePath(), "/tmp1/drawable-xhdpi/sample.png".replace('/', File.separatorChar));
        assertEndsWith(files.get(3).getAbsolutePath(), "/tmp1/drawable-hdpi/sample.png".replace('/', File.separatorChar));
        assertEndsWith(files.get(4).getAbsolutePath(), "/tmp1/drawable-mdpi/sample.png".replace('/', File.separatorChar));
    }

    @Test
    public void testInternalConfig() throws Exception {
        List<File> files = Main.run(new String[] {
                "-d",
                new File(getClass().getResource("/svgfolder/sample.svg").toURI()).getParent(),
                "-android-small",
                "-o",
                tmpDir.getAbsolutePath()
        });
        assertEquals(10, files.size());
    }

    @Test
    public void testIcon() throws Exception {
        List<File> files = Main.run(new String[] {
                "-d",
                new File(getClass().getResource("/svgfolder/sample.svg").toURI()).getParent(),
                "-android-icon",
                "-o",
                tmpDir.getAbsolutePath()
        });
        assertEquals(10, files.size());
    }
    
    @Test
    public void testConversionOfFileWithExternalResource() throws Exception {
        Svg2PngException ex = assertThrows(Svg2PngException.class, () ->
            Main.run(new String[] {
                    "--allow-external",
                    "-d",
                    new File(getClass().getResource("/svg-with-external-resource.svg").toURI()).getParent(),
                    "-android-icon",
                    "-o",
                    tmpDir.getAbsolutePath()
            })
        );
        assertTrue(ex.getMessage().contains("http://localhost:8080/svg"));

        ex = assertThrows(Svg2PngException.class, () ->
            Main.run(new String[] {
                    "-e",
                    "-d",
                    new File(getClass().getResource("/svg-with-external-resource.svg").toURI()).getParent(),
                    "-android-icon",
                    "-o",
                    tmpDir.getAbsolutePath()
            })
        );
        assertTrue(ex.getMessage().contains("http://localhost:8080/svg"));

    }

    @Test
    public void testConversionOfFileWithExternalResourceFails() throws Exception {
        Svg2PngException ex = assertThrows(Svg2PngException.class, () ->
            Main.run(new String[] {
                    "-d",
                    new File(getClass().getResource("/svg-with-external-resource.svg").toURI()).getParent(),
                    "-android-icon",
                    "-o",
                    tmpDir.getAbsolutePath()
            })
        );
        Throwable exception = ex.getCause();
        System.err.println(exception.getMessage());
        assertEquals(
            "The security settings do not allow any external resources to be referenced from the document",
            exception.getMessage());
    }

    @Test
    public void testForceWhite() throws Exception {
        // GIVEN
        final File normal = convertFile(new String[]{
            "-n", "normal.png",
            "-f", svgPath("sample.svg")
        });
        
        // WHEN
        final File white = convertFile(new String[]{
            "-n", "white.png",
            "-f", svgPath("sample.svg"),
            "-transparent-white"
        });

        // THEN they should not be equal
        assertNotEquals(normal.hashCode(), white.hashCode());
    }

    @Test
    public void testAlphaChannelRemoval() throws Exception {
        // GIVEN
        final File normal = convertFile(new String[]{
                "-n", "normal.png",
                "-f", svgPath("sample.svg")
        });

        // WHEN
        final File noAlpha = convertFile(new String[]{
                "-n", "no-alpha.png",
                "-f", svgPath("sample.svg"),
                "--no-alpha", "0077ff"
        });

        // THEN the file without alpha channel should be smaller
        assertTrue(normal.length() > noAlpha.length());
    }

    @Test
    public void testIosIcons() throws Exception {
        List<File> files = Main.run(new String[] {
                "-f", svgPath("sample.svg"),
                "--ios",
                "-n", "sample-ios",
                "-o", tmpDir.getAbsolutePath()
        });
        assertEquals(16, files.size());
    }

    @Test
    public void testRequireNameWithIos() throws Exception {
        Svg2PngException ex = assertThrows(Svg2PngException.class, () ->
                Main.run(new String[] {
                        "-f", svgPath("sample.svg"),
                        "--ios",
                        "-o", tmpDir.getAbsolutePath()
                })
        );
        Throwable exception = ex.getCause();
        System.err.println(exception.getMessage());
        assertEquals(
                "-n name must be specified when --ios is used.",
                exception.getMessage());
    }

    @Test
    public void testInvalidBackground() throws Exception {
        Svg2PngException ex = assertThrows(Svg2PngException.class, () ->
                Main.run(new String[] {
                        "-f", svgPath("sample.svg"),
                        "--no-alpha", "zzz",
                        "-o", tmpDir.getAbsolutePath()
                })
        );
        Throwable exception = ex.getCause();
        System.err.println(exception.getMessage());
        assertEquals(
                "Background must be specified as hex triplet e.g. --no-alpha 2a5c8b",
                exception.getMessage());
    }

    private String svgPath(String file) throws URISyntaxException {
        return getClass().getResource("/svgfolder/" + file).toString();
    }

    private static File convertFile(String[] args) throws Svg2PngException, URISyntaxException {
        List<File> files = Main.run(args);
        for (File file : files) file.deleteOnExit();
        assertEquals(1, files.size());
        return files.get(0);
    }
}
