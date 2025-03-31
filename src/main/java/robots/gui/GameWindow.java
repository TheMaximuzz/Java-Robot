package robots.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class GameWindow extends BaseInternalFrame {
    private final GameVisualizer m_visualizer;
    private ResourceBundle messages;

    public GameWindow() {
        super("", true, true, true, true);
        messages = ResourceBundle.getBundle("messages");
        setTitle(messages.getString("gameWindowTitle"));
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    protected boolean confirmClose() {
        return closeHelper.showConfirmationDialog(this, messages.getString("confirmCloseGameWindow"), messages.getString("confirmCloseTitle"));
    }

    public void updateLanguage(ResourceBundle newMessages) {
        this.messages = newMessages;
        super.updateLanguage(messages);
        setTitle(messages.getString("gameWindowTitle"));
        revalidate();
        repaint();
    }
}