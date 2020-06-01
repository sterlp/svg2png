package org.sterl.svg2png;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public final class AssertUtil {
    public final static void assertEndsWith(String absolutePath, String value) {
        assertNotNull("Expected end with: " + value, absolutePath);
        assertTrue(absolutePath + " doesn't end with: " + value, absolutePath.equals(value) || absolutePath.endsWith(value));
    }
}
