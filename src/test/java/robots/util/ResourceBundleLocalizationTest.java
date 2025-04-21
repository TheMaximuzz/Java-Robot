package robots.util;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceBundleLocalizationTest {

    @Test
    public void testResourceBundleLoadsEnglishLocale() {
        // Устанавливаем локаль на английский
        Locale.setDefault(new Locale("en", "US"));
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

        // Проверяем, что строки соответствуют messages_en.properties
        assertEquals("Worm", bundle.getString("mainWindowTitle"));
        assertEquals("Game Field", bundle.getString("gameWindowTitle"));
        assertEquals("Work Log", bundle.getString("logWindowTitle"));
        assertEquals("Application started", bundle.getString("appStarted"));
        assertEquals("Are you sure you want to close the application?", bundle.getString("confirmCloseApp"));
        assertEquals("Yes", bundle.getString("yesButtonText"));
        assertEquals("No", bundle.getString("noButtonText"));
    }

    @Test
    public void testResourceBundleLoadsRussianLocale() {
        // Устанавливаем локаль на русский
        Locale.setDefault(new Locale("ru", "RU"));
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

        // Проверяем, что строки соответствуют messages_ru.properties
        assertEquals("Червячок", bundle.getString("mainWindowTitle"));
        assertEquals("Игровое поле", bundle.getString("gameWindowTitle"));
        assertEquals("Протокол работы", bundle.getString("logWindowTitle"));
        assertEquals("Приложение запущено", bundle.getString("appStarted"));
        assertEquals("Вы действительно хотите закрыть приложение?", bundle.getString("confirmCloseApp"));
        assertEquals("Да", bundle.getString("yesButtonText"));
        assertEquals("Нет", bundle.getString("noButtonText"));
    }

    @Test
    public void testResourceBundleFallbackToDefault() {
        // Устанавливаем неподдерживаемую локаль (например, французский)
        Locale.setDefault(new Locale("fr", "FR"));
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

        assertEquals("Worm", bundle.getString("mainWindowTitle"));
        assertEquals("Game Field", bundle.getString("gameWindowTitle"));
        assertEquals("Work Log", bundle.getString("logWindowTitle"));
        assertEquals("Application started", bundle.getString("appStarted"));
    }

    @Test
    public void testResourceBundleMissingKey() {
        // Устанавливаем локаль на английский
        Locale.setDefault(new Locale("en", "US"));
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

        // Проверяем, что при отсутствии ключа выбрасывается MissingResourceException
        assertThrows(MissingResourceException.class, () -> bundle.getString("nonExistentKey"));
    }
}