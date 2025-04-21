package robots.gui;

import robots.log.LogChangeListener;
import robots.log.LogEntry;
import robots.log.LogWindowSource;
import robots.log.Logger;
import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class LogWindow extends BaseInternalFrame implements LogChangeListener {
    private LogWindowSource m_logSource;
    private TextArea m_logContent;

    public LogWindow(LogWindowSource logSource) {
        super(true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        Logger.logWindowOpened();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public void dispose() {
        m_logSource.unregisterListener(this);
        Logger.logWindowClosed();
        super.dispose();
    }

    @Override
    protected boolean confirmClose() {
        return closeHelper.showConfirmationDialog(this, messages.getString("confirmCloseLogWindow"), messages.getString("confirmCloseTitle"));
    }

    @Override
    public String getTitleKey() {
        return "logWindowTitle"; // Ключ для заголовка окна логов
    }

    @Override
    public void updateLanguage(ResourceBundle newMessages) {
        super.updateLanguage(newMessages);
        updateLogContent(); // Обновляем содержимое лога с учетом нового языка
    }
}