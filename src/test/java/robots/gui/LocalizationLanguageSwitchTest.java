package robots.gui;

import org.junit.Test;
import java.util.Locale;
import java.util.ResourceBundle;
import static org.junit.Assert.assertEquals;

public class LocalizationLanguageSwitchTest {

    @Test
    public void testLanguageSwitch() {
        // Проверка русского языка
        ResourceBundle bundleRu = ResourceBundle.getBundle("messages", new Locale("ru", "RU"));
        assertEquals("Режим отображения", bundleRu.getString("lookAndFeelMenu"));
        assertEquals("Выйти", bundleRu.getString("exitItem"));

        // Проверка английского языка
        ResourceBundle bundleEn = ResourceBundle.getBundle("messages", Locale.US);
        assertEquals("View Mode", bundleEn.getString("lookAndFeelMenu"));
        assertEquals("Exit", bundleEn.getString("exitItem"));
    }

}
