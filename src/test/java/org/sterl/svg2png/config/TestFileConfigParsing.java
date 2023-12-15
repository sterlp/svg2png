package org.sterl.svg2png.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestFileConfigParsing {

    @Test
    public void testNameGeneration() {
        assertEquals("foo.png", FileOutput.buildName("foo", null, null, null));
        assertEquals("ic_foo.png", FileOutput.buildName("foo", null, "ic_", null));
        assertEquals("foo_24dp.png", FileOutput.buildName("foo", null, null, "_24dp"));
        assertEquals("ic_foo_24dp.png", FileOutput.buildName("foo", null, "ic_", "_24dp"));
        assertEquals("ic_foo_24dp.png", FileOutput.buildName("ic_foo_24dp", null, "ic_", "_24dp"));
        
        assertEquals("ic_foo.png", FileOutput.buildName("ic_foo", null, "ic_", null));
        
        assertEquals("bar.png", FileOutput.buildName("foo", "bar", null, null));
        assertEquals("ic_bar.png", FileOutput.buildName("foo", "bar", "ic_", null));
        assertEquals("ic_bar_24dp.png", FileOutput.buildName("foo", "bar", "ic_", "_24dp"));
        
        assertEquals("ic_bar_24dp.png", FileOutput.buildName("foo", "ic_bar_24dp", "ic_", "_24dp"));
    }
}
