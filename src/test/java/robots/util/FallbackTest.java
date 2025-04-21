package robots.util;

import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FallbackTest {

    //подключаем несуществующий язык => язык == языку системы
    @Test
    public void testFallbackToSystemLanguage() {
        // Сохраняем текущую локаль системы
        Locale systemLocale = Locale.getDefault();

        try {
            // Устанавливаем тестовую локаль системы (например, русскую)
            Locale.setDefault(new Locale("ru", "RU"));

            ResourceBundle systemBundle = ResourceBundle.getBundle("messages");
            ResourceBundle bundle = ResourceBundle.getBundle("messages", new Locale("fr", "FR"));

            assertEquals(
                    systemBundle.getString("lookAndFeelMenu"),
                    bundle.getString("lookAndFeelMenu")
            );

            assertEquals(
                    systemBundle.getString("exitItem"),  // Исправленный ключ
                    bundle.getString("exitItem")
            );
        } finally {
            // Восстанавливаем оригинальную локаль
            Locale.setDefault(systemLocale);
        }
    }
}