package org.sterl.svg2png;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.sterl.svg2png.TestUtil.svgPath;

import org.junit.jupiter.api.Test;
import org.sterl.svg2png.config.FileOutput;
import org.sterl.svg2png.config.OutputConfig;

class MainTest extends AbstractFileTest {

    /**
     * https://github.com/sterlp/svg2png/issues/21
     */
    @Test
    public void testBug21() throws Exception {
        // WHEN
        final OutputConfig config = Main.buildOutputConfig(new String[] {
                "-f",
                svgPath("sample.svg"),
                "-c",
                getClass().getResource("/Bug21-config.json").toURI().toString(),
                "-o", "./"
        });
        
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
}
