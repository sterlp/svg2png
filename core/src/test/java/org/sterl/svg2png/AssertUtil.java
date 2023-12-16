package org.sterl.svg2png;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class AssertUtil {
    public final static void assertEndsWith(String absolutePath, String value) {
        assertNotNull("Expected end with: " + value, absolutePath);
        assertTrue(absolutePath.equals(value) || absolutePath.endsWith(value),
                absolutePath + " doesn't end with: " + value);
    }
}
