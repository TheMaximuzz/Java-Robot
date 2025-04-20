package robots.gui;

import org.junit.jupiter.api.Test;
import java.util.Locale;
import java.util.ResourceBundle;
import static org.junit.jupiter.api.Assertions.*;

public class GameWindowTest {

    @Test
    public void testGetTitleKey() {
        GameWindow gameWindow = new GameWindow();
        assertEquals("gameWindowTitle", gameWindow.getTitleKey());
    }

    @Test
    public void testTitleLocalization() {
        GameWindow gameWindow = new GameWindow();
        String key = gameWindow.getTitleKey();

        ResourceBundle enBundle = ResourceBundle.getBundle("messages", Locale.US);
        assertEquals("Game Field", enBundle.getString(key));

        ResourceBundle ruBundle = ResourceBundle.getBundle("messages", new Locale("ru", "RU"));
        assertEquals("Игровое поле", ruBundle.getString(key));
    }
}