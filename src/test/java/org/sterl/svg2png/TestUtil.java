package org.sterl.svg2png;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

public final class TestUtil {
    public final static void assertEndsWith(String absolutePath, String value) {
        assertNotNull("Expected end with: " + value, absolutePath);
        assertTrue(absolutePath.equals(value) || absolutePath.endsWith(value),
                absolutePath + " doesn't end with: " + value);
    }
    
    public final static String svgPath(String file) throws URISyntaxException {
        return TestUtil.class.getResource("/svgfolder/" + file).toString();
    }
}
