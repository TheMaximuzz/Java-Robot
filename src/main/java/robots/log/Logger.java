package robots.log;

import java.util.ResourceBundle;

public class Logger {
    private static LogWindowSource logSource = new LogWindowSource(100);
    private static ResourceBundle messages = ResourceBundle.getBundle("messages");

    public static void appStarted() {
        logSource.append(LogLevel.Info, "appStarted");
    }

    public static void logWorking() {
        logSource.append(LogLevel.Info, "logWorking");
    }

    public static void logWindowOpened() {
        logSource.append(LogLevel.Info, "logWindowOpened");
    }

    public static void logWindowClosed() {
        logSource.append(LogLevel.Info, "logWindowClosed");
    }

    public static void debug(String message) {
        logSource.append(LogLevel.Debug, message);
    }

    public static LogWindowSource getDefaultLogSource() {
        return logSource;
    }

    public static void updateLanguage(ResourceBundle newMessages) {
        messages = newMessages;
        LogEntry.updateLanguage(newMessages);
    }
}