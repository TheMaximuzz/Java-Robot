package robots.log;

import java.util.ArrayList;
import java.util.List;

public class LogWindowSource {
    private final int m_iQueueLength;
    private final List<LogEntry> m_messages;
    public final List<LogChangeListener> m_listeners;

    public LogWindowSource(int iQueueLength) {
        m_iQueueLength = iQueueLength;
        m_messages = new ArrayList<>(iQueueLength);
        m_listeners = new ArrayList<>();
    }

    public void registerListener(LogChangeListener listener) {
        m_listeners.add(listener);
    }

    public void unregisterListener(LogChangeListener listener) {
        m_listeners.remove(listener);
    }

    public void append(LogLevel logLevel, String strMessage) {
        // Проверяем, является ли strMessage ключом локализации
        boolean isLocalized = isLocalizationKey(strMessage);
        LogEntry entry = new LogEntry(logLevel, strMessage, isLocalized);
        m_messages.add(entry);
        if (m_messages.size() > m_iQueueLength) {
            m_messages.remove(0);
        }
        for (LogChangeListener listener : m_listeners) {
            listener.onLogChanged();
        }
    }

    public int message_size() {
        return m_messages.size();
    }

    public Iterable<LogEntry> all() {
        return new ArrayList<>(m_messages);
    }

    // Метод для проверки, является ли строка ключом локализации
    private boolean isLocalizationKey(String message) {
        // Список известных ключей локализации
        String[] localizationKeys = {
                "appStarted", "logWindowOpened", "logWorking", "logWindowClosed", "newLogMessage"
        };
        for (String key : localizationKeys) {
            if (key.equals(message)) {
                return true;
            }
        }
        return false;
    }
}