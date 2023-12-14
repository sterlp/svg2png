package org.sterl.svg2png;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sterl.svg2png.TestUtil.assertEndsWith;
import static org.sterl.svg2png.TestUtil.svgPath;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.sterl.svg2png.config.OutputConfig;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;

public class Svg2PngTest extends AbstractFileTest {

    @Test
    public void testConversionOfOneFile() throws Exception {
        // GIVEN
        OutputConfig cfg = OutputConfig.fromPath(svgPath("sample.svg"));
        cfg.applyOutputSize(128, 128);

        // WHEN
        List<File> convert = new Svg2Png(cfg).convert();
        convert.get(0).deleteOnExit();
        
        // THEN
        assertEquals(1, convert.size());
        assertTrue(convert.get(0).exists());
        assertTrue(convert.get(0).isFile());
        assertTrue(convert.get(0).getTotalSpace() > 1);
        
        assertEqualImages("normal-128x128.png", convert.get(0).getAbsolutePath());

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
                "-f", svgPath("sample.svg"),
                "-o", tmpDir.getAbsolutePath()
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
                "-f", svgPath("sample.svg"),
                "-o", tmpDir.getAbsolutePath()
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
                "-f", svgPath("sample.svg"),
                "-o", tmpDir.getAbsolutePath()
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
        assertTrue(convert.stream().filter(f -> "sample.png".equals(f.getName())).findFirst().isPresent(),
                "sample.png not found");
        assertTrue(convert.stream().filter(f -> "sample2.png".equals(f.getName())).findFirst().isPresent(),
                "sample2.png not found");

        convert.stream().forEach(f -> f.delete());
    }

    @Test
    public void testWithConfig() throws Exception {
        List<File> files = Main.run(new String[] {
                "-f",
                svgPath("sample.svg"),
                "-c",
                getClass().getResource("/android.json").toURI().toString(),
                "-o", tmpDir.getAbsolutePath()
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
                "-o", tmpDir.getAbsolutePath()
        });
        assertEquals(10, files.size());
    }

    @Test
    public void testIcon() throws Exception {
        List<File> files = Main.run(new String[] {
                "-d",
                new File(getClass().getResource("/svgfolder/sample.svg").toURI()).getParent(),
                "-android-icon",
                "-o", tmpDir.getAbsolutePath()
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
                    "-o", tmpDir.getAbsolutePath()
            })
        );
        assertTrue(ex.getMessage().contains("http://localhost:8080/svg"));

        ex = assertThrows(Svg2PngException.class, () ->
            Main.run(new String[] {
                    "-e",
                    "-d",
                    new File(getClass().getResource("/svg-with-external-resource.svg").toURI()).getParent(),
                    "-android-icon",
                    "-o", tmpDir.getAbsolutePath()
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
                    "-o", tmpDir.getAbsolutePath()
            })
        );
        // THEN
        assertTrue(ex.getMessage().contains("This is not allowed for security reasons and that resource will not be loaded"),
                "'This is not allowed for security reasons and that resource will not be loaded'\n not found in:\n" + ex.getMessage());
    }

    @Test
    public void testForceWhite() throws Exception {
        // GIVEN
        final File normal = convertFile(new String[]{
            "-n", "normal.png",
            "-f", svgPath("sample.svg"),
            "-o", tmpDir.getAbsolutePath()
        });
        
        // WHEN
        final File white = convertFile(new String[]{
            "-n", "white.png",
            "-f", svgPath("sample.svg"),
            "-transparent-white",
            "-o", tmpDir.getAbsolutePath()
        });

        // THEN they should not be equal
        assertNotEquals(normal.hashCode(), white.hashCode());
        assertTrue(normal.length() < white.length(), "white background file should be bigger");
    }

    @Test
    public void testCustomAlphaChannel() throws Exception {
        // GIVEN
        final File normal = convertFile(new String[]{
                "-n", "normal.png",
                "-w", "128",
                "-h", "128",
                "-f", svgPath("sample.svg"),
                "-o", tmpDir.getAbsolutePath()
        });

        // WHEN
        final File noAlpha = convertFile(new String[]{
                "-n", "no-alpha.png",
                "-w", "128",
                "-h", "128",
                "-f", svgPath("sample.svg"),
                "--no-alpha", "0077ff",
                "-o", tmpDir.getAbsolutePath()
        });

        // THEN the file without alpha channel should be smaller
        assertNotEquals(normal.hashCode(), noAlpha.hashCode());
        assertTrue(normal.length() > noAlpha.length(), "Removed alpha file should be smaller");
    }

    @Test
    public void testIosIcons() throws Exception {
        // WHEN
        List<File> files = Main.run(new String[] {
                "-f", svgPath("sample.svg"),
                "--ios",
                "-n", "sample-ios",
                "-o", tmpDir.getAbsolutePath()
        });
        // THEN
        assertEquals(16, files.size());
        
        // AND
        File contentJson = new File(tmpDir.getAbsolutePath() + "/Contents.json");
        assertTrue(contentJson.exists() && contentJson.isFile(),
                "Content.json doesn't exists " + contentJson.getAbsolutePath());
        
        String contentsJsonString = IOUtils.toString(new FileInputStream(contentJson), StandardCharsets.UTF_8);
        assertTrue(contentsJsonString.contains("sample-ios"));
        assertFalse(contentsJsonString.contains("?PREFIX?"));
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
    public void testInvalidNoAlphaBackground() throws Exception {
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
    
    @Test
    public void testCustomBackground() throws Exception {
        // WHEN
        final File subject = convertFile(new String[]{
                "-w", "128",
                "-h", "128",
                "-f", svgPath("sample.svg"),
                "--background-color", "0077ff",
                "-o", tmpDir.getAbsolutePath()
        });

        // THEN
        assertEqualImages("background-128x128-0077FF.png", subject.getAbsolutePath());
    }

    private static void assertEqualImages(String source, String actual) {
        BufferedImage expectedImage = ImageComparisonUtil.readImageFromResources(source);
        BufferedImage actualImage = ImageComparisonUtil.readImageFromResources(actual);

        // Create ImageComparison object and compare the images.
        ImageComparisonResult imageComparisonResult = new ImageComparison(expectedImage, actualImage).compareImages();
        
        // Check the result
        assertEquals(ImageComparisonState.MATCH, imageComparisonResult.getImageComparisonState(),
                "Expected " + source + " to be " + actual);
    }

    private static File convertFile(String[] args) throws Svg2PngException, URISyntaxException {
        List<File> files = Main.run(args);
        for (File file : files) file.deleteOnExit();
        assertEquals(1, files.size());
        return files.get(0);
    }
}
