package org.sterl.svg2png;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sterl.svg2png.TestUtil.svgPath;

import java.net.URISyntaxException;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.sterl.svg2png.config.FileOutput;
import org.sterl.svg2png.config.OutputConfig;

class MainConfigTest extends AbstractFileTest {

    /**
     * https://github.com/sterlp/svg2png/issues/21
     */
    @Test
    void testBug21() throws Exception {
        // WHEN
        final OutputConfig config = buildOutputConfigForJson("/Bug21-config.json");

        // THEN
        for (FileOutput f : config.getFiles()) {
            assertNotEquals(f.getWidth(), -1);
            assertNotEquals(f.getHeight(), -1);
        }
        assertEquals(config.getFiles().get(0).getWidth(), 16);
        assertEquals(config.getFiles().get(0).getHeight(), 16);

        assertEquals(config.getFiles().get(1).getWidth(), 38);
        assertEquals(config.getFiles().get(1).getHeight(), 32);
    }
    
    @Test
    void testWidthAndHightOverrides() throws Exception {
        // WHEN
        OutputConfig config = Main.buildOutputConfig(new String[] { 
                "-f", svgPath("sample.svg"), 
                "-c", getClass().getResource("/Bug21-config.json").toURI().toString(),
                "-w", "128",
                "-h", "128",
                "-o", "./" });
        
        // THEN
        for (FileOutput f : config.getFiles()) {
            assertEquals(f.getWidth(), 128);
            assertEquals(f.getHeight(), 128);
        }
    }

    @Test
    void testAndroid() throws Exception {
        // WHEN
        final OutputConfig config = buildOutputConfigForJson("/android.json");
        assertEquals(5, config.getFiles().size());
        assertEquals(192, config.getFiles().get(0).getHeight());
        assertEquals(192, config.getFiles().get(0).getWidth());
        assertEquals("drawable-xxxhdpi", config.getFiles().get(0).getDirectory());
    }

    @Test
    void testAndroidSmall() throws Exception {
        // WHEN
        final OutputConfig config = buildOutputConfigForJson("/android-small.json");
        assertEquals(5, config.getFiles().size());
        assertEquals(96, config.getFiles().get(0).getHeight());
        assertEquals(96, config.getFiles().get(0).getWidth());
        assertEquals("drawable-xxxhdpi", config.getFiles().get(0).getDirectory());
    }

    @Test
    void testAndroidIcon() throws Exception {
        // WHEN
        final OutputConfig config = buildOutputConfigForJson("/android-icon.json");
        // THEN

        assertEquals(5, config.getFiles().size());
        assertEquals(192, config.getFiles().get(0).getHeight());
        assertEquals(192, config.getFiles().get(0).getWidth());
        assertEquals("drawable-xxxhdpi", config.getFiles().get(0).getDirectory());
    }

    @Test
    void testIos() throws Exception {
        // WHEN
        final OutputConfig config = buildOutputConfigForJson("/ios.json");
        // THEN

        assertEquals(15, config.getFiles().size());
        assertEquals(20, config.getFiles().get(0).getHeight());
        assertEquals(20, config.getFiles().get(0).getWidth());
        assertEquals("ffffff", config.getNoAlpha());
        assertTrue(config.isContentsJson());
        assertNull(config.getFiles().get(0).getDirectory());
    }

    private OutputConfig buildOutputConfigForJson(String file) throws ParseException, URISyntaxException {
        return Main.buildOutputConfig(new String[] { 
                "-f", svgPath("sample.svg"), 
                "-c", getClass().getResource(file).toURI().toString(), 
                "-o", "./" });
    }
}
