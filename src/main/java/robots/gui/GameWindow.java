package robots.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class GameWindow extends BaseInternalFrame {
    private final GameVisualizer m_visualizer;

    public GameWindow() {
        super(true, true, true, true);
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

    @Override
    protected String getTitleKey() {
        return "gameWindowTitle"; // Ключ для заголовка окна игры
    }

    @Override
    public void updateLanguage(ResourceBundle newMessages) {
        super.updateLanguage(newMessages);
        // Дополнительные обновления интерфейса, если необходимо
    }
}