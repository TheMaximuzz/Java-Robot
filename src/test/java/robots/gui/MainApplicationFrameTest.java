package robots.gui;

import org.junit.jupiter.api.Test;
import java.util.Locale;
import java.util.ResourceBundle;
import static org.junit.jupiter.api.Assertions.*;

public class MainApplicationFrameTest {
    private String profileName;

    @Test
    public void testGetTitleKey() {
        MainApplicationFrame frame = new MainApplicationFrame(profileName);
        assertEquals("mainWindowTitle", frame.getTitleKey());
    }

    @Test
    public void testTitleLocalization() {

        MainApplicationFrame frame = new MainApplicationFrame(profileName);
        String key = frame.getTitleKey();


        ResourceBundle enBundle = ResourceBundle.getBundle("messages", Locale.US);
        assertEquals("Worm", enBundle.getString(key));

        ResourceBundle ruBundle = ResourceBundle.getBundle("messages", new Locale("ru", "RU"));
        assertEquals("Червячок", ruBundle.getString(key));
    }
}