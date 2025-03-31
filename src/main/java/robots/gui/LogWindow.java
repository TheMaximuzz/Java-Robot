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
    private ResourceBundle messages;

    public LogWindow(LogWindowSource logSource) {
        super("", true, true, true, true);
        messages = ResourceBundle.getBundle("messages");
        setTitle(messages.getString("logWindowTitle"));
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

    public void updateLanguage(ResourceBundle newMessages) {
        this.messages = newMessages;
        super.updateLanguage(messages);
        setTitle(messages.getString("logWindowTitle"));
        updateLogContent(); // Перестраиваем содержимое лога с новым языком
        revalidate();
        repaint();
    }
}