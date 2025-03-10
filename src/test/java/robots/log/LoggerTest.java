package robots.log;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {

    @Test
    void testDebugLogging() {
        Logger.debug("Test message");
        assertNotNull(Logger.getDefaultLogSource(), "Log source should not be null");
    }

    @Test
    void testErrorLogging() {
        Logger.error("Error message");
        assertNotNull(Logger.getDefaultLogSource(), "Log source should not be null");
    }
}
