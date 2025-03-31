package robots.log;

import org.junit.jupiter.api.Test;
import robots.gui.LogWindow;

import static org.junit.jupiter.api.Assertions.*;

class TestLogs {

    // Тест 1 - Ограничение размера строк логов
    @Test
    void testLogQueueSizeLimit() {
        LogWindowSource logSource = new LogWindowSource(4);

        logSource.append(LogLevel.Debug, "1");
        logSource.append(LogLevel.Debug, "2");
        logSource.append(LogLevel.Debug, "3");
        logSource.append(LogLevel.Debug, "4");
        logSource.append(LogLevel.Debug, "5");

        assertEquals(4, logSource.message_size());

        Iterable<LogEntry> logs = logSource.all();
        int count = 0;
        for (LogEntry entry : logs) {
            count++;
            assertNotEquals("1", entry.getMessage());
        }
        assertEquals(4, count);
    }

    // Тест 2 - Утечка ресурсов - слушатели должны отвязываться
    @Test
    void testListenerUnregisterOnDispose() {
        LogWindowSource logSource = new LogWindowSource(4);

        // добавляем слушателя и убеждаемся, что реально добавили
        LogWindow logWindow = new LogWindow(logSource);
        assertEquals(1, logSource.m_listeners.size());

        logWindow.dispose();

        // реально ли слушатель отвязался
        assertEquals(0, logSource.m_listeners.size());
    }

    // Тест 3 - Проверка обновления лога при добавлении сообщения
    private static class TestLogChangeListener implements LogChangeListener {
        private int k = 0;

        @Override
        public void onLogChanged() {
            k++;
        }

        public int getK() {
            return k;
        }
    }

    @Test
    void testLogChangeNotification() {
        LogWindowSource logSource = new LogWindowSource(4);
        TestLogChangeListener listener = new TestLogChangeListener();

        logSource.registerListener(listener);
        logSource.append(LogLevel.Debug, "Какое-то сообщение");

        assertEquals(1, listener.getK());
    }
}