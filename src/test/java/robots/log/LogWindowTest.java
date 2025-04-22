//package robots.log;
//
//import org.junit.jupiter.api.Test;
//import robots.gui.LogWindow;
//
//import java.util.Locale;
//import java.util.ResourceBundle;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class LogWindowTest {
//
//    @Test
//    public void testGetTitleKey() {
//        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
//        assertEquals("logWindowTitle", logWindow.getTitleKey());
//    }
//
//    @Test
//    public void testTitleLocalization() {
//        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
//        String key = logWindow.getTitleKey();
//
//        ResourceBundle enBundle = ResourceBundle.getBundle("messages", Locale.US);
//        assertEquals("Work Log", enBundle.getString(key));
//
//        ResourceBundle ruBundle = ResourceBundle.getBundle("messages", new Locale("ru", "RU"));
//        assertEquals("Протокол работы", ruBundle.getString(key));
//    }
//}