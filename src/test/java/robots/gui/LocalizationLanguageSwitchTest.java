package robots.gui;

import org.junit.jupiter.api.Test;
import java.util.Locale;
import java.util.ResourceBundle;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalizationLanguageSwitchTest {

    @Test
    public void testLanguageSwitch() {
        // Проверка русского языка
        Locale russianLocale = new Locale.Builder().setLanguage("ru").setRegion("RU").build();
        ResourceBundle bundleRu = ResourceBundle.getBundle("messages", russianLocale);
        assertEquals("Режим отображения", bundleRu.getString("lookAndFeelMenu"));
        assertEquals("Выйти", bundleRu.getString("exitItem"));

        // Проверка английского языка
        ResourceBundle bundleEn = ResourceBundle.getBundle("messages", Locale.US);
        assertEquals("View Mode", bundleEn.getString("lookAndFeelMenu"));
        assertEquals("Exit", bundleEn.getString("exitItem"));
    }
}