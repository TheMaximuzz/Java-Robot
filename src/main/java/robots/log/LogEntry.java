package robots.log;

import java.util.ResourceBundle;

public class LogEntry {
    private static ResourceBundle messages = ResourceBundle.getBundle("messages");
    private final LogLevel level;
    private final String messageKey;
    private final boolean isLocalized; // Флаг, указывающий, является ли messageKey ключом для локализации

    public LogEntry(LogLevel level, String messageKey, boolean isLocalized) {
        this.level = level;
        this.messageKey = messageKey;
        this.isLocalized = isLocalized;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        if (isLocalized) {
            return messages.getString(messageKey); // Если это ключ локализации, получаем строку из ResourceBundle
        } else {
            return messageKey; // Если это обычное сообщение, возвращаем его как есть
        }
    }

    public static void updateLanguage(ResourceBundle newMessages) {
        messages = newMessages;
    }
}